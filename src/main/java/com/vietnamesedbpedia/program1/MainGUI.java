package com.vietnamesedbpedia.program1;

import com.jfoenix.controls.JFXButton;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by vietanknb on 14/5/2017.
 */
public class MainGUI extends Application {
    private VBox vbox = new VBox();
    private StackPane root = new StackPane();

    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Vietnamese DBpedia");
        vbox.setAlignment(Pos.CENTER);

        JFXButton startButton = new JFXButton("START");
        startButton.getStyleClass().add("button-raised");
        vbox.getChildren().add(startButton);

        startButton.setOnMouseClicked(new ClickToStart(primaryStage));

        Image background = new Image(new File("image/background.png").toURI().toString());
        ImageView imageView = new ImageView();
        imageView.setImage(background);

        root.getChildren().setAll(imageView,startButton);

        scene.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());
        primaryStage.setScene(scene);
        primaryStage.show();

    }

    private class ClickToStart implements EventHandler<MouseEvent>{
        Stage primaryStage;

        public ClickToStart(Stage primaryStage){
            this.primaryStage = primaryStage;
        }

        @Override
        public void handle(MouseEvent event) {
            primaryStage.close();
            new MainGUI2().startNew(primaryStage);
        }
    }

    public static void main(String args[]){
        launch(args);
    }
}
