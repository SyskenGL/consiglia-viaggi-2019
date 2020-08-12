package it.isw.cvoffice.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;


public class LoginView extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        System.setProperty("prism.lcdtext", "false");
        Parent root = null;
        root = FXMLLoader.load(LoginView.class.getResource("/layout/login.fxml"));
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    public static void main(String[] args) {
        Application.launch(args);
    }

}
