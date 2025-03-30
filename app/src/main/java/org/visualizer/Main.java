package org.visualizer;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class Main extends Application {

    private final BooleanProperty isAuto = new SimpleBooleanProperty(true);

    @Override
    public void start(Stage stage) {
        try {
            FXMLLoader autoLoader = new FXMLLoader(getClass().getResource("/org/visualizer/auto.fxml"));
            Parent autoRoot = autoLoader.load();

            FXMLLoader teleopLoader = new FXMLLoader(getClass().getResource("/org/visualizer/teleop.fxml"));
            Parent teleopRoot = teleopLoader.load();

            autoRoot.setStyle("-fx-background-color: darkgray;");
            teleopRoot.setStyle("-fx-background-color: darkgray;");

            int w = 900;
            int h = 600;
            Color color = Color.GRAY;

            Scene autoScene = new Scene(autoRoot, w, h, color);
            Scene teleopScene = new Scene(teleopRoot, w, h, color);

            stage.setTitle("FRC Visualizer");
            if (NetworkCommunicator.getInstance().isAuto())
            {
                stage.setScene(autoScene);
            }
            else
            {
                stage.setScene(teleopScene);
            }
            stage.show();

            isAuto.addListener((obs, oldValue, newValue) -> {
                Platform.runLater(() -> {
                    double x = stage.getX();
                    double y = stage.getY();
                    double width = stage.getWidth();
                    double height = stage.getHeight();

                    if (newValue) {
                        stage.setScene(autoScene);
                    } else {
                        stage.setScene(teleopScene);
                    }

                    // Ensure the window position and size remain constant
                    stage.setX(x);
                    stage.setY(y);
                    stage.setWidth(width);
                    stage.setHeight(height);
                });
            });

            Thread autoCheckThread = new Thread(() -> {
                while (true) {
                    try {
                        Thread.sleep(100);
                        isAuto.set(NetworkCommunicator.getInstance().isAuto());
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                // For testing, we can switch to teleop after a few seconds
                /*try {
                    Thread.sleep(5000);
                    isAuto.set(false);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }*/
            });
            autoCheckThread.setDaemon(true);
            autoCheckThread.start();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        NetworkCommunicator.getInstance().begin();

        // UI TESTING
        launch(args);

        NetworkCommunicator.getInstance().end();
    }
}
