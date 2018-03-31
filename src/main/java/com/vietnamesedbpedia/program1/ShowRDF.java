package com.vietnamesedbpedia.program1;

import javafx.scene.Scene;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.text.Font;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

/**
 * Created by vietanknb on 16/5/2017.
 */
public class ShowRDF {
    private HBox root = new HBox();
    private TextArea textArea = new TextArea();

    public void showRDF(String file){
        Stage stage = new Stage();
        Scene scene = new Scene(root);

        stage.setWidth(1000);
        stage.setHeight(600);
        stage.setTitle(file);

        textArea.setFont(new Font(13));
        HBox.setHgrow(textArea, Priority.ALWAYS);
        root.getChildren().add(textArea);

        readFromFile(file);
        stage.setScene(scene);
        stage.show();
    }

    private void readFromFile(String file){

        try{
            StringBuilder a = new StringBuilder();
            FileReader fileReader = new FileReader(new File("result_program",file));
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String line;
            while ((line=bufferedReader.readLine())!=null){
                textArea.appendText(line);
                textArea.appendText("\n");
            }



            bufferedReader.close();
            fileReader.close();
        }catch (Exception e){
            e.getMessage();
        }

    }

}
