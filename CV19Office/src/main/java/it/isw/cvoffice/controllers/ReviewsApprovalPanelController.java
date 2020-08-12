package it.isw.cvoffice.controllers;

import com.google.gson.JsonObject;
import it.isw.cvoffice.controllers.enumerations.AlertType;
import it.isw.cvoffice.dao.DAOFactory;
import it.isw.cvoffice.dao.NotificationDAO;
import it.isw.cvoffice.dao.ReviewDAO;
import it.isw.cvoffice.models.ObservableReview;
import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.models.Review;
import it.isw.cvoffice.utils.Examiner;
import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import it.isw.cvoffice.utils.aws.lambda.callbacks.LambdaResultHandler;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.lambda.model.LambdaException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;


public class ReviewsApprovalPanelController extends Controller {

    private static final Map<String, String> NOTIFICATION_MODELS;

    @FXML private Button buttonPreviousImage;
    @FXML private Button buttonNextImage;
    @FXML private Button buttonApprove;
    @FXML private Button buttonDisapprove;
    @FXML private RadioButton radioButtonAutopilot;
    @FXML private ImageView iconButtonPreviousImage;
    @FXML private ImageView iconButtonNextImage;
    @FXML private ImageView imageSlider;
    @FXML private TextArea textAreaReviewTitle;
    @FXML private TextArea textAreaReviewDescription;
    @FXML private Label labelCurrentImageCount;

    private Thread autopilotThread;
    private int currentImageIndex;
    private int totalImagesCount;
    private final ReviewsManagementController reviewsManagementController;
    private final ObservableReview observableReview = new ObservableReview();


    static {
        NOTIFICATION_MODELS = new HashMap<>();
        NOTIFICATION_MODELS.put("APPROVED", "utils/approved_notification.txt");
        NOTIFICATION_MODELS.put("DISAPPROVED", "utils/disapproved_notification.txt");
        NOTIFICATION_MODELS.put("REVALUED_APPROVED", "utils/revalued_approved_notification.txt");
        NOTIFICATION_MODELS.put("REVALUED_DISAPPROVED", "utils/revalued_disapproved_notification.txt");
    }


    public ReviewsApprovalPanelController(ReviewsManagementController reviewsManagementController) {
        this.reviewsManagementController = reviewsManagementController;
    }

    @Override
    public void initialize() {
        listenToEvents();
        setDisableReviewsApprovalPanelControls(true);
    }

    @Override
    public void listenToEvents() {
        /* CLICK */
        buttonPreviousImage.setOnAction(this::onButtonPreviousImageClicked);
        buttonNextImage.setOnAction(this::onButtonNextImageClicked);
        radioButtonAutopilot.setOnAction(this::onRadioButtonAutopilotClicked);
        buttonApprove.setOnAction(this::onButtonApproveClicked);
        buttonDisapprove.setOnAction(this::onButtonDisapproveClicked);
        /* HOVER IN */
        buttonPreviousImage.setOnMouseEntered(event -> {
            onButtonPreviousImageMouseMoved(event, true);
        });
        buttonNextImage.setOnMouseEntered(event -> {
            onButtonNextImageMouseMoved(event, true);
        });
        /* HOVER OUT */
        buttonPreviousImage.setOnMouseExited(event -> {
            onButtonPreviousImageMouseMoved(event, false);
        });
        buttonNextImage.setOnMouseExited(event -> {
            onButtonNextImageMouseMoved(event, false);
        });
        /* PRESSED */
        buttonPreviousImage.setOnMousePressed(this::onButtonPreviousMousePressed);
        buttonNextImage.setOnMousePressed(this::onButtonNextMousePressed);
        /* RELEASED */
        buttonPreviousImage.setOnMouseReleased(this::onButtonPreviousMouseReleased);
        buttonNextImage.setOnMouseReleased(this::onButtonNextMouseReleased);
        /* CHANGED */
        observableReview.setOnObservableValueChangedListener(this::onObservableReviewValueChanged);
    }

    private void onButtonPreviousImageClicked(ActionEvent event) {
        currentImageIndex = (currentImageIndex-1 < 0) ? totalImagesCount-1 : currentImageIndex-1;
        showImage();
    }

    private void onButtonNextImageClicked(ActionEvent event) {
        currentImageIndex = (currentImageIndex+1 == totalImagesCount) ? 0 : currentImageIndex+1;
        showImage();
    }

    private void onButtonApproveClicked(ActionEvent event) {
        updateReviewsStatus("approved");
    }

    private void onButtonDisapproveClicked(ActionEvent event) {
        updateReviewsStatus("disapproved");
    }

    private void onRadioButtonAutopilotClicked(ActionEvent event) {
        if(radioButtonAutopilot.isSelected()) {
            radioButtonAutopilot.setText("Disable autopilot");
            autopilotThread = startAutopilot();
        } else {
            autopilotThread.interrupt();
            radioButtonAutopilot.setText("Enable autopilot");
        }
    }

