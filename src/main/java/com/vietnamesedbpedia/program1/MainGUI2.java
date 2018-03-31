package com.vietnamesedbpedia.program1;

import com.jfoenix.controls.JFXListView;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Screen;
import javafx.stage.Stage;

import static javafx.application.Application.launch;

/**
 * Created by vietanknb on 14/5/2017.
 */
public class MainGUI2 {
    private HBox root = new HBox();
    TextField textField = new TextField();
    Separator separator = new Separator();

    public void startNew(Stage primaryStage){
        Scene scene = new Scene(root);

        Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setX(primaryScreenBounds.getMinX());
        primaryStage.setY(primaryScreenBounds.getMinY());
        primaryStage.setWidth(primaryScreenBounds.getWidth());
        primaryStage.setHeight(primaryScreenBounds.getHeight());

        VBox leftBox = new VBox();
        VBox rightBox = new VBox();

        HBox headerBox = new HBox();

//        headerBox.setSpacing(10);
//        headerBox.setPadding(new Insets(10, 10, 10, 10));
//        headerBox.setAlignment(Pos.CENTER);
//        headerBox.setStyle("-fx-background-color: #EEEEEE;");

        textField.setPrefWidth(300);
//        HBox.setHgrow(textField, Priority.ALWAYS);
        headerBox.getChildren().add(textField);
        WebView webView = new WebView();
        WebEngine webEngine = webView.getEngine();

        WikiBox wikiBox = new WikiBox(leftBox);
        wikiBox.createWikiBox();
        HBox headerWikiBox = wikiBox.getHeaderBox();
        StackPane stackPaneWikiBox = wikiBox.getStackPane();
        JFXListView<Label> list = wikiBox.getList();

        webEngine.load("https://vi.wikipedia.org/wiki/H%C3%A0_N%E1%BB%99i");

        leftBox.getChildren().addAll(headerWikiBox, list, stackPaneWikiBox);

        DbpediaBox dbpediaBox = new DbpediaBox(rightBox);
        dbpediaBox.createDbpediaBox();
        StackPane dbpediaWebViewPane = dbpediaBox.getWebViewPane();
        HBox headerDbpediaBox = dbpediaBox.getHeader();

        rightBox.getChildren().setAll(headerDbpediaBox, dbpediaWebViewPane);

        separator.setMaxWidth(40);
        separator.setMaxHeight(primaryScreenBounds.getHeight());
        separator.setValignment(VPos.CENTER);
        separator.setStyle("-fx-background-color: GREEN");

        root.getChildren().addAll(leftBox, separator, rightBox);

//        scene.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());
        primaryStage.setScene(scene);
        primaryStage.show();
    }

}
