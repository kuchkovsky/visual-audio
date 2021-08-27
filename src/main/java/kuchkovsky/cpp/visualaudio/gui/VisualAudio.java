package kuchkovsky.cpp.visualaudio.gui;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class VisualAudio extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        Parent root = FXMLLoader.load(getClass().getResource("/ui.fxml"));
        root.getStylesheets().add("/style.css");
        primaryStage.getIcons().add(new Image(getClass().getResourceAsStream("/icon.png")));
        primaryStage.setTitle("Visual Audio [© 2018 Kuchkovsky Development Studio]");
        primaryStage.setScene(new Scene(root));
        primaryStage.show();
        primaryStage.setMinWidth(primaryStage.getWidth());
        primaryStage.setMinHeight(primaryStage.getHeight());
    }


    public static void main(String[] args) {
        launch(args);
    }

}
