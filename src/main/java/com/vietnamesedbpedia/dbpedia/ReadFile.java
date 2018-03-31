package com.vietnamesedbpedia.dbpedia;

import jxl.Workbook;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.*;
import java.util.*;

/**
 * Created by vieta on 24/11/2016.
 */
public class ReadFile {
    BufferedReader br = null;
    public ReadFile(String name){
        try{
            br = new BufferedReader(new FileReader(name));

        }catch (Exception e){
            e.getMessage();
        }
    }

    public String readLine(){
        try{
            String a=br.readLine();
            if(a!=null){
                return a;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return null;
    }

    public void close(){
        try {
            br.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String [] args){
        try{
           // BufferedReader bufferedReader = new BufferedReader(new FileReader("title_infobox_6_12_draft_2.txt"));
            BufferedReader bufferedReader = new BufferedReader(new FileReader("infobox_6_12_draft3.txt"));
            BufferedReader bufferedReader1 = new BufferedReader(new FileReader("result_6_12.txt"));
//            PrintWriter printWriter = new PrintWriter("infobox_6_12_draft3.txt");
            //PrintWriter printWriter1 = new PrintWriter("title_6_12_draft3.txt");

            HashMap<String, Integer> hashMap = new HashMap<>();
            HashMap<String, ArrayList<Integer> > hashMap1= new HashMap<>();
            HashMap<String, ArrayList<String> > infoboxClassHashMap = new HashMap<>();
            ArrayList<String> listClass = new ArrayList<>();

            String line;
            int i = 0;
//            int j=0;

            while ((line = bufferedReader1.readLine())!=null){
                listClass.add(line);
            }
//            for(int j=0; j<listClass.size(); j++ ){
//                System.out.println(j+"  "+listClass.get(j));
//            }

            while ((line = bufferedReader.readLine())!=null){
                if(line.length()>0){
                    if(line.contains("<!")){
                        int index = line.indexOf('<');
                        line = line.substring(0,index);
                    }
                    line = line.replace("_"," ").trim().replace(" ", "_");
                    line = line.toLowerCase();
                    if(hashMap.containsKey(line)){
                        int value = hashMap.get(line);
                        value++;
                        hashMap.put(line, value);
                        ArrayList<Integer> list = hashMap1.get(line);
                        list.add(i);
                        hashMap1.put(line, list);
                    }else{
                        hashMap.put(line, 1);
                        ArrayList<Integer> list = new ArrayList<>();
                        list.add(i);
                        hashMap1.put(line,list);
                    }

                    i++;
                }
            }


            //printWriter1.close();
            //System.out.println(listClass.size());
            Set<String> keys = hashMap1.keySet();
            //System.out.println(hashMap1.size());
            for (String a: keys){
//                System.out.println(a+ " ----- "+ hashMap1.get(a).toString());

                ArrayList<String> list = new ArrayList<>();
                ArrayList<Integer> listId = hashMap1.get(a);
                for(int id: listId){
//                    System.out.println(id);
//                    try{
                        list.add(listClass.get(id));
//                    }catch (Exception e){
//                        e.getMessage();
//                    }
//                    System.out.println(a + " --- "+ id + " ----- "+ listClass.get(id));
                }
                infoboxClassHashMap.put(a, list);
//                for (String b: list){
//                    System.out.print(a+ b+" ---- ");
//                }
//                System.out.println();
            }
            //System.out.println(infoboxClassHashMap.size());
            Set<String> keys1 = infoboxClassHashMap.keySet();
            for (String a: keys1){
                //System.out.println(a+" ---- "+infoboxClassHashMap.get(a).toString());
            }

            int k = 0;

            ExtractAllInfoboxVietNamese extractAllInfoboxVietNamese = new ExtractAllInfoboxVietNamese();

            try{
                WritableWorkbook workbook = Workbook.createWorkbook(new File("infobox_count_final2.xls"));
                WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

                for (String a: keys) {
//                    ArrayList<String> list = count(infoboxClassHashMap.get(a));
                    System.out.println();
                    //System.out.println(a+"--"+count(infoboxClassHashMap.get(a)));
                    if(extractAllInfoboxVietNamese.searchVerifyInfobox(a)){
                        ArrayList<Node> list = count(infoboxClassHashMap.get(a));
                        //if (list.size() == 1) {
                        if (!list.get(0).equals("null")&&(list.size()==1)) {
                            sheet.addCell(new Label(0, k, a)); // add a String to cell A1
                            sheet.addCell(new Number(1, k, hashMap.get(a))); // add number 100 to cell A2
//                            String temp="";
//                            for(com.vietnamesedbpedia.dbpedia.Node n:list){
//                                temp += n.getName() +";";
//                            }
                            sheet.addCell(new Label(2, k, list.get(0).getName()));
                            k++;
                        }
                    }

                    //}
                }

                workbook.write();
                workbook.close();
            }catch (Exception e){
                System.out.println(e);
            }
            //printWriter.close();
            bufferedReader.close();
            bufferedReader1.close();
        }catch (Exception e){
            e.getMessage();
        }

    }

    private static ArrayList<Node> count(ArrayList<String> listClass){
        HashMap<String, ArrayList<String>> hashMap = new HashMap<>();
        HashMap<String, String> hashMap1 = new HashMap<>();
        SearchAllParentClass searchAllParentClass = new SearchAllParentClass();
        ArrayList<String> listClass1 = new ArrayList<>();
        int i = 0;
        for (String a: listClass){
//            if(hashMap.containsKey(a)){
//                int value = hashMap.get(a)+1;
//                hashMap.put(a, value);
//            }else{
//                hashMap.put(a,1);
//            }
            listClass1 = searchAllParentClass.search(a);
            hashMap.put(String.valueOf(i),listClass1);
            hashMap1.put(String.valueOf(i),a);
            i++;
        }

        Tree tree = new Tree();
        tree.constructTree(hashMap);
        tree.increase(hashMap,hashMap1);
        tree.reIncrease();
        tree.findMaxNode();
        //tree.printTree();
        return tree.findListMaxNode();

//        Collection<Integer> values = hashMap.values();
//        int max = Collections.max(values);
//        ArrayList<String> list = new ArrayList<>();
//        Set<String> keySet = hashMap.keySet();
//        for(String a: keySet){
//            if(hashMap.get(a)==max){
//                list.add(a);
//            }
//        }
        //return list;
    }

}
