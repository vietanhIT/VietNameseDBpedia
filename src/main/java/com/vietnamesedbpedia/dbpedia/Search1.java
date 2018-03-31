package com.vietnamesedbpedia.dbpedia;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Created by vieta on 5/4/2017.
 */
public class Search1 {
    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public Search1(){

    }

    public Search1(String indexDirectoryPath) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));
        indexSearcher = new IndexSearcher(indexDirectory);
        queryParser = new QueryParser(Version.LUCENE_20,
                "page",
                new StandardAnalyzer(Version.LUCENE_20));
    }

    public void init(String indexDirectoryPath)throws IOException{
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));
        indexSearcher = new IndexSearcher(indexDirectory);
        queryParser = new QueryParser(Version.LUCENE_20,
                "page",
                new StandardAnalyzer(Version.LUCENE_20));
    }

    public TopDocs search(String searchQuery)
            throws IOException, ParseException {
        query = new TermQuery(new Term(searchQuery));
        return indexSearcher.search(query, 10);
    }

    public TopDocs search(Query query) throws IOException, ParseException{
        return indexSearcher.search(query, 200);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException{
        return indexSearcher.doc(scoreDoc.doc);
    }

    public void close() throws IOException{
        indexSearcher.close();
    }
    public static void main(String[] args){
        try{
            String queryname = "Hà_Nội";
            Search1 search = new Search1(Constant.VIETNAMESE_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
                if(search.findLanguage(doc.get(Constant.VALUE))!=-1){
                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }

//        com.vietnamesedbpedia.dbpedia.Search search = new com.vietnamesedbpedia.dbpedia.Search();
//        com.vietnamesedbpedia.dbpedia.SearchType searchType = new com.vietnamesedbpedia.dbpedia.SearchType();
//        search.searchInterlanguageLink("Holacanthus_ciliaris",searchType);
//
//        System.out.print(hashMapInterLanguageLink.get(com.vietnamesedbpedia.dbpedia.Constants.RUSSIA));

    }

    public static HashMap<String, String> getInterLanguageLink(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.ENGLISH_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.VIETNAMESE+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    public static HashMap<String, String> getInterLanguageLink_de(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.GERMANY_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.VIETNAMESE+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    public static HashMap<String, String> getInterLanguageLink_nl(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.NETHERLAND_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.VIETNAMESE+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    public static HashMap<String, String> getViInterLanguageLink(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.VIETNAMESE_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constant.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.ENGLISH)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.ENGLISH+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    public static HashMap<String, String> getViInterLanguageLink_de(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.VIETNAMESE_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constant.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.GERMANY)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.GERMANY+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    public static HashMap<String, String> getViInterLanguageLink_nl(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search1 search = new Search1(Constant.VIETNAMESE_INDEX);
            Term term = new Term(Constant.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(com.vietnamesedbpedia.dbpedia.Constant.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constant.VALUE).contains(Constant.NETHERLAND)){
                    hashMap.put(queryname, doc.get(Constant.VALUE).replace(Constant.NETHERLAND+"/resource/",""));
                }
                // }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return hashMap;
    }

    private static HashMap<String, String> hashMapInterLanguageLink = new HashMap<>();

    public HashMap<String,String> searchInterlanguageLink(String nameArticle, SearchType searchType){
        try{
            init(Constant.VIETNAMESE_INDEX);
            Term term = new Term(Constant.PAGE, nameArticle);
            //create the term query object
            Query query = new TermQuery(term);  //PrefixQuery
            TopDocs hits = search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = getDocument(scoreDoc);
                String p = doc.get(Constant.PAGE);
                if(p.equals(nameArticle)){
                    int index = findLanguage(doc.get(Constant.VALUE));
                    if(index!=-1){
                        //System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
                        //hashMapInterLanguageLink.put(com.vietnamesedbpedia.dbpedia.Language.getLanguages().get(index),doc.get(com.vietnamesedbpedia.dbpedia.Constant.VALUE));
                        String type = searchType.searchType((doc.get(Constant.VALUE)));
                        if(!type.equals("")){
                            hashMapInterLanguageLink.put(Language.getLanguages().get(index),type);
                        }
                        //searchType.searchType(doc.get(com.vietnamesedbpedia.dbpedia.Constant.VALUE));
                    }
                }
            }
        }catch (Exception e){

        }
        return hashMapInterLanguageLink;
    }

    private int findLanguage(String link){
        int size = Language.getLanguages().size();
        for(int i=0; i<size;i++){
            if(link.contains(Language.getLanguages().get(i))) return i;
        }
        return -1;
    }
}
