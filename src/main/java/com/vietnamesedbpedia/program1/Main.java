package com.vietnamesedbpedia.program1;

/**
 * Created by vietanknb on 14/5/2017.
 */

import com.jfoenix.controls.JFXHamburger;
import com.jfoenix.controls.JFXListView;
import com.jfoenix.controls.JFXPopup;
import com.jfoenix.controls.JFXPopup.PopupHPosition;
import com.jfoenix.controls.JFXPopup.PopupVPosition;
import com.jfoenix.controls.JFXRippler;
import com.jfoenix.controls.JFXRippler.RipplerMask;
import com.jfoenix.controls.JFXRippler.RipplerPos;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.File;

public class Main extends Application {
    JFXPopup popup;
    JFXRippler rippler;
    @Override
    public void start(Stage primaryStage) throws Exception {

        JFXHamburger show = new JFXHamburger();
        show.setPadding(new Insets(10, 5, 10, 5));
        rippler = new JFXRippler(show, RipplerMask.CIRCLE, RipplerPos.BACK);

        JFXListView<Label> list = new JFXListView<>();
        for (int i = 1; i < 5; i++) {
            list.getItems().add(new Label("Item " + i));
        }

        AnchorPane container = new AnchorPane();
        container.getChildren().add(rippler);
        AnchorPane.setLeftAnchor(rippler, 200.0);
        AnchorPane.setTopAnchor(rippler, 210.0);

        StackPane main = new StackPane();
        main.getChildren().add(container);

        popup = new JFXPopup(list);
        rippler.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                popup.show(rippler, PopupVPosition.TOP, PopupHPosition.LEFT);
            }
        });

        final Scene scene = new Scene(main, 800, 800);
        scene.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());

        primaryStage.setTitle("JFX Popup Demo");
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

}