    private void onButtonPreviousImageMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconButtonPreviousImage.setImage(getIcon("IC_ARROW_LEFT_HIGHLIGHTED"));
        } else {
            iconButtonPreviousImage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
        }
    }

    private void onButtonNextImageMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconButtonNextImage.setImage(getIcon("IC_ARROW_RIGHT_HIGHLIGHTED"));
        } else {
            iconButtonNextImage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
        }
    }

    private void onButtonPreviousMousePressed(MouseEvent event) {
        iconButtonPreviousImage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
    }

    private void onButtonPreviousMouseReleased(MouseEvent event) {
        if(buttonPreviousImage.isHover()) {
            iconButtonPreviousImage.setImage(getIcon("IC_ARROW_LEFT_HIGHLIGHTED"));
        } else {
            iconButtonPreviousImage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
        }
    }

    private void onButtonNextMousePressed(MouseEvent event) {
        iconButtonNextImage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
    }

    private void onButtonNextMouseReleased(MouseEvent event) {
        if(buttonNextImage.isHover()) {
            iconButtonNextImage.setImage(getIcon("IC_ARROW_RIGHT_HIGHLIGHTED"));
        } else {
            iconButtonNextImage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
        }
    }

    private void onObservableReviewValueChanged(Review newValue) {
        if(newValue != null) {
            currentImageIndex = 0;
            totalImagesCount = 0;
            setDisableReviewsApprovalPanelControls(false);
            if(newValue.getStatus() != null && newValue.getStatus().equals("approved")) {
                buttonApprove.setDisable(true);
            } else if(newValue.getStatus() != null && newValue.getStatus().equals("disapproved")) {
                buttonDisapprove.setDisable(true);
            }
            imageSlider.setImage(getIcon("IC_MAGNIFYING_GLASS_LOADING_SPINNER"));
            textAreaReviewTitle.setText(newValue.getTitle());
            textAreaReviewDescription.setText(newValue.getDescription());
            if(newValue.getImages() != null && newValue.getImages().length != 0) {
                totalImagesCount = newValue.getImages().length;
                showImage();
            } else {
                labelCurrentImageCount.setText("0 - 0");
                imageSlider.setImage(getIcon("IC_GHOST"));
                buttonNextImage.setDisable(true);
                buttonPreviousImage.setDisable(true);
            }
        } else {
            textAreaReviewTitle.clear();
            textAreaReviewDescription.clear();
            labelCurrentImageCount.setText("0 - 0");
            imageSlider.setImage(getIcon("IC_GHOST"));
            setDisableReviewsApprovalPanelControls(true);
        }
    }

    private void setDisableReviewsApprovalPanelControls(boolean disable) {
        buttonApprove.setDisable(disable);
        buttonDisapprove.setDisable(disable);
        buttonNextImage.setDisable(disable);
        buttonPreviousImage.setDisable(disable);
    }

    protected void setObservableReview(Review review) {
        observableReview.setValue(review);
    }

    private Thread startAutopilot() {
        autopilotThread = new Thread(() -> {
            ReviewsExplorerController reviewsExplorerController = reviewsManagementController.getReviewsExplorerController();
            while(!(Thread.currentThread().isInterrupted())) {
                Platform.runLater(() -> {
                    if(!autopilotThread.isInterrupted()) {
                        int reviewsCount = reviewsExplorerController.getObservableReviewsSize();
                        if(reviewsCount > 0) {
                            int selectedReviewIndex = reviewsExplorerController.getSelectedRowReviewIndex();
                            if(selectedReviewIndex < 0) {
                                reviewsExplorerController.selectRowReview(0);
                            }
                            Review selectedReview = reviewsExplorerController.getSelectedRowReview();
                            observableReview.setValue(selectedReview);
                            if(Examiner.examine(selectedReview.getTitle() + " " + selectedReview.getDescription())) {
                                if(buttonDisapprove.isDisabled()) {
                                    if(reviewsCount == selectedReviewIndex+1) {
                                        radioButtonAutopilot.fire();
                                    } else {
                                        reviewsExplorerController.selectRowReview(selectedReviewIndex+1);
                                        reviewsExplorerController.scrollToRowReviewIndex(selectedReviewIndex+1);
                                    }
                                } else {
                                    buttonDisapprove.fire();
                                }
                            } else {
                                if(selectedReview.getImages() != null && selectedReview.getImages().length > 0) {
                                    reviewsManagementController.showAlert(
                                            AlertType.WARNING,
                                            "Operator is required to verify the validity of the images"
                                    );
                                    radioButtonAutopilot.fire();
                                } else {
                                    if(buttonApprove.isDisabled()) {
                                        if(reviewsCount == selectedReviewIndex+1) {
                                            radioButtonAutopilot.fire();
                                        } else {
                                            reviewsExplorerController.selectRowReview(selectedReviewIndex+1);
                                            reviewsExplorerController.scrollToRowReviewIndex(selectedReviewIndex+1);
                                        }
                                    } else {
                                        buttonApprove.fire();
                                    }
                                }
                            }
                        } else {
                            radioButtonAutopilot.fire();
                        }
                    }
                 });
                try {
                    Thread.sleep(1500);
                } catch (InterruptedException e) {
                    return;
                }
            }
        });
        autopilotThread.start();
        return autopilotThread;
    }

    private void updateReviewsStatus(String status) {
        setDisableReviewsApprovalPanelControls(true);
        ReviewDAO reviewDAO = DAOFactory.getReviewDAO();
        reviewDAO.updateReviewStatus(String.valueOf(observableReview.getValue().getReviewId()), status, new LambdaResultHandler() {

            @Override
            public void onSuccess(JsonObject result) {
                handleUpdateReviewsStatusSuccess(reviewDAO.parseResult(result).get(0), observableReview.getValue().getStatus());
            }

            @Override
            public void onFailure(@NotNull Exception exception) {
                handleUpdateReviewsStatusFailure(exception);
            }

        });
    }

    private void handleUpdateReviewsStatusSuccess(Review reviewUpdated, String oldStatus) {
        Platform.runLater(() -> {
            reviewsManagementController.getReviewsExplorerController().updateRowReview(observableReview.getValue(), reviewUpdated);
            if(reviewUpdated.getStatus().equals("approved")) {
                reviewsManagementController.showAlert(AlertType.APPROVED, "The review has been approved");
            } else {
                reviewsManagementController.showAlert(AlertType.DISAPPROVED, "The review has been disapproved");
            }
            sendNotification(reviewUpdated, oldStatus);
        });
    }

    private void handleUpdateReviewsStatusFailure(Exception exception) {
        Platform.runLater(() -> {
            setDisableReviewsApprovalPanelControls(false);
            reviewsManagementController.showAlert(AlertType.ERROR, "An unknown error occurred, please try again");
            if(autopilotThread != null && !(autopilotThread.isInterrupted())) {
                radioButtonAutopilot.fire();
            }
            if(exception.getClass() == LambdaException.class) {
                Authenticator authenticator = Authenticator.builder(AuthFlowType.REFRESH_TOKEN_AUTH);
                Map<String, String> authParameters = new HashMap<>();
                authParameters.put("REFRESH_TOKEN", Operator.getInstance().getAuthenticationBundle().refreshToken());
                authenticator.authenticate(authParameters, new AuthenticationHandler() {

                    @Override
                    public void onAuthenticationSuccess(AuthenticationResultType authenticationBundle, Credentials credentials) {
                        Operator.getInstance().setAuthenticationBundle(authenticationBundle);
                        Operator.getInstance().setCredentials(credentials);
                    }

                });
            }
        });
    }

    private void sendNotification(@NotNull Review reviewUpdated, String oldStatus) {
        String title = "";
        String description  = "";
        InputStream inputStream = ReviewsApprovalPanelController.class.getClassLoader().getResourceAsStream(
                NOTIFICATION_MODELS.get(getNotificationType(oldStatus, reviewUpdated.getStatus()))
        );
        if(inputStream != null) {
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            try {
                title = bufferedReader.readLine();
                description = bufferedReader.readLine();
                bufferedReader.close();
            } catch (IOException e) {
                return;
            }
        }
        description = description.replace("$1", reviewUpdated.getReviewerUsername());
        description = description.replace("$2", reviewUpdated.getAccommodationFacilityName());
        description = description.replace("$3", reviewUpdated.getPublicationDate());
        NotificationDAO notificationDAO = DAOFactory.getNotificationDAO();
        notificationDAO.postNotification(
                String.valueOf(reviewUpdated.getReviewId()),
                reviewUpdated.getReviewerId(),
                title,
                description,
                (reviewUpdated.getStatus().equals("approved")) ? "positive" : "negative",
                null
        );
    }

    private @NotNull String getNotificationType(@NotNull String oldStatus, @NotNull String newStatus) {
        if(oldStatus.equals("pending")) {
            return (newStatus.equals("approved")) ? "APPROVED" : "DISAPPROVED";
        } else if(oldStatus.equals("approved")) {
            return "REVALUED_DISAPPROVED";
        } else {
            return "REVALUED_APPROVED";
        }
    }

    private void showImage() {
        new Thread(() -> {
            try {
                Image image = new Image(observableReview.getValue().getImages()[currentImageIndex], 370, 235, true, true);
                Platform.runLater(() -> {
                    labelCurrentImageCount.setText((currentImageIndex + 1) + " - " + totalImagesCount);
                    if (image.getException() != null) {
                        imageSlider.setImage(getIcon("IC_WARNING"));
                    } else {
                        ImageView tempImageView = new ImageView();
                        tempImageView.setPreserveRatio(true);
                        tempImageView.setImage(image);
                        Rectangle clip = new Rectangle(
                                image.getWidth(), image.getHeight()
                        );
                        clip.setArcWidth(15);
                        clip.setArcHeight(15);
                        tempImageView.setClip(clip);
                        SnapshotParameters parameters = new SnapshotParameters();
                        parameters.setFill(Color.TRANSPARENT);
                        WritableImage roundedImage = tempImageView.snapshot(parameters, null);
                        imageSlider.setImage(roundedImage);
                    }
                });
            } catch (Exception exception) {
                Platform.runLater(() -> imageSlider.setImage(getIcon("IC_WARNING")));
            }
        }).start();
    }

}