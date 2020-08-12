package it.isw.cvoffice.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public abstract class Controller {

    private static final String UI_RESOURCE_PATH = "/layout/";
    private static final String ICONS_RESOURCE_PATH = "/icons/";
    private static final Map<String, Image> ICONS = new HashMap<>();


    static {
        loadIcons();
    }

    private static void loadIcons() {
        ICONS.put(
                "IC_APPROVE",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_approve.png"))
        );
        ICONS.put(
                "IC_DISAPPROVE",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_disapprove.png"))
        );
        ICONS.put(
                "IC_ARROW_LEFT_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_arrow_left_default.png"))
        );
        ICONS.put(
                "IC_ARROW_LEFT_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_arrow_left_highlighted.png"))
        );
        ICONS.put(
                "IC_ARROW_RIGHT_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_arrow_right_default.png"))
        );
        ICONS.put(
                "IC_ARROW_RIGHT_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_arrow_right_highlighted.png"))
        );
        ICONS.put(
                "IC_FILTERS_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_filters_default.png"))
        );
        ICONS.put(
                "IC_FILTERS_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_filters_highlighted.png"))
        );
        ICONS.put(
                "IC_WARNING",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_warning.png"))
        );
        ICONS.put(
                "IC_MAGNIFYING_GLASS_LOADING_SPINNER",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_magnifying_glass_loading_spinner.gif"))
        );
        ICONS.put(
                "IC_GHOST",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_ghost.png"))
        );
        ICONS.put(
                "IC_STAMP_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_stamp_default.png"))
        );
        ICONS.put(
                "IC_STAMP_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_stamp_highlighted.png"))
        );
        ICONS.put(
                "IC_RULEBOOK_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_rulebook_default.png"))
        );
        ICONS.put(
                "IC_RULEBOOK_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_rulebook_highlighted.png"))
        );
        ICONS.put(
                "IC_SIGN_OUT_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_sign_out_default.png"))
        );
        ICONS.put(
                "IC_SIGN_OUT_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_sign_out_highlighted.png"))
        );
        ICONS.put(
                "IC_USER_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_user_default.png"))
        );
        ICONS.put(
                "IC_USER_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_user_highlighted.png"))
        );
        ICONS.put(
                "IC_LOCK_DEFAULT",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_lock_default.png"))
        );
        ICONS.put(
                "IC_LOCK_HIGHLIGHTED",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_lock_highlighted.png"))
        );
        ICONS.put(
                "IC_ALERT_SUCCESS",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_alert_success.png"))
        );
        ICONS.put(
                "IC_ALERT_ERROR",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_alert_error.png"))
        );
        ICONS.put(
                "IC_ALERT_WARNING",
                new Image(MenuController.class.getResourceAsStream(ICONS_RESOURCE_PATH + "ic_alert_warning.png"))
        );
    }

    public static Parent loadUI(String UIName, Controller controller) throws IOException {
        FXMLLoader loader = new FXMLLoader(MenuController.class.getResource(UI_RESOURCE_PATH + UIName + ".fxml"));
        if(controller != null) {
            loader.setController(controller);
        }
        return loader.load();
    }

    public static Image getIcon(String icon) {
        return ICONS.get(icon);
    }

    public static void changeStage(String UIName, Controller controller, Stage primaryStage) throws IOException {
        Platform.setImplicitExit(false);
        primaryStage.hide();
        Scene scene = new Scene(loadUI(UIName, controller));
        scene.setFill(Color.TRANSPARENT);
        Stage newStage = new Stage();
        newStage.initStyle(StageStyle.TRANSPARENT);
        newStage.setScene(scene);
        newStage.show();
    }


    @FXML
    public abstract void initialize();

    public abstract void listenToEvents();

}