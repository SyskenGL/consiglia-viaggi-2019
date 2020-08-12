package it.isw.cvoffice.controllers;

import it.isw.cvoffice.models.Operator;
import it.isw.cvoffice.utils.aws.cognito.Authenticator;
import it.isw.cvoffice.utils.aws.cognito.callbacks.AuthenticationHandler;
import javafx.application.Platform;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.jetbrains.annotations.NotNull;
import software.amazon.awssdk.services.cognitoidentity.model.Credentials;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthFlowType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.AuthenticationResultType;
import software.amazon.awssdk.services.cognitoidentityprovider.model.NotAuthorizedException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;


public class SignInController extends Controller {

    @FXML private AnchorPane signInRoot;
    @FXML private TextField textFieldUsername;
    @FXML private TextField textFieldPassword;
    @FXML private ImageView iconUsername;
    @FXML private ImageView iconPassword;
    @FXML private Button buttonSignIn;
    @FXML private Circle buttonClose;
    @FXML private Circle buttonMinimize;
    @FXML private Circle buttonMaximize;
    @FXML private Label labelInfo;
    @FXML private Pane contentPaneUsername;
    @FXML private Pane contentPanePassword;

    private boolean newPasswordObtained;
    private double windowX;
    private double windowY;


    @Override
    public void initialize() {
        listenToEvents();
        buttonSignIn.setDisable(true);
    }

    @Override
    public void listenToEvents() {
        /* CLICKED */
        buttonClose.setOnMouseClicked(this::onButtonCloseClicked);
        buttonMinimize.setOnMouseClicked(this::onButtonMinimizeClicked);
        buttonMaximize.setOnMouseClicked(this::onButtonMaximizeClicked);
        buttonSignIn.setOnAction(this::onButtonSignInClicked);
        /* HOVER IN */
        contentPaneUsername.setOnMouseEntered(event -> {
            onContentPaneUsernameMouseMoved(event, true);
        });
        contentPanePassword.setOnMouseEntered(event -> {
            onContentPanePasswordMouseMoved(event, true);
        });
        /* HOVER OUT */
        contentPaneUsername.setOnMouseExited(event -> {
            onContentPaneUsernameMouseMoved(event, false);
        });
        contentPanePassword.setOnMouseExited(event -> {
            onContentPanePasswordMouseMoved(event, false);
        });
        /* DRAGGED */
        signInRoot.setOnMouseDragged(this::onMainViewRootDragged);
        /* PRESSED */
        signInRoot.setOnMousePressed(this::onMainViewRootPressed);
        /* CHANGED */
        textFieldUsername.textProperty().addListener((observable, oldValue, newValue) ->
                onTextFieldUsernameChanged(newValue)
        );
        textFieldPassword.textProperty().addListener((observable, oldValue, newValue) ->
                onTextFieldPasswordChanged(newValue)
        );
    }

    private void onButtonCloseClicked(MouseEvent event) {
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

    private void onButtonSignInClicked(@NotNull Event event) {
        Node source = (Node)  event.getSource();
        Stage primaryStage  = (Stage) source.getScene().getWindow();
        if(newPasswordObtained) {
            openMainView(primaryStage);
        } else {
            labelInfo.setText("");
            signIn(textFieldUsername.getText(), textFieldPassword.getText(), primaryStage);
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

    private void onTextFieldUsernameChanged(String newValue){
        if(labelInfo.getText().length() > 0){
            labelInfo.setText("");
        }
        if(newValue.length() == 0 ){
            buttonSignIn.setDisable(true);
        }else{
            if(textFieldPassword.getText().length() > 0){
                buttonSignIn.setDisable(false);
            }
        }
    }

    private void onTextFieldPasswordChanged(String newValue){
        if(labelInfo.getText().length() > 0){
            labelInfo.setText("");
        }
        if(newValue.length() == 0 ){
            buttonSignIn.setDisable(true);
        }else{
            if(textFieldUsername.getText().length() > 0){
                buttonSignIn.setDisable(false);
            }
        }
    }

    private void onContentPaneUsernameMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconUsername.setImage(getIcon("IC_USER_HIGHLIGHTED"));
        } else {
            iconUsername.setImage(getIcon("IC_USER_DEFAULT"));
        }
    }

    private void onContentPanePasswordMouseMoved(MouseEvent event, boolean entered) {
        if(entered) {
            iconPassword.setImage(getIcon("IC_LOCK_HIGHLIGHTED"));
        } else {
            iconPassword.setImage(getIcon("IC_LOCK_DEFAULT"));
        }
    }

    private void signIn(String username, String password, Stage primaryStage) {
        Authenticator authenticator = Authenticator.builder(AuthFlowType.USER_PASSWORD_AUTH);
        Map<String, String> authParameters = new HashMap<>();
        authParameters.put("USERNAME", username);
        authParameters.put("PASSWORD", password);
        authenticator.authenticate(authParameters, new AuthenticationHandler() {

            @Override
            public void onAuthenticationSuccess(AuthenticationResultType authenticationBundle, Credentials credentials) {
                handleSignInSuccess(username, authenticationBundle, credentials, primaryStage);
            }

            @Override
            public void onNewPasswordObtained(AuthenticationResultType authenticationBundle, Credentials credentials, String newPassword) {
                handleSignInNewPasswordObtained(username, authenticationBundle, credentials, newPassword);
            }

            @Override
            public void onAuthenticationFailure(Exception exception) {
                handleSignInFailure(exception);
            }

        });
    }

    private void handleSignInSuccess(String username,
                                     AuthenticationResultType authenticationBundle,
                                     Credentials credentials,
                                     Stage primaryStage) {
        Platform.runLater(() -> {
            Operator.initializeOperator(username, authenticationBundle, credentials);
            openMainView(primaryStage);
        });
    }

    private void handleSignInFailure(Exception exception) {
        Platform.runLater(() -> {
            if(exception.getClass() == NotAuthorizedException.class) {
                labelInfo.setStyle("-fx-text-fill: -fx-color-red");
                labelInfo.setText("Attention - incorrect username or password");
            } else {
                labelInfo.setStyle("-fx-text-fill: -fx-color-red");
                labelInfo.setText("An unknown error occurred, please try again");
            }
        });
    }

    private void handleSignInNewPasswordObtained(String username,
                                                 AuthenticationResultType authenticationBundle,
                                                 Credentials credentials,
                                                 String newPassword) {
        Platform.runLater(() -> {
            buttonSignIn.setText("Enter");
            newPasswordObtained = true;
            Operator.initializeOperator(username, authenticationBundle, credentials);
            labelInfo.setStyle("-fx-text-fill: -fx-color-orange");
            labelInfo.setText("Welcome to CV19Office, here is your password, keep it secret: " + newPassword);
        });
    }

    private void openMainView(@NotNull Stage primaryStage) {
        Parent root = null;
        try {
            Platform.setImplicitExit(false);
            primaryStage.hide();
            root = FXMLLoader.load(SignInController.class.getResource("/layout/main_view.fxml"));
            Scene scene = new Scene(root);
            scene.setFill(Color.TRANSPARENT);
            Stage newPrimaryStage = new Stage();
            newPrimaryStage.initStyle(StageStyle.TRANSPARENT);
            newPrimaryStage.setScene(scene);
            newPrimaryStage.show();
        } catch (IOException e) {
            Logger.getLogger(SignInController.class.toString()).severe("main_view loading failed");
        }
    }

}