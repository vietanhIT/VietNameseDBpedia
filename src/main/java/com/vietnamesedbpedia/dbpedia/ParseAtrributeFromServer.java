package com.vietnamesedbpedia.dbpedia;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.vietnamesedbpedia.model.Attribute;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.net.URLEncoder;
import java.util.ArrayList;

/**
 * Created by vieta on 9/3/2017.
 */
public class ParseAtrributeFromServer {
    public static String FOLDER = "template_statistic";

    public static void main(String[] args){
        ParseAtrributeFromServer parseAtrributeFromServer = new ParseAtrributeFromServer();
        parseAtrributeFromServer.parseFromServer("Thông_tin_khu_dân_cư");
    }
    private void parseFromServer(final String title){
        new Thread(new Runnable() {
            @Override
            public void run() {
                try{
//                    ArrayList<String> titles = new ArrayList<>();
//                    File file = new File(FOLDER + File.separator+ "all.xls");
//                    Workbook workbook1 = Workbook.getWorkbook(file);
//                    Sheet sheet1 = workbook1.getSheet(0);
//                    for(int i =0 ; i< sheet1.getRows(); i++){
//                        titles.add(sheet1.getCell(0, i).getContents().trim().replace(" ","_"));
//                    }
//                    for(final String title: titles){
//                        new Thread(new Runnable() {
//                            @Override
//                            public void run() {
                                int index =0;
                                WritableWorkbook workbook;
                                WritableSheet sheet;
                                ArrayList<Attribute> attributes = new ArrayList<>();
                                try{
                                    Document document = Jsoup.connect("http://mappings.dbpedia.org/server/templatestatistics/vi/?template="+ URLEncoder.encode(title))
                                            .header("Accept-Encoding", "gzip, deflate")
                                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                                            .maxBodySize(0)
                                            .timeout(600000)
                                            .get();
                                    Elements elements = document.select("tr.success");
                                    for(Element e: elements){
                                        Elements e1 = e.select("td");
                                        String name = e1.get(1).text();
                                        String count = e1.get(0).text();
                                        if(Integer.valueOf(count) > 0){
                                            Attribute attribute = new Attribute();
                                            attribute.setName(name);
                                            attribute.setCount(count);
                                            attributes.add(attribute);
                                        }
                                        //System.out.println(right + " -- " +left);
                                    }
                                    elements = document.select("tr.danger");
                                    for(Element e: elements){
                                        Elements e1 = e.select("td");
                                        String name = e1.get(1).text();
                                        String count = e1.get(0).text();
                                        if(Integer.valueOf(count) > 0){
                                            Attribute attribute = new Attribute();
                                            attribute.setName(name);
                                            attribute.setCount(count);
                                            attributes.add(attribute);
                                        }
                                        //System.out.println(right + " -- " +left);
                                    }
                                }catch (Exception e){
                                    e.getMessage();
                                }
                                try{
                                    workbook = Workbook.createWorkbook(new File(FOLDER + File.separator + title + ".xls"));
                                    sheet = workbook.createSheet("Sheet 1", 0);
                                    for(Attribute a: attributes){
                                        sheet.addCell(new Label(0, index, a.getName()));
                                        sheet.addCell(new Number(1, index, Double.valueOf(a.getCount())));
                                        index++;
                                    }

                                    workbook.write();
                                    workbook.close();
                                }catch (Exception e){
                                    e.getMessage();
                                }
//                            }
//                        }).start();

                    //}

                }catch (Exception e){
                    e.getMessage();
                }
            }
        }).start();


    }
}
