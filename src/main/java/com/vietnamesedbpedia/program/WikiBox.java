package com.vietnamesedbpedia.program;

import impl.org.controlsfx.autocompletion.AutoCompletionTextFieldBinding;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.concurrent.Worker.State;
import javafx.util.Callback;
import org.controlsfx.control.textfield.AutoCompletionBinding;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;

/**
 * Created by vieta on 5/4/2017.
 */
public class WikiBox {
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

    public void createWikiBox(){
        header.setSpacing(10);
        header.setPadding(new Insets(10, 10, 10, 10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #EEEEEE;");

        reloadImage.setFitWidth(16);
        reloadImage.setFitHeight(16);
        reloadImage.setImage(reload);

        HBox.setHgrow(textField, Priority.ALWAYS);

        submitImage.setFitWidth(16);
        submitImage.setFitHeight(16);
        submitImage.setImage(submit);

        header.getChildren().add(reloadImage);
        header.getChildren().add(textField);
        header.getChildren().add(submitImage);

        progressIndicator.setMaxWidth(32);
        progressIndicator.setMaxHeight(32);
        progressIndicator.setStyle(" -fx-progress-color: #BDBDBD;");
        progressIndicator.setVisible(false);
        stackPane.getChildren().addAll(webView, progressIndicator);
        stackPane.setAlignment(Pos.CENTER);
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
                webEngine.load("https://vi.wikipedia.org/wiki/"+ URLEncoder.encode(title));
                InfoboxBox.isLoad = false;
            }
        }
    }

    private class ChangeListenerWebView implements ChangeListener<State>{
        @Override
        public void changed(ObservableValue<? extends State> observable, State oldValue, State newValue) {
            if(newValue == State.SUCCEEDED){
                progressIndicator.setVisible(false);
            }
        }
    }

    private class Suggestion implements ChangeListener<String>{
        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            listSuggestion = SuggestionSearch.getSuggestion(newValue);
            new AutoCompletionTextFieldBinding(textField, new Callback<AutoCompletionBinding.ISuggestionRequest, Collection>() {
                @Override
                public Collection call(AutoCompletionBinding.ISuggestionRequest param) {
                    return listSuggestion;
                }
            });
        }
    }


    public HBox getHeaderBox(){
        return header;
    }

    public StackPane getStackPane(){
        return stackPane;
    }
}
