package com.vietnamesedbpedia.program;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.web.WebView;
import javafx.stage.Stage;

import java.io.File;

/**
 * Created by vieta on 5/4/2017.
 */
public class MainGUI extends Application{
    private HBox root = new HBox();
    private VBox menuBox = new VBox();
    private VBox contentBox = new VBox();
    private HBox headerWikiBox, headerDbpediaBox;
    private StackPane stackPaneWikiBox;
    private StackPane stackPaneInfoBox;
    private WebView dbpediaWebView;
    private InfoboxBox infoboxBox;

    private TableView tableViewInfobox;

    private ListView<String> menuList = new ListView<>();


    @Override
    public void start(Stage primaryStage) throws Exception {
        Scene scene = new Scene(root);
        primaryStage.setWidth(1000);
        primaryStage.setHeight(600);
        primaryStage.setTitle("Vietnamese DBpedia");

        createMenuBox();
        WikiBox wikiBox = new WikiBox();
        wikiBox.createWikiBox();
        headerWikiBox = wikiBox.getHeaderBox();
        stackPaneWikiBox = wikiBox.getStackPane();

        infoboxBox = new InfoboxBox(contentBox);
        infoboxBox.createInfoboxBox();
        //tableViewInfobox = infoboxBox.getTableView();
        stackPaneInfoBox = infoboxBox.getInfoboxStackPane();

        DbpediaBox dbpediaBox = new DbpediaBox(contentBox);
        dbpediaBox.createDbpediaBox();
        dbpediaWebView = dbpediaBox.getWebView();
        headerDbpediaBox = dbpediaBox.getHeader();

        contentBox.getChildren().setAll(headerWikiBox,stackPaneWikiBox);

        root.getChildren().add(menuBox);
        root.getChildren().add(contentBox);
        scene.getStylesheets().add(new File("styles/styles.css").toURI().toString());

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createMenuBox(){
        menuBox.setAlignment(Pos.CENTER);
        Label label = new Label("Vietnamese \n DBpedia");
        label.setFont(new Font(30));

        ObservableList<String> items = FXCollections.observableArrayList (
                "Wikipedia", "Infobox", "DBpedia");
        menuList.setItems(items);
        menuList.setPrefHeight(100);
        menuList.setPrefWidth(200);
        menuList.setMaxWidth(200);
        menuList.setMinWidth(200);
        menuList.setOnMouseClicked(new ClickItemMenu());

        menuBox.getChildren().addAll(label, menuList);
    }



    public static void main(String[] args){
        launch(args);
    }

    private class ClickItemMenu implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            int id = menuList.getSelectionModel().getSelectedIndex();
            switch (id){
                case 0:
                    contentBox.getChildren().setAll(headerWikiBox, stackPaneWikiBox);
                    break;
                case 1:
//                    Platform.runLater(new InfoboxBox.ExtractInfoboxFromWiki());
                    infoboxBox.new ExtractInfoboxFromWiki(stackPaneInfoBox).run();
                    break;
                case 2:
                    contentBox.getChildren().setAll(headerDbpediaBox, dbpediaWebView);
                    new DbpediaBox.DBpediaPage().run();
                    break;
            }
        }
    }
}
