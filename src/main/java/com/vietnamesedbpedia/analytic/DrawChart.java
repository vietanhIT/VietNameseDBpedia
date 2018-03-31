package com.vietnamesedbpedia.analytic;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by vietanknb on 5/6/2017.
 */
public class DrawChart extends Application {

    private ArrayList<String> list = new ArrayList<>();
    private ArrayList<Double> percentAttributeMappingCorrect_v0s = new ArrayList<>();
    private ArrayList<Double> percentAttributeMappingCorrect_v1s = new ArrayList<>();

    private void readData(){
        File algothrimV0File = new File("analytic", "algothrim_v0.xls");
        File algothrimV1File = new File("analytic", "algothrim_v1.xls");


        WorkbookSettings ws = new WorkbookSettings();
        ws.setEncoding("Cp1252");
        try{
            Workbook w = Workbook.getWorkbook(algothrimV0File, ws);
            Sheet sheet = w.getSheet(0);

            for(int i=0; i<sheet.getRows(); i++){
                String name = sheet.getCell(1,i).getContents();
                list.add(name);
                String a = sheet.getCell(3, i).getContents();
                percentAttributeMappingCorrect_v0s.add(Double.valueOf(a));
            }

            Workbook w1= Workbook.getWorkbook(algothrimV1File, ws);
            Sheet sheet1 = w1.getSheet(0);

            for(int i=0; i<sheet1.getRows(); i++){
                String a = sheet1.getCell(3, i).getContents();
                percentAttributeMappingCorrect_v1s.add(Double.valueOf(a));
            }

        }catch (Exception e){
            e.getMessage();
        }




    }

    @Override public void start(Stage stage) {
        stage.setTitle("Kết quả thuật toán");
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Bản mẫu Infobox");

        final LineChart<String,Number> lineChart =
                new LineChart<String,Number>(xAxis,yAxis);

        lineChart.setTitle("Kết quả thuật toán, số lượng thuộc tính được ánh xạ");

        XYChart.Series series = new XYChart.Series();
        series.setName("Thuật toán ánh xạ chưa cải tiến");

        XYChart.Series series1 = new XYChart.Series();
        series1.setName("Thuật toán ánh xạ cải tiến");

        readData();

        for(int i=0; i<list.size(); i++){
            series.getData().add(new XYChart.Data(list.get(i), percentAttributeMappingCorrect_v0s.get(i)));
            series1.getData().add(new XYChart.Data(list.get(i), percentAttributeMappingCorrect_v1s.get(i)));
        }


        Scene scene  = new Scene(lineChart,800,600);
        lineChart.getData().addAll(series, series1);

        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}