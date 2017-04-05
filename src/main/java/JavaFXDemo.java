import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
//import javafx.collections.ObservableList;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Group;
//import javafx.scene.Scene;
//import javafx.scene.control.Button;
//import javafx.scene.control.ListView;
//import javafx.scene.control.ProgressIndicator;
//import javafx.scene.control.ScrollPane;
//import javafx.scene.control.cell.ComboBoxListCell;
//import javafx.scene.layout.HBox;
//import javafx.scene.layout.Priority;
//import javafx.scene.layout.StackPane;
//import javafx.scene.layout.VBox;
//import javafx.scene.web.WebEngine;
//import javafx.scene.web.WebView;
//import javafx.stage.Stage;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * Created by vieta on 23/3/2017.
 */
public class JavaFXDemo extends Application {

//    @Override
//    public void start(final Stage stage) {
////        Group root = new Group();
////        //Scene scene = new Scene(root, 260, 80);
//////        stage.setScene(scene);
//////
//////        Group g = new Group();
////        Scene scene = new Scene(new Group());
////
////        final ProgressIndicator p1 = new ProgressIndicator();
//////
//////        g.getChildren().add(p1);
//////
//////
//////        scene.setRoot(g);
//////        stage.show();
////        final WebView browser = new WebView();
////        final WebEngine webEngine = browser.getEngine();
////        StackPane stackPane = new StackPane();
////
////
////        ScrollPane scrollPane = new ScrollPane();
////        scrollPane.setContent(browser);
////
////        webEngine.getLoadWorker().stateProperty()
////                .addListener(new ChangeListener<State>() {
////                    @Override
////                    public void changed(ObservableValue ov, State oldState, State newState) {
////
////                        if (newState == Worker.State.SUCCEEDED) {
////                            p1.setVisible(false);
////                        }
////
////                    }
////                });
////        webEngine.load("https://vi.wikipedia.org/wiki/H%C3%A0_N%E1%BB%99i");
////        stackPane.getChildren().add(scrollPane);
////        stackPane.getChildren().add(p1);
////
////        scene.setRoot(stackPane);
////
////        stage.setScene(scene);
////        stage.show();
////        VBox root = new VBox(30);
////        root.setPadding(new Insets(10));
////        root.setAlignment(Pos.CENTER);
//////
//////        HBox hbox = new HBox(30);
//////        hbox.setAlignment(Pos.CENTER);
//////
//////        VBox vbox = new VBox(10);
//////        vbox.setMaxWidth(200);
//////        vbox.setStyle("-fx-border-style: solid;"
//////                + "-fx-border-width: 1;"
//////                + "-fx-border-color: black");
//////
//////        VBox vbox2 = new VBox(10);
//////        vbox2.setMaxWidth(200);
//////        vbox2.setStyle("-fx-border-style: solid;"
//////                + "-fx-border-width: 1;"
//////                + "-fx-border-color: black");
//////
//////        Button bt1 = new Button("1");
//////        Button bt2 = new Button("I am Button!"); // got it? ;)
//////        Button bt3 = new Button("I am a veeeeeeeery long button");
//////
//////        Button bt4 = new Button("1");
//////        bt4.setMaxWidth(Double.MAX_VALUE);
//////        Button bt5 = new Button("I am Button!");
//////        bt5.setMaxWidth(Double.MAX_VALUE);
//////        Button bt6 = new Button("I am a veeeeeeeery long button");
//////        bt6.setMaxWidth(Double.MAX_VALUE);
//////
//////        vbox.getChildren().addAll(bt1, bt2, bt3);
//////        vbox2.getChildren().addAll(bt4, bt5, bt6);
//////        hbox.getChildren().addAll(vbox, vbox2);
////
////        HBox hbox2 = new HBox(20);
////        hbox2.setStyle("-fx-border-style: solid;"
////                + "-fx-border-width: 1;"
////                + "-fx-border-color: black");
////        hbox2.setAlignment(Pos.CENTER);
////        hbox2.setPrefHeight(50);
////        VBox.setVgrow(hbox2, Priority.ALWAYS);
////
////        for (int i = 0; i < 10; i++)
////        {
////            Button bt = new Button(String.valueOf(i));
////            bt.setMaxHeight(Double.MAX_VALUE);
////            hbox2.getChildren().add(bt);
////        }
////
////        root.getChildren().addAll( hbox2);
////        Scene scene = new Scene(root, 500, 250);
////
////        stage.setTitle("Sizing Buttons equally inside a VBox or HBox");
////        stage.setScene(scene);
////        stage.show();
//
//        ObservableList<String> names = FXCollections
//                .observableArrayList();
//        ObservableList<String> data = FXCollections.observableArrayList();
//
//        ListView<String> listView = new ListView<String>(data);
//        listView.setPrefSize(200, 250);
//        listView.setEditable(true);
//
//        names.addAll("A", "B", "C", "D", "E");
//
//        data.add("Double Click to Select Value");
//
//        listView.setItems(data);
//        listView.setCellFactory(ComboBoxListCell.forListView(names));
//
//        StackPane root = new StackPane();
//        root.getChildren().add(listView);
//        stage.setScene(new Scene(root, 200, 250));
//        stage.show();
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }

    private final TableView<Person> table = new TableView<>();
    private final ObservableList<Person> data =
            FXCollections.observableArrayList(new Person("A", "B"),
                    new Person("D", "E"));


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        Scene scene = new Scene(new Group());
        stage.setWidth(1000);
        stage.setHeight(550);


        TableColumn firstNameCol = new TableColumn("First Name");
        firstNameCol.setMinWidth(100);
        firstNameCol.setCellValueFactory(
                new PropertyValueFactory<>("firstName"));

        TableColumn lastNameCol = new TableColumn("Last Name");
        lastNameCol.setMinWidth(100);
        lastNameCol.setCellValueFactory(
                new PropertyValueFactory<>("lastName"));

        table.setItems(data);
        table.getColumns().addAll(firstNameCol, lastNameCol);

        firstNameCol.setSortType(TableColumn.SortType.DESCENDING);
        lastNameCol.setSortable(false);

        final VBox vbox = new VBox();
        vbox.setSpacing(5);
        vbox.setPadding(new Insets(10, 0, 0, 10));
        vbox.getChildren().addAll(table);

        ((Group) scene.getRoot()).getChildren().addAll(vbox);

        stage.setScene(scene);
        stage.show();
    }

    public static class Person {

        private final SimpleStringProperty firstName;
        private final SimpleStringProperty lastName;

        private Person(String fName, String lName) {
            this.firstName = new SimpleStringProperty(fName);
            this.lastName = new SimpleStringProperty(lName);
        }

        public String getFirstName() {
            return firstName.get();
        }

        public void setFirstName(String fName) {
            firstName.set(fName);
        }

        public String getLastName() {
            return lastName.get();
        }

        public void setLastName(String fName) {
            lastName.set(fName);
        }
    }
}