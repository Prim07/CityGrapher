package com.agh.bsct.view;

import com.agh.bsct.view.config.PathsConstants;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class View extends Application {

    private static final int SCENE_WIDTH = 1500;
    private static final int SCENE_HEIGHT = 844;
    private static final String WINDOW_TITLE = "City Grapher";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent mapViewParent = FXMLLoader.load(getClass().getResource(PathsConstants.MAP_VIEW_ROOT_PATH));
        primaryStage.setTitle(WINDOW_TITLE);
        primaryStage.setScene(new Scene(mapViewParent, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
    }

}