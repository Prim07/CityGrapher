package com.agh.bsct.view;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import static com.agh.bsct.view.config.PathsConstants.FXML_ROOT_PATH;

public class View extends Application {

    private static final int SCENE_HEIGHT = 600;
    private static final int SCENE_WIDTH = 800;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource(FXML_ROOT_PATH));
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, SCENE_WIDTH, SCENE_HEIGHT));
        primaryStage.show();
    }

}
