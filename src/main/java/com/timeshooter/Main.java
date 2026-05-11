package com.timeshooter;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main entry point for the Time Shooter 2 game
 */
public class Main extends Application {
    @Override
    public void start(Stage stage) {
        GameWindow gameWindow = new GameWindow(stage);
        gameWindow.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
