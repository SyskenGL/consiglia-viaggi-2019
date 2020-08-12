package it.isw.cvoffice.controllers;

import it.isw.cvoffice.controllers.enumerations.AlertType;
import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import javafx.util.Pair;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.logging.Logger;


public class ReviewsManagementController extends Controller {

    @FXML private Pane reviewsExplorerContainer;
    @FXML private Pane reviewsApprovalPanelContainer;
    @FXML private Pane alertPane;
    @FXML private Label alertLabelTitle;
    @FXML private Label alertLabelMessage;
    @FXML private ImageView alertIcon;

    private ReviewsExplorerController reviewsExplorerController;
    private ReviewsApprovalPanelController reviewsApprovalPanelController;
    private BlockingQueue<Pair<AlertType, String>> alerts;


    @Override
    public void initialize() {
        alerts = new LinkedBlockingQueue<>();
        reviewsExplorerController = new ReviewsExplorerController(this);
        reviewsApprovalPanelController = new ReviewsApprovalPanelController(this);
        try {
            reviewsExplorerContainer.getChildren().add(loadUI("reviews_explorer", reviewsExplorerController));
        } catch (Exception e) {
            Logger.getLogger(MenuController.class.toString()).severe("reviews_explorer UI loading failed");
        }
        try {
            reviewsApprovalPanelContainer.getChildren().add(loadUI("reviews_approval_panel", reviewsApprovalPanelController));
        } catch (Exception e) {
            Logger.getLogger(MenuController.class.toString()).severe("reviews_approval_panel UI loading failed");
        }
        listenToEvents();
    }

    @Override
    public void listenToEvents() {
        listenToAlerts();
    }

    private void listenToAlerts() {
        new Thread(() -> {
            try {
                while(true) {
                    Pair<AlertType, String> alert = alerts.take();
                    AlertType alertType = alert.getKey();
                    String message = alert.getValue();
                    Platform.runLater(() -> {
                        alertPane.setVisible(true);
                        FadeTransition fadeTransition = new FadeTransition(Duration.millis(1750), alertPane);
                        fadeTransition.setFromValue(0.0);
                        fadeTransition.setToValue(1.0);
                        fadeTransition.setAutoReverse(true);
                        fadeTransition.setCycleCount(2);
                        fadeTransition.setOnFinished(event -> {
                            alertPane.setVisible(false);
                        });
                        fadeTransition.play();
                        switch (alertType) {
                            case APPROVED:
                                alertIcon.setImage(getIcon("IC_ALERT_SUCCESS"));
                                alertPane.setStyle("-fx-background-color: -fx-color-transparent-green");
                                alertLabelTitle.setStyle("-fx-text-fill: -fx-color-green");
                                alertLabelMessage.setStyle("-fx-text-fill: -fx-color-green");
                                alertLabelTitle.setText("APPROVED");
                                break;
                            case DISAPPROVED:
                                alertIcon.setImage(getIcon("IC_ALERT_ERROR"));
                                alertPane.setStyle("-fx-background-color: -fx-color-transparent-red");
                                alertLabelTitle.setStyle("-fx-text-fill: -fx-color-red");
                                alertLabelMessage.setStyle("-fx-text-fill: -fx-color-red");
                                alertLabelTitle.setText("DISAPPROVED");
                                break;
                            case ERROR:
                                alertIcon.setImage(getIcon("IC_ALERT_ERROR"));
                                alertPane.setStyle("-fx-background-color: -fx-color-transparent-red");
                                alertLabelTitle.setStyle("-fx-text-fill: -fx-color-red");
                                alertLabelMessage.setStyle("-fx-text-fill: -fx-color-red");
                                alertLabelTitle.setText("ERROR");
                                break;
                            case WARNING:
                                alertIcon.setImage(getIcon("IC_ALERT_WARNING"));
                                alertPane.setStyle("-fx-background-color: -fx-color-transparent-orange");
                                alertLabelTitle.setStyle("-fx-text-fill: -fx-color-orange");
                                alertLabelMessage.setStyle("-fx-text-fill: -fx-color-orange");
                                alertLabelTitle.setText("ATTENTION");
                                break;
                        }
                        alertLabelMessage.setText(message);
                    });
                    Thread.sleep(1500);
                }
            } catch (InterruptedException e) {
                return;
            }
        }).start();
    }

    protected ReviewsExplorerController getReviewsExplorerController() {
        return reviewsExplorerController;
    }

    protected ReviewsApprovalPanelController getReviewsApprovalPanelController() {
        return reviewsApprovalPanelController;
    }

    protected void showAlert(AlertType alertType, String message) {
        Pair<AlertType, String> pair = new Pair<>(alertType, message);
        try {
            alerts.put(pair);
        } catch (InterruptedException e) {
            return;
        }
    }

}