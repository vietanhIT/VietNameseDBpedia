package com.vietnamesedbpedia.dbpedia;

import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vieta on 23/11/2016.
 */
public class Tree {
    private ArrayList<Node> allListNode = new ArrayList<>();
    private Node maxNode;
    private Logger logger;

    public Tree(){
        maxNode = new Node();
        logger = Logger.getLogger(Tree.class.getName());
    }

    public void constructTree(HashMap<String, ArrayList<String>> mapClass){
        ArrayList<String> list = new ArrayList<>();
        ArrayList<Node> listChild;
        Node node,parentNode = new Node();
        for(String key: mapClass.keySet()){
            list = mapClass.get(key);
            break;
        }

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
        int count=0;

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
                        count++;
                        if(count==size){
                            parentNode = allListNode.get(0);
                        }
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

    public void increase(HashMap<String, ArrayList<String>> mapClass,HashMap<String, String> hashMap){
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

    public void findMaxNode(){
        for(Node node: allListNode){
            if(node.getPriority()!=0){
                if(maxNode.isGreater(node.getPriority())){
                    maxNode = node;
                }
            }
        }
    }

    public ArrayList<Node> findListMaxNode(){
        ArrayList<Node> listNode = new ArrayList<>();
        for(Node node: allListNode){
            if(node.getPriority()==maxNode.getPriority()){
                listNode.add(node);
            }
        }
        return listNode;
    }


    public String printMaxNode(){
        return maxNode.getName();
    }


}
