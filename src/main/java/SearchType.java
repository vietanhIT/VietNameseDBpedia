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
 * Created by vieta on 21/11/2016.
 */
public class SearchType {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public SearchType(){
        try{
            init("type");
        }catch (Exception e){

        }
    }

    public SearchType(String indexDirectoryPath) throws IOException {
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
            throws IOException, ParseException {
        query = new TermQuery(new Term(searchQuery));
        return indexSearcher.search(query, 10);
    }

    public TopDocs search(Query query) throws IOException, ParseException{
        return indexSearcher.search(query, 10);
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
            SearchType search = new SearchType("type");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term("page", "http://wikidata.dbpedia.org/resource/Q9705");
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                //System.out.println("Value: "+ doc.get("page")+ doc.get("type"));
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    private HashMap<String,String> hashMap = new HashMap<>();

    public String searchType(String link){
        String type="";
        try{
            Term term = new Term("page", link);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = getDocument(scoreDoc);
                if(doc.get("page").equals(link)){
                    //System.out.println("Value: "+ doc.get("page")+ doc.get("type"));
//                    hashMap.put(link,doc.get("type"));
                    type = doc.get(Constants.TYPE);
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
        return type;
    }
}
