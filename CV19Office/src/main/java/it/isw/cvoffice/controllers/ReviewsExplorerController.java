package it.isw.cvoffice.controllers;

import com.google.gson.JsonObject;
import it.isw.cvoffice.controllers.enumerations.AlertType;
import it.isw.cvoffice.dao.DAOFactory;
import it.isw.cvoffice.dao.ReviewDAO;
import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.models.Review;
import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import it.isw.cvoffice.utils.aws.lambda.callbacks.LambdaResultHandler;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.lambda.model.LambdaException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class ReviewsExplorerController extends Controller {

    @FXML private Button buttonPreviousReviewsPage;
    @FXML private Button buttonNextReviewsPage;
    @FXML private Button buttonFilters;
    @FXML private ComboBox<Integer> comboBoxMaxRows;
    @FXML private ImageView iconButtonPreviousReviewsPage;
    @FXML private ImageView iconButtonNextReviewsPage;
    @FXML private ImageView iconButtonFilters;
    @FXML private Label labelCurrentReviewsPageCount;
    @FXML private TableView<Review> tableReviews;
    @FXML private TableColumn<Review, Integer> columnReviewId;
    @FXML private TableColumn<Review, Integer> columnAccommodationFacilityId;
    @FXML private TableColumn<Review, String> columnUserId;
    @FXML private TableColumn<Review, String> columnStatus;
    @FXML private TableColumn<Review, Integer> columnRating;
    @FXML private TableColumn<Review, String> columnAccommodationFacilityName;
    @FXML private TableColumn<Review, String> columnUsername;
    @FXML private TableColumn<Review, String> columnPublicationDate;
    @FXML private TableColumn<Review, String> columnDateOfStay;
    @FXML private TableColumn<Review, String> columnDeleteDate;
    @FXML private TableColumn<Review, Integer> columnLikes;
    @FXML private TableColumn<Review, Integer> columnDislikes;

    private final ReviewsManagementController reviewsManagementController;
    private final Map<String, String> filters = new HashMap<>();
    private ObservableList<Review> observableReviews = FXCollections.emptyObservableList();
    private int currentReviewsPageIndex;
    private int totalReviewsPagesCount;
    private int fetchLimit = 10;


    public ReviewsExplorerController(ReviewsManagementController reviewsManagementController) {
        this.reviewsManagementController = reviewsManagementController;
    }

    @Override
    public void initialize() {
        setColumnCellValueFactory();
        comboBoxMaxRows.setItems(FXCollections.observableArrayList(10, 20, 30, 40, 50));
        comboBoxMaxRows.getSelectionModel().selectFirst();
        listenToEvents();
        fetchReviews(true);
    }

    @Override
    public void listenToEvents() {
        /* CLICK */
        buttonPreviousReviewsPage.setOnAction(this::onButtonPreviousReviewsPageClicked);
        buttonNextReviewsPage.setOnAction(this::onButtonNextReviewsPageClicked);
        buttonFilters.setOnAction(this::onButtonFiltersClicked);
        /* HOVER IN */
        buttonPreviousReviewsPage.setOnMouseEntered(event -> {
            onButtonPreviousReviewsPageMouseMoved(event, true);
        });
        buttonNextReviewsPage.setOnMouseEntered(event -> {
            onButtonNextReviewsPageMouseMoved(event, true);
        });
        buttonFilters.setOnMouseEntered(event -> {
            onButtonFiltersMouseMoved(event, true);
        });
        /* HOVER OUT */
        buttonPreviousReviewsPage.setOnMouseExited(event -> {
            onButtonPreviousReviewsPageMouseMoved(event, false);
        });
        buttonNextReviewsPage.setOnMouseExited(event -> {
            onButtonNextReviewsPageMouseMoved(event, false);
        });
        buttonFilters.setOnMouseExited(event -> {
            onButtonFiltersMouseMoved(event, false);
        });
        /* PRESSED */
        buttonPreviousReviewsPage.setOnMousePressed(this::onButtonPreviousReviewsPageMousePressed);
        buttonNextReviewsPage.setOnMousePressed(this::onButtonNextReviewsPageMousePressed);
        /* RELEASED */
        buttonPreviousReviewsPage.setOnMouseReleased(this::onButtonPreviousReviewsPageMouseReleased);
        buttonNextReviewsPage.setOnMouseReleased(this::onButtonNextReviewsPageMouseReleased);
        /* SELECTED */
        comboBoxMaxRows.setOnAction(this::onComboBoxMaxRowsItemSelected);
        tableReviews.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            onTableReviewsRowSelected(newValue);
        });
    }

    private void onButtonPreviousReviewsPageClicked(ActionEvent event) {
        fetchReviews(false);
    }

    private void onButtonNextReviewsPageClicked(ActionEvent event) {
        fetchReviews(true);
    }

    private void onButtonFiltersClicked(ActionEvent event) {
        if(filters.get("status") != null) {
            iconButtonFilters.setImage(getIcon("IC_FILTERS_DEFAULT"));
            filters.remove("status");
        } else {
            iconButtonFilters.setImage(getIcon("IC_FILTERS_HIGHLIGHTED"));
            filters.put("status", "pending");
        }
        currentReviewsPageIndex = 0;
        totalReviewsPagesCount = 0;
        fetchReviews(true);
    }

    private void onComboBoxMaxRowsItemSelected(ActionEvent event) {
        fetchLimit = comboBoxMaxRows.getSelectionModel().getSelectedItem();
        currentReviewsPageIndex = 0;
        totalReviewsPagesCount = 0;
        fetchReviews(true);
    }

    private void onTableReviewsRowSelected(Review review) {
        reviewsManagementController.getReviewsApprovalPanelController().setObservableReview(review);
    }

    private void onButtonPreviousReviewsPageMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconButtonPreviousReviewsPage.setImage(getIcon("IC_ARROW_LEFT_HIGHLIGHTED"));
        } else {
            iconButtonPreviousReviewsPage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
        }
    }

    private void onButtonNextReviewsPageMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconButtonNextReviewsPage.setImage(getIcon("IC_ARROW_RIGHT_HIGHLIGHTED"));
        } else {
            iconButtonNextReviewsPage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
        }
    }

    private void onButtonFiltersMouseMoved(MouseEvent event, boolean entered) {
        if(filters.get("status") != null) {
            if(entered) {
                iconButtonFilters.setImage(getIcon("IC_FILTERS_DEFAULT"));
            } else {
                iconButtonFilters.setImage(getIcon("IC_FILTERS_HIGHLIGHTED"));
            }
        } else {
            if(entered) {
                iconButtonFilters.setImage(getIcon("IC_FILTERS_HIGHLIGHTED"));
            } else {
                iconButtonFilters.setImage(getIcon("IC_FILTERS_DEFAULT"));
            }
        }
    }

    private void onButtonPreviousReviewsPageMousePressed(MouseEvent event) {
        iconButtonPreviousReviewsPage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
    }

    private void onButtonNextReviewsPageMousePressed(MouseEvent event) {
        iconButtonNextReviewsPage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
    }

    private void onButtonPreviousReviewsPageMouseReleased(MouseEvent event) {
        if(buttonPreviousReviewsPage.isHover()) {
            iconButtonPreviousReviewsPage.setImage(getIcon("IC_ARROW_LEFT_HIGHLIGHTED"));
        } else {
            iconButtonPreviousReviewsPage.setImage(getIcon("IC_ARROW_LEFT_DEFAULT"));
        }
    }

    private void onButtonNextReviewsPageMouseReleased(MouseEvent event) {
        if(buttonNextReviewsPage.isHover()) {
            iconButtonNextReviewsPage.setImage(getIcon("IC_ARROW_RIGHT_HIGHLIGHTED"));
        } else {
            iconButtonNextReviewsPage.setImage(getIcon("IC_ARROW_RIGHT_DEFAULT"));
        }
    }

    protected int getSelectedRowReviewIndex() {
        return tableReviews.getSelectionModel().getSelectedIndex();
    }

    protected int getObservableReviewsSize() {
        return observableReviews.size();
    }

    protected Review getSelectedRowReview() {
        return tableReviews.getSelectionModel().getSelectedItem();
    }

    private void setColumnCellValueFactory() {
        columnReviewId.setCellValueFactory(new PropertyValueFactory<>("reviewId"));
        columnAccommodationFacilityId.setCellValueFactory(new PropertyValueFactory<>("accommodationFacilityId"));
        columnUserId.setCellValueFactory(new PropertyValueFactory<>("reviewerId"));
        columnStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        columnRating.setCellValueFactory(new PropertyValueFactory<>("rating"));
        columnAccommodationFacilityName.setCellValueFactory(new PropertyValueFactory<>("accommodationFacilityName"));
        columnUsername.setCellValueFactory(new PropertyValueFactory<>("reviewerUsername"));
        columnPublicationDate.setCellValueFactory(new PropertyValueFactory<>("publicationDate"));
        columnDateOfStay.setCellValueFactory(new PropertyValueFactory<>("dateOfStay"));
        columnDeleteDate.setCellValueFactory(new PropertyValueFactory<>("expectedCancellationDate"));
        columnLikes.setCellValueFactory(new PropertyValueFactory<>("totalLikes"));
        columnDislikes.setCellValueFactory(new PropertyValueFactory<>("totalDislikes"));
    }

    private void setDisableReviewExplorerControls(boolean disable) {
        buttonNextReviewsPage.setDisable(disable);
        buttonPreviousReviewsPage.setDisable(disable);
        comboBoxMaxRows.setDisable(disable);
        buttonFilters.setDisable(disable);
    }

    protected void scrollToRowReviewIndex(int index) {
        tableReviews.scrollTo(index);
    }

    protected void selectRowReview(int index) {
        tableReviews.getSelectionModel().select(index);
    }

    private void fetchReviews(boolean isNext) {
        int offset = (currentReviewsPageIndex + ((isNext) ? +1: -1) - 1) * fetchLimit;
        setDisableReviewExplorerControls(true);
        ReviewDAO reviewDAO = DAOFactory.getReviewDAO();
        reviewDAO.getReviews(filters, null, offset, fetchLimit, new LambdaResultHandler() {

            @Override
            public void onSuccess(JsonObject result) {
                handleFetchReviewsSuccess(
                        reviewDAO.parseResult(result),
                        result.getAsJsonObject("data").get("records").getAsInt(),
                        isNext
                );
            }

            @Override
            public void onFailure(@NotNull Exception exception) {
                handleFetchReviewsFailure(exception);
            }

        });
    }

    private void handleFetchReviewsSuccess(List<Review> reviews, int totalReviews, boolean isNext) {
        Platform.runLater(() -> {
            updateReviewsPageCounter(totalReviews, isNext);
            observableReviews = FXCollections.observableList(reviews);
            tableReviews.setItems(observableReviews);
            setDisableReviewExplorerControls(false);
            buttonPreviousReviewsPage.setDisable(currentReviewsPageIndex == 1 || currentReviewsPageIndex == 0);
            buttonNextReviewsPage.setDisable(currentReviewsPageIndex == totalReviewsPagesCount);
        });
    }

    private void handleFetchReviewsFailure(Exception exception) {
        Platform.runLater(() -> {
            setDisableReviewExplorerControls(false);
            reviewsManagementController.showAlert(AlertType.ERROR, "An unknown error occurred, please try again");
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

    private void updateReviewsPageCounter(int totalReviews, boolean isNext) {
        if(totalReviews > 0) {
            totalReviewsPagesCount = (int) Math.ceil((double) totalReviews/fetchLimit);
            currentReviewsPageIndex = currentReviewsPageIndex + ((isNext) ? +1: -1);
        }
        labelCurrentReviewsPageCount.setText(
                String.format("%02d - %02d", currentReviewsPageIndex, totalReviewsPagesCount)
        );
    }

    protected void updateRowReview(Review oldReview, Review updatedReview) {
        if(filters.get("status") == null) {
            int reviewIndex = observableReviews.indexOf(oldReview);
            observableReviews.remove(oldReview);
            observableReviews.add(reviewIndex, updatedReview);
            tableReviews.getSelectionModel().select((reviewIndex+1 == observableReviews.size()) ? 0: reviewIndex+1);
            tableReviews.scrollTo((reviewIndex+1 == observableReviews.size()) ? 0: reviewIndex+1);
        } else {
            observableReviews.remove(oldReview);
        }
    }

}