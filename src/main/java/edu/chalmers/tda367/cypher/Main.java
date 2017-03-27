package edu.chalmers.tda367.cypher;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {

        Parent root = FXMLLoader.load(getClass().getResource("/fxml/root.fxml"));
        primaryStage.setTitle("Cypher");
        primaryStage.setScene(new Scene(root, 1000, 600));
        primaryStage.setMinWidth(25);
        primaryStage.setMinHeight(25);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
