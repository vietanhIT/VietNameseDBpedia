package com.vietnamesedbpedia.program1;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXListView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker.State;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import com.vietnamesedbpedia.program.InfoboxBox;
import com.vietnamesedbpedia.program.SuggestionSearch;

import java.io.File;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by vieta on 5/4/2017.
 */
public class WikiBox {
    private VBox contentBox = new VBox();
    private HBox header = new HBox();
    private WebView webView = new WebView();
    private StackPane stackPane = new StackPane();
    private ImageView reloadImage = new ImageView();
    private ImageView submitImage = new ImageView();
    private Image reload = new Image(new File("image/redo.png").toURI().toString());
    private Image reloadHover = new Image(new File("image/redo_hover.png").toURI().toString());
    private Image submit = new Image(new File("image/next.png").toURI().toString());
    private Image submitHover = new Image(new File("image/next_hover.png").toURI().toString());
    private WebEngine webEngine = webView.getEngine();
    private ProgressIndicator progressIndicator = new ProgressIndicator();
    private TextField textField = new TextField();
    private ArrayList<String> listSuggestion;
    public static String title;
    JFXListView<Label> list = new JFXListView<>();
    private static final String ITEM = "Item ";
    private int index = -1;
    JFXDialog dialog = new JFXDialog();
    JFXButton startConvertButton = new JFXButton("Start Convert");


    public WikiBox(VBox contentBox){
        this.contentBox = contentBox;
    }

    public void createWikiBox(){
        header.setSpacing(10);
        header.setPadding(new Insets(10, 10, 10, 10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #EEEEEE;");

        reloadImage.setFitWidth(16);
        reloadImage.setFitHeight(16);
        reloadImage.setImage(reload);

        HBox.setHgrow(textField, Priority.ALWAYS);
        VBox.setVgrow(stackPane, Priority.ALWAYS);

        submitImage.setFitWidth(16);
        submitImage.setFitHeight(16);
        submitImage.setImage(submit);

        header.getChildren().add(reloadImage);
        header.getChildren().add(textField);
        header.getChildren().add(submitImage);

        list.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());


        list.getStyleClass().add("mylistview");

        progressIndicator.setMaxWidth(32);
        progressIndicator.setMaxHeight(32);
        progressIndicator.setStyle(" -fx-progress-color: #BDBDBD;");
        progressIndicator.setVisible(false);

        startConvertButton.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());
        startConvertButton.getStyleClass().add("button-raised");

        startConvertButton.setVisible(false);
        stackPane.getChildren().addAll(webView, progressIndicator, startConvertButton);
        stackPane.setAlignment(Pos.CENTER);

//        dialog.getStylesheets().add(new File("material/css/jfoenix-components.css").toURI().toString());
//        dialog.getStyleClass().add("dialog");
//        dialog.setContent(new Label("Content"));



        setAction();
    }

    private void setAction(){
        MouseEntered mouseEntered = new MouseEntered();
        reloadImage.setOnMouseEntered(mouseEntered);
        submitImage.setOnMouseEntered(mouseEntered);
        MouseExited mouseExited = new MouseExited();
        reloadImage.setOnMouseExited(mouseExited);
        submitImage.setOnMouseExited(mouseExited);
        MouseClicked mouseClicked = new MouseClicked();
        reloadImage.setOnMouseClicked(mouseClicked);
        submitImage.setOnMouseClicked(mouseClicked);

        webEngine.getLoadWorker().stateProperty().addListener(new ChangeListenerWebView());
        textField.textProperty().addListener(new Suggestion());

        webEngine.locationProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                System.out.println(oldValue + newValue);
            }
        });

        list.setOnMouseClicked(new ClickListView());
        list.getFocusModel().focus(0);
        textField.setOnKeyPressed(new UsingKeyOnListView());
        startConvertButton.setOnMouseClicked(new ClickStartConvert());
    }

    private class MouseEntered implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if(object.equals(reloadImage)){
                reloadImage.setImage(reloadHover);
            }else if(object.equals(submitImage)){
                submitImage.setImage(submitHover);
            }
        }
    }

    private class MouseExited implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if(object.equals(reloadImage)){
                reloadImage.setImage(reload);
            }else if(object.equals(submitImage)){
                submitImage.setImage(submit);
            }
        }
    }

    private class MouseClicked implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            progressIndicator.setVisible(true);
            title = textField.getText();
            if(object.equals(reloadImage)){
                webEngine.reload();
            }else if(object.equals(submitImage)){
                index = -1;
                progressIndicator.setVisible(true);
//                list.setVisible(false);
                contentBox.getChildren().remove(list);
//                if(stackPane.getChildren().size()==0){
//                    stackPane.getChildren().addAll(webView, progressIndicator, startConvertButton);
//                }


                //stackPane.getChildren().addAll(webView, progressIndicator, startConvertButton);
                ArrayList<String> list = RedirectSearch.getRedirect(title);
                if(list.size()>0){
                    title = list.get(0);
                }
                try{
                    webEngine.load("https://vi.wikipedia.org/wiki/"+ URLEncoder.encode(title,"UTF-8"));
                    System.out.println(URLDecoder.decode(webEngine.getLocation()));
                    System.out.println(URLEncoder.encode(title,"UTF-8"));
                }catch (Exception e){
                    e.getMessage();
                }
                InfoboxBox.isLoad = false;
            }
        }
    }

    private class ChangeListenerWebView implements ChangeListener<State>{
        @Override
        public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
            if(newValue == State.SUCCEEDED){
                progressIndicator.setVisible(false);
                startConvertButton.setVisible(true);
//                dialog.show(stackPane);
            }
        }
    }

    private class ClickStartConvert implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            startConvertButton.setVisible(false);
//            new DbpediaBox.DBpediaPage().run();
            try {
                DbpediaBox.load();
            }catch (Exception e){
                e.getMessage();
            }
        }
    }

    private class ClickListView implements EventHandler<MouseEvent>{

        @Override
        public void handle(MouseEvent event) {
            textField.setText(list.getSelectionModel().getSelectedItem().getText());
        }
    }

    private class UsingKeyOnListView implements EventHandler<KeyEvent>{

        @Override
        public void handle(KeyEvent event) {
            KeyCode code = event.getCode();
            if(code==KeyCode.DOWN){
                if(index<9){
                    index++;
                    System.out.println("D "+index);
                }
                list.getSelectionModel().select(index);
                list.scrollTo(index);

            }else if(code == KeyCode.UP){
                if(index >0){
                    index--;
                    System.out.println("U "+index);
                }
                list.getSelectionModel().select(index);
                list.scrollTo(index);
            }else if(code == KeyCode.ENTER){
                textField.setText(list.getSelectionModel().getSelectedItem().getText());
            }
        }
    }

    private class Suggestion implements ChangeListener<String>{
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            index = -1;
            if(!contentBox.getChildren().contains(list)){
                contentBox.getChildren().remove(stackPane);
                contentBox.getChildren().add(list);
                contentBox.getChildren().add(stackPane);
            }

            startConvertButton.setVisible(false);
            progressIndicator.setVisible(false);

            listSuggestion = SuggestionSearch.getSuggestion(newValue);
            list.getItems().removeAll(list.getItems());
            int size = listSuggestion.size();
            for (int i = 0; i < size; i++) {
                list.getItems().add(new Label(listSuggestion.get(i)));
            }

        }
    }


    public HBox getHeaderBox(){
        return header;
    }

    public StackPane getStackPane(){
        return stackPane;
    }

    public JFXListView<Label> getList(){
        return  list;
    }
}
