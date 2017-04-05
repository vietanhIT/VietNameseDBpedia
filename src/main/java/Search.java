import java.io.File;
import java.io.IOException;
import java.util.HashMap;

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

/**
 * Created by vieta on 17/11/2016.
 */
public class Search {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public Search(){

    }

    public Search(String indexDirectoryPath) throws IOException {
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

    public TopDocs search( String searchQuery)
            throws IOException, ParseException{
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
            Search search = new Search(Constants.VIETNAMESE_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
                    if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
                        System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
                    }
               // }
            }
        }catch (Exception e){
            e.getMessage();
        }

//        Search search = new Search();
//        SearchType searchType = new SearchType();
//        search.searchInterlanguageLink("Holacanthus_ciliaris",searchType);
//
//        System.out.print(hashMapInterLanguageLink.get(Constants.RUSSIA));

    }

    public static HashMap<String, String> getInterLanguageLink(String queryname){
        HashMap<String, String> hashMap = new HashMap<>();
        try{
            Search search = new Search(Constants.ENGLISH_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.VIETNAMESE+"/resource/",""));
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
            Search search = new Search(Constants.GERMANY_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.VIETNAMESE+"/resource/",""));
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
            Search search = new Search(Constants.NETHERLAND_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.VIETNAMESE)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.VIETNAMESE+"/resource/",""));
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
            Search search = new Search(Constants.VIETNAMESE_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.ENGLISH)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.ENGLISH+"/resource/",""));
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
            Search search = new Search(Constants.VIETNAMESE_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.GERMANY)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.GERMANY+"/resource/",""));
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
            Search search = new Search(Constants.VIETNAMESE_INDEX);
            Term term = new Term(Constants.PAGE, queryname);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                //if(p.equals(queryname)){
//                if(search.findLanguage(doc.get(Constants.VALUE))!=-1){
//                    System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
//                }
                if(doc.get(Constants.VALUE).contains(Constants.NETHERLAND)){
                    hashMap.put(queryname, doc.get(Constants.VALUE).replace(Constants.NETHERLAND+"/resource/",""));
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
            init(Constants.VIETNAMESE_INDEX);
            Term term = new Term(Constants.PAGE, nameArticle);
            //create the term query object
            Query query = new TermQuery(term);  //PrefixQuery
            TopDocs hits = search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = getDocument(scoreDoc);
                String p = doc.get(Constants.PAGE);
                if(p.equals(nameArticle)){
                    int index = findLanguage(doc.get(Constants.VALUE));
                    if(index!=-1){
                        //System.out.println("Value: "+ doc.get("page")+ doc.get("value"));
                        //hashMapInterLanguageLink.put(Language.getLanguages().get(index),doc.get(Constants.VALUE));
                        String type = searchType.searchType((doc.get(Constants.VALUE)));
                        if(!type.equals("")){
                            hashMapInterLanguageLink.put(Language.getLanguages().get(index),type);
                        }
                        //searchType.searchType(doc.get(Constants.VALUE));
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
