package it.isw.cvoffice.controllers;

import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class MenuController extends Controller {

    @FXML private AnchorPane mainViewRoot;
    @FXML private BorderPane contentWrapper;
    @FXML private ToggleButton toggleButtonReviewsManagement;
    @FXML private ToggleButton toggleButtonRulebook;
    @FXML private ToggleButton toggleButtonSignOut;
    @FXML private Circle buttonClose;
    @FXML private Circle buttonMinimize;
    @FXML private Circle buttonMaximize;
    @FXML private Label labelOperatorName;
    @FXML private ImageView iconReviewsManagement;
    @FXML private ImageView iconRulebook;
    @FXML private ImageView iconSignOut;

    private double windowX;
    private double windowY;
    private ToggleButton currentToggledButton;


    @Override
    public void initialize() {
        listenToEvents();
        refreshTokenAutomatically();
        toggleButtonReviewsManagement.fire();
        labelOperatorName.setText("Operator - " + Operator.getInstance().getName());
    }

    @Override
    public void listenToEvents() {
        /* CLICK */
        toggleButtonReviewsManagement.setOnAction(this::onToggleButtonReviewsManagementToggled);
        toggleButtonRulebook.setOnAction(this::onToggleButtonRulebookToggled);
        toggleButtonSignOut.setOnAction(this::onToggleButtonSignOutToggled);
        buttonClose.setOnMouseClicked(this::onButtonCloseClicked);
        buttonMinimize.setOnMouseClicked(this::onButtonMinimizeClicked);
        buttonMaximize.setOnMouseClicked(this::onButtonMaximizeClicked);
        /* HOVER IN */
        toggleButtonReviewsManagement.setOnMouseEntered(event -> {
            onToggleButtonReviewsManagementMouseMoved(event, true);
        });
        toggleButtonRulebook.setOnMouseEntered(event -> {
            onToggleButtonRulebookMouseMoved(event, true);
        });
        toggleButtonSignOut.setOnMouseEntered(event -> {
            onToggleButtonSignOutMouseMoved(event, true);
        });
        /* HOVER OUT */
        toggleButtonReviewsManagement.setOnMouseExited(event -> {
            onToggleButtonReviewsManagementMouseMoved(event, false);
        });
        toggleButtonRulebook.setOnMouseExited(event -> {
            onToggleButtonRulebookMouseMoved(event, false);
        });
        toggleButtonSignOut.setOnMouseExited(event -> {
            onToggleButtonSignOutMouseMoved(event, false);
        });
        /* DRAGGED */
        mainViewRoot.setOnMouseDragged(this::onMainViewRootDragged);
        /* PRESSED */
        mainViewRoot.setOnMousePressed(this::onMainViewRootPressed);
    }

    private void refreshTokenAutomatically() {
        new Thread(() -> {
            while(!Thread.currentThread().isInterrupted()) {
                try {
                    Thread.sleep(3605000);
                } catch (InterruptedException e) {
                    return;
                }
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
        }).start();
    }

    private void onButtonCloseClicked(MouseEvent event) {
        Authenticator authenticator = Authenticator.builder(AuthFlowType.USER_PASSWORD_AUTH);
        authenticator.signOut(Operator.getInstance().getAuthenticationBundle().accessToken());
        Stage stage = (Stage) buttonClose.getScene().getWindow();
        stage.close();
    }

    private void onButtonMinimizeClicked(MouseEvent event) {
        Stage stage = (Stage) buttonMinimize.getScene().getWindow();
        stage.setIconified(true);
    }

    private void onButtonMaximizeClicked(MouseEvent event) {
        // Not supported
    }

    private void onToggleButtonReviewsManagementMouseMoved(Event event, boolean entered) {
        if(!(toggleButtonReviewsManagement.isSelected())) {
            if(entered) {
                iconReviewsManagement.setImage(getIcon("IC_STAMP_HIGHLIGHTED"));
            } else {
                iconReviewsManagement.setImage(getIcon("IC_STAMP_DEFAULT"));
            }
        }
    }

    private void onToggleButtonRulebookMouseMoved(Event event, boolean entered) {
        if(!(toggleButtonRulebook.isSelected())) {
            if(entered) {
                iconRulebook.setImage(getIcon("IC_RULEBOOK_HIGHLIGHTED"));
            } else {
                iconRulebook.setImage(getIcon("IC_RULEBOOK_DEFAULT"));
            }
        }
    }

    private void onToggleButtonSignOutMouseMoved(Event event, boolean entered) {
        if(!(toggleButtonSignOut.isSelected())) {
            if(entered) {
                iconSignOut.setImage(getIcon("IC_SIGN_OUT_HIGHLIGHTED"));
            } else {
                iconSignOut.setImage(getIcon("IC_SIGN_OUT_DEFAULT"));
            }
        }
    }

    private void onToggleButtonReviewsManagementToggled(ActionEvent event) {
        iconReviewsManagement.setImage(getIcon("IC_STAMP_HIGHLIGHTED"));
        toggleButtonReviewsManagement.setDisable(true);
        enableLastToggledButton();
        currentToggledButton = toggleButtonReviewsManagement;
        try {
            contentWrapper.setBottom(loadUI("reviews_management", null));
        } catch (Exception e) {
            Logger.getLogger(MenuController.class.toString()).severe("reviews_management UI loading failed");
        }
    }

    private void onToggleButtonRulebookToggled(ActionEvent event) {
        iconRulebook.setImage(getIcon("IC_RULEBOOK_HIGHLIGHTED"));
        toggleButtonRulebook.setDisable(true);
        enableLastToggledButton();
        currentToggledButton = toggleButtonRulebook;
        try {
            contentWrapper.setBottom(loadUI("rulebook", null));
        } catch (Exception e) {
            Logger.getLogger(MenuController.class.toString()).severe("rulebook UI loading failed");
        }
    }

    private void onToggleButtonSignOutToggled(@NotNull ActionEvent event) {
        iconSignOut.setImage(getIcon("IC_SIGN_OUT_HIGHLIGHTED"));
        toggleButtonSignOut.setDisable(true);
        enableLastToggledButton();
        currentToggledButton = toggleButtonSignOut;
        Authenticator authenticator = Authenticator.builder(AuthFlowType.USER_PASSWORD_AUTH);
        authenticator.signOut(Operator.getInstance().getAuthenticationBundle().accessToken());
        Operator.setLoggedIn(false);
        try {
            changeStage("sign_in", null, (Stage) ((Node) event.getSource()).getScene().getWindow());
        } catch (IOException e) {
            Logger.getLogger(MenuController.class.toString()).severe("login UI loading failed");
        }
    }

    private void onMainViewRootDragged(@NotNull MouseEvent event) {
        Node eventSource = (Node) event.getSource();
        eventSource.getScene().getWindow().setX(event.getScreenX() - windowX);
        eventSource.getScene().getWindow().setY(event.getScreenY() - windowY);
    }

    private void onMainViewRootPressed(@NotNull MouseEvent event) {
        windowX = event.getSceneX();
        windowY = event.getSceneY();
    }

    private void enableLastToggledButton() {
        if(currentToggledButton != null) {
            currentToggledButton.setDisable(false);
            if(currentToggledButton == toggleButtonReviewsManagement) {
                iconReviewsManagement.setImage(getIcon("IC_STAMP_DEFAULT"));
            } else if(currentToggledButton == toggleButtonRulebook) {
                iconRulebook.setImage(getIcon("IC_RULEBOOK_DEFAULT"));
            } else {
                iconSignOut.setImage(getIcon("IC_SIGN_OUT_DEFAULT"));
            }
        }
    }

}