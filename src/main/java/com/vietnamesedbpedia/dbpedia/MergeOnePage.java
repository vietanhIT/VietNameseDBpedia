package com.vietnamesedbpedia.dbpedia;

import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by vietanknb on 21/3/2017.
 */
public class MergeOnePage {

    public static void main(String[] args){
        MergeOnePage mergeOnePage = new MergeOnePage();
        mergeOnePage.merge("Thông_tin_viên_chức", "Hồ_Chí_Minh");
    }

    public void merge(String nameInfobox, String nameWiki){
        String FOLDER = CompareWikiAndDb.MAIN_FOLDER_OUTPUT+File.separator+nameInfobox+File.separator+"wiki";
        try{
            File mainFolder = new File(FOLDER+File.separator+"detail");
            File[] listFileName = mainFolder.listFiles();
            HashMap<String, Double> hashMap = new HashMap<>();
//            HashMap<String, Double> averageHashMap = new HashMap<>();
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");

            for(File a: listFileName){
                if(a.getName().contains(nameWiki)){
                    Workbook w = Workbook.getWorkbook(a,ws);
                    Sheet sheet = w.getSheet(0);
                    for (int i = 0; i < sheet.getRows(); i++) {
                        String attr = sheet.getCell(0,i).getContents().toLowerCase();
                        String pttr = sheet.getCell(1,i).getContents();
                        Double count = Double.valueOf(sheet.getCell(2,i).getContents());
                        String name = attr + ";" + pttr;
                        Set<String> keys = hashMap.keySet();
                        if(keys.contains(name)){
                            if(hashMap.get(name) < count){
                                hashMap.remove(name);
                                hashMap.put(name, count);
                            }
                        }else{
                            hashMap.put(name, count);
                        }
                    }
                }
            }
            Set<String> keys1 = hashMap.keySet();

            HashMap<String, HashMap<String, Double>> mapping = new HashMap<>();
            for(String b: keys1){
                String[] list = b.split(";");
                Double similarity = hashMap.get(b);

                if(!mapping.containsKey(list[0])){
                    HashMap<String, Double> h3 = new HashMap<>();
                    h3.put(list[1],similarity);
                    mapping.put(list[0], h3);
                }else{
                    mapping.get(list[0]).put(list[1],similarity);
                }
            }

            HashMap<String, HashMap<String, Double>> resultMapping = new HashMap<>();
            Set<String> k3s = mapping.keySet();
            for (String c: k3s){
                resultMapping.put(c, new HashMap<String, Double>());
                HashMap<String, Double> h1 = mapping.get(c);
                Collection<Double> doubles = h1.values();
                Double max = Collections.max(doubles);
                Set<String> k2s = h1.keySet();
                for(String a: k2s){
                    if(h1.get(a).equals(max)){
                        resultMapping.get(c).put(a, max);
                    }
                }

            }

            Set<String> k2s = resultMapping.keySet();


            try {
                WritableWorkbook workbook = Workbook.createWorkbook(new File(FOLDER, nameWiki+".xls"));
                WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
                int index =0;
                for (String k : k2s) {
                    HashMap<String, Double> h1 = resultMapping.get(k);
                    Set<String> k1 = h1.keySet();
                    for (String c : k1) {
//                    String[] attrAndpttr = k.split(Pattern.quote(";"));
                        sheet.addCell(new Label(0, index, k));
                        sheet.addCell(new Label(1, index, c));
                        sheet.addCell(new Label(2, index, String.valueOf(h1.get(c))));
                        index++;
                    }
                }

                workbook.write();
                workbook.close();
            } catch (Exception e) {
                e.getMessage();
            }

//            for(File a: listFileName){
//                if(a.getName().contains(nameWiki+"_")){
//                    a.delete();
//                }
//            }
        }catch (Exception e){
            e.getMessage();
        }



    }

}
