package com.vietnamesedbpedia.program;

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
import java.util.ArrayList;

/**
 * Created by vietanknb on 4/5/2017.
 */
public class SuggestionSearch {
    private IndexSearcher indexSearcher;
    private QueryParser queryParser;

    public SuggestionSearch(String indexDirectoryPath) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));
        indexSearcher = new IndexSearcher(indexDirectory);
        queryParser = new QueryParser(Version.LUCENE_20,
                "page",
                new StandardAnalyzer(Version.LUCENE_20));
    }

    public TopDocs search(Query query) throws IOException, ParseException {
        return indexSearcher.search(query, 10);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException{
        return indexSearcher.doc(scoreDoc.doc);
    }

    public static ArrayList<String> getSuggestion(String queryname){
        ArrayList<String> listSuggestion = new ArrayList<>();
        try{
            SuggestionSearch search = new SuggestionSearch("index-label-vi");
            Term term = new Term("page", queryname);
            //create the term query object
            Query query = new PrefixQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                String p = doc.get("page");
                listSuggestion.add(p);
            }
        }catch (Exception e){
            e.getMessage();
        }
        return listSuggestion;
    }

    public static void main(String[] args){
        System.out.println(SuggestionSearch.getSuggestion("H").toString());
    }
}
