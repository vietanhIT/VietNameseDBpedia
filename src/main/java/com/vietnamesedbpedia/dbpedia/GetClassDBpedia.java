package com.vietnamesedbpedia.dbpedia;

import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;
import org.fbk.cit.hlt.thewikimachine.util.CharacterTable;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vieta on 22/11/2016.
 */
public class GetClassDBpedia {
    private static HashMap<String, String> hashMap = new HashMap<>();
    static AirpediaOntology airpediaOntology;
    static WriteFile writeFile;
    private static PrintWriter titleIdWriter;

    public GetClassDBpedia(){
        writeFile = new WriteFile();
    }

    private void init(){
    }

    private static ArrayList<String> listTitle = new ArrayList<>();

    public static void main(String args[]){
        Logger logger = Logger.getLogger(GetClassDBpedia.class.getName());
        Search search = new Search();
        SearchType searchType = new SearchType();
//        airpediaOntology = new AirpediaOntology(com.vietnamesedbpedia.dbpedia.Constants.ONTOLOGY_2016);
        SearchAllParentClass searchAllParentClass = new SearchAllParentClass();
        writeFile = new WriteFile("result_6_12.txt");
        try{
            titleIdWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("result_draft_6_12.txt"), "UTF-8")));
        }catch (Exception e){
            e.getMessage();
        }
        listTitle.add("Cá_trôi_Ấn_Độ");
//        listTitle.add("Wales");
//        listTitle.add("FreeBSD");
//        listTitle.add("Wales");
//        listTitle.add("B");
//        listTitle.add("Cộng_Hòa_Xã_Hội_Chủ_Nghĩa_Việt_Nam");
//        listTitle.add("Myasishchev_M-18");
//        listTitle.add("Thuận_Liệt_Lương_hoàng_hậu");
//        listTitle.add("Cộng_hòa_Síp");
        ReadFile readFile= new ReadFile("title_6_12_draft3.txt");
        String str;
        while ((str = readFile.readLine())!=null){
            str = str.split(CharacterTable.HORIZONTAL_TABULATION+"")[0];
//        for(String str: listTitle){
////        {
            hashMap = search.searchInterlanguageLink(str,searchType);
//
            HashMap<String, ArrayList<String>> mapClass = new HashMap<>();
            ArrayList<String> listClass = new ArrayList<>();
            for(String key: hashMap.keySet()){
                listClass = searchAllParentClass.search(hashMap.get(key));
                mapClass.put(key,listClass);
            }

            //logger.debug(mapClass.toString());

//        com.vietnamesedbpedia.dbpedia.GetClassDBpedia getClassDBpedia= new com.vietnamesedbpedia.dbpedia.GetClassDBpedia();
//        getClassDBpedia.constructTree(mapClass);
//        getClassDBpedia.increase(mapClass);
//        getClassDBpedia.reIncrease();
//        getClassDBpedia.printTree();

            Tree tree = new Tree();
            tree.constructTree(mapClass);
            tree.increase(mapClass,hashMap);
            tree.reIncrease();
            //tree.printTree();
            tree.findMaxNode();
            logger.debug(str + " --> " + tree.printMaxNode());
            writeFile.write1Line(tree.printMaxNode());
            //synchronized (com.vietnamesedbpedia.dbpedia.GetClassDBpedia.class){
                //titleIdWriter.println(str + " --> " + tree.printMaxNode());
            //}

//            try{
//                Thread.sleep(1000);
//            }catch(Exception e){
//                e.getMessage();
//            }


            hashMap.clear();
            mapClass.clear();
            listClass.clear();
        }
        readFile.close();
        writeFile.close();
    }

    public void closeWriteFile(){
        writeFile.close();
    }

    public void getDBpediaClass(String a){
        Search search = new Search();
        SearchType searchType = new SearchType();
//        airpediaOntology = new AirpediaOntology(com.vietnamesedbpedia.dbpedia.Constants.ONTOLOGY_2016);
        SearchAllParentClass searchAllParentClass = new SearchAllParentClass();

        hashMap = search.searchInterlanguageLink(a,searchType);
//
        HashMap<String, ArrayList<String>> mapClass = new HashMap<>();
        ArrayList<String> listClass = new ArrayList<>();
        for(String key: hashMap.keySet()){
            listClass = searchAllParentClass.search(hashMap.get(key));
            mapClass.put(key,listClass);
        }

        //logger.debug(mapClass.toString());

//        com.vietnamesedbpedia.dbpedia.GetClassDBpedia getClassDBpedia= new com.vietnamesedbpedia.dbpedia.GetClassDBpedia();
//        getClassDBpedia.constructTree(mapClass);
//        getClassDBpedia.increase(mapClass);
//        getClassDBpedia.reIncrease();
//        getClassDBpedia.printTree();

        Tree tree = new Tree();
        tree.constructTree(mapClass);
        tree.increase(mapClass,hashMap);
        tree.reIncrease();
        //tree.printTree();
        tree.findMaxNode();
        //logger.debug(tree.printMaxNode());
        System.out.println(a + " --> " + tree.printMaxNode());
        hashMap.clear();
        mapClass.clear();
        listClass.clear();
        //writeFile.write1Line(a + " --> " + tree.printMaxNode());
    }

    public static ArrayList<String> getDBpediaClassFromTitle(String a){
        Search search = new Search();
        SearchType searchType = new SearchType();
        SearchAllParentClass searchAllParentClass = new SearchAllParentClass();

        hashMap = search.searchInterlanguageLink(a,searchType);
        HashMap<String, ArrayList<String>> mapClass = new HashMap<>();
        ArrayList<String> listClass = new ArrayList<>();
        for(String key: hashMap.keySet()){
            listClass = searchAllParentClass.search(hashMap.get(key));
            mapClass.put(key,listClass);
        }

        Tree tree = new Tree();
        tree.constructTree(mapClass);
        tree.increase(mapClass,hashMap);
        tree.reIncrease();
        tree.findMaxNode();
        ArrayList<String> list = searchAllParentClass.search(tree.printMaxNode());
        return list;
    }

    private ArrayList<Node> allListNode = new ArrayList<>();

    private void constructTree(HashMap<String, ArrayList<String>> mapClass){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Node> listChild;
        Node node,parentNode = new Node();
//        for(String key: mapClass.keySet()){
//            list = mapClass.get(key);
//            break;
//        }

        int size = list.size();
        int depth = 0;

//        node = new com.vietnamesedbpedia.dbpedia.Node();
//        node.setName(list.get(size-1));
//        listChild = new ArrayList<>();
//        node.setListChild(listChild);
//        node.setDepth(depth);

        node = new Node();
        node.setName(Constants.TYPE_THING);
        listChild = new ArrayList<>();
        node.setListChild(listChild);
        node.setDepth(depth);

        allListNode.add(node);

        for(int i=size-1;i>=0;i--){
            depth++;
            parentNode = node;
            node = new Node();
            node.setParent(parentNode);
            listChild.add(node);
            node.setName(list.get(i));
            node.setDepth(depth);
            listChild = new ArrayList<>();
            node.setListChild(listChild);
            allListNode.add(node);
        }

        int index=0,i=0;

        for(String key: mapClass.keySet()){
            if(index==0){
                index++;
            }else{
                    list = mapClass.get(key);
                    size = list.size();
                    for(i=size-1;i>=0;i--){
                        node = searchInAllNode(list.get(i));
                        if(node!=null){
                            parentNode = node;
                        }else{
                            node = new Node();
                            node.setParent(parentNode);
                            node.setName(list.get(i));
                            listChild = new ArrayList<>();
                            node.setListChild(listChild);
                            int d = parentNode.getDepth()+1;
                            node.setDepth(d);
                            parentNode.getListChild().add(node);
                            allListNode.add(node);
                            parentNode = node;
                        }
                    }
                    index++;
                }
        }
    }

    public Node searchInAllNode(String name){
        for(Node node:allListNode){
            if(node.getName().equals(name)){
                return node;
            }
        }
        return null;
    }

    public void printTree(){
        for(Node node: allListNode){
            System.out.println(node.getName()+"  "+node.getPriority());
        }
    }

    public void increase(HashMap<String, ArrayList<String>> mapClass){
        ArrayList<String> list;
        for(String key: hashMap.keySet()){
            String a = hashMap.get(key);
            for(Node node:allListNode){
                if(node.getName().equals(a)){
                    int f = node.getFrequency()+1;
                    node.setFrequency(f);
                    node.setPriority(f);
//                    do{
//                        for(com.vietnamesedbpedia.dbpedia.Node n:node.getListChild()){
//                            n.setFrequency(n.getFrequency()+1);
//                            node = n;
//                        }
//                    }while(node.isChild());
//
//                    break;
                }
            }
        }
    }

    public void reIncrease(){
        Node temp = new Node();
        for(Node node: allListNode){
            if(node.getFrequency()!=0){
                temp = node;
                while(temp.isParent()){
                    temp = temp.getParent();
                    int fParent = temp.getFrequency();
                    node.setPriority(node.getPriority()+fParent);
                }
            }
        }
    }




}
