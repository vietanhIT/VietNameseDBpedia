import model.Property;
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
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vieta on 20/2/2017.
 */
public class SearchDBpediaAllDump {

    IndexSearcher indexSearcher;
    QueryParser queryParser;
    Query query;

    public SearchDBpediaAllDump(String indexDirectoryPath) throws IOException {
        Directory indexDirectory =
                FSDirectory.open(new File(indexDirectoryPath));
        indexSearcher = new IndexSearcher(indexDirectory);
        queryParser = new QueryParser(Version.LUCENE_20,
                "page",
                new StandardAnalyzer(Version.LUCENE_20));
    }

    public TopDocs search(Query query) throws IOException, ParseException {
        return indexSearcher.search(query, 100);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException{
        return indexSearcher.doc(scoreDoc.doc);
    }

    public static void main(String[] args){
        try{
            SearchDBpediaAllDump search = new SearchDBpediaAllDump("ALL_DB_DUMP");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term(Constants.PAGE, "http://dbpedia.org/resource/Hanoi");
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                System.out.println("Value: "+ doc.get(Constants.PAGE)+ "--"+ doc.get(Constants.PROPERTY)+"--"+doc.get(Constants.VALUE)+"--"+doc.get(Constants.VALUE_TYPE));
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    public ArrayList<Property> getAllPropertyFromDBpediaByTitleArticle(String name){
        ArrayList<Property> properties = new ArrayList<>();
        String path = "http://dbpedia.org/resource/" + name;
        String owlFile = "dbpedia_2016-04.owl";
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        HashMap<String, String> propertyNames = new HashMap<>();
        try{
            SearchDBpediaAllDump search = new SearchDBpediaAllDump("ALL_DB_DUMP");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                //System.out.println("Value: "+ doc.get(Constants.PAGE)+ "--"+ doc.get(Constants.PROPERTY)+"--"+doc.get(Constants.VALUE)+"--"+doc.get(Constants.VALUE_TYPE));
                HashMap<String, String> h1 = airpediaOntology.getProperty(doc.get(Constants.PROPERTY));
                Property property = new Property();
                String range = null;
                if (h1 != null) {
                    range = h1.get("range");
                }
                if(range==null||!range.equals("TimePeriod")){
                    propertyNames.put(doc.get(Constants.PROPERTY), doc.get(Constants.VALUE));
                    property.setName(doc.get(Constants.PROPERTY));
                    property.setValue(doc.get(Constants.VALUE));
                    property.setObject(doc.get(Constants.VALUE_TYPE).equals("object"));
                    property.setRange(range);
                    properties.add(property);
                }else{
                    searchTermPoriod(search, doc.get(Constants.VALUE),airpediaOntology, properties);
                }

            }
            Set<String> keys = propertyNames.keySet();
            if(keys.contains("populationTotal")&keys.contains("areaTotal")){
                Double populationTotal = Double.valueOf(propertyNames.get("populationTotal"));
                Double areaTotal = Double.valueOf(propertyNames.get("areaTotal"));
                Property property = new Property();
                property.setName("populationDensity");
                property.setValue(String.valueOf(populationTotal/areaTotal));
                property.setObject(false);
                property.setRange("double");
                properties.add(property);
            }
            if(keys.contains("populationUrban")&keys.contains("areaUrban")){
                Double populationUrbanTotal = Double.valueOf(propertyNames.get("populationUrban"));
                Double areaUrbanTotal = Double.valueOf(propertyNames.get("areaUrban"));
                Property property = new Property();
                property.setName("populationUrbanDensity");
                property.setValue(String.valueOf(populationUrbanTotal/areaUrbanTotal));
                property.setObject(false);
                property.setRange("double");
                properties.add(property);
            }
            if(keys.contains("populationMetro")&keys.contains("areaMetro")){
                Double populationMetroTotal = Double.valueOf(propertyNames.get("populationMetro"));
                Double areaMetroTotal = Double.valueOf(propertyNames.get("areaMetro"));
                Property property = new Property();
                property.setName("populationMetroDensity");
                property.setValue(String.valueOf(populationMetroTotal/areaMetroTotal));
                property.setObject(false);
                property.setRange("double");
                properties.add(property);
            }
        }catch (Exception e){
            e.getMessage();
        }

        return properties;
    }

    public ArrayList<String> getAllPropertyNameFromDBpediaByTitleArticle(String name){
        ArrayList<String> properties = new ArrayList<>();
        String path = "http://dbpedia.org/resource/" + name;
        String owlFile = "dbpedia_2016-04.owl";
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        try{
            SearchDBpediaAllDump search = new SearchDBpediaAllDump("ALL_DB_DUMP");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                //System.out.println("Value: "+ doc.get(Constants.PAGE)+ "--"+ doc.get(Constants.PROPERTY)+"--"+doc.get(Constants.VALUE)+"--"+doc.get(Constants.VALUE_TYPE));
                HashMap<String, String> h1 = airpediaOntology.getProperty(doc.get(Constants.PROPERTY));
                Property property = new Property();
                String range = null;
                if (h1 != null) {
                    range = h1.get("range");
                }
                if(range==null||!range.equals("TimePeriod")){
                    if(!properties.contains(doc.get(Constants.PROPERTY))){
                        String p = doc.get(Constants.PROPERTY);
                        if(p.contains("foaf:")){
                            p.replace("foaf:","");
                        }
//                        properties.add(doc.get(Constants.PROPERTY));
                        properties.add(p);
                    }
                }else{
                    searchTermPoriodName(search, doc.get(Constants.VALUE),airpediaOntology, properties);
                }

            }
        }catch (Exception e){
            e.getMessage();
        }

        return properties;
    }

    public ArrayList<Property> getAllPropertyFromDBpediaByTitleArticle_de(String name){
        ArrayList<Property> properties = new ArrayList<>();
        String path = "http://de.dbpedia.org/resource/" + name;
        String owlFile = "dbpedia_2016-04.owl";
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        try{
            SearchDBpediaAllDump search = new SearchDBpediaAllDump("ALL_DB_DUMP_DE");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                //System.out.println("Value: "+ doc.get(Constants.PAGE)+ "--"+ doc.get(Constants.PROPERTY)+"--"+doc.get(Constants.VALUE)+"--"+doc.get(Constants.VALUE_TYPE));
                HashMap<String, String> h1 = airpediaOntology.getProperty(doc.get(Constants.PROPERTY));
                Property property = new Property();
                String range = null;
                if (h1 != null) {
                    range = h1.get("range");
                }
                if(range==null||!range.equals("TimePeriod")){
                    property.setName(doc.get(Constants.PROPERTY));
                    property.setValue(doc.get(Constants.VALUE));
                    property.setObject(doc.get(Constants.VALUE_TYPE).equals("object"));
                    property.setRange(range);
                    properties.add(property);
                }else{
                    searchTermPoriod(search, doc.get(Constants.VALUE),airpediaOntology, properties);
                }

            }
        }catch (Exception e){
            e.getMessage();
        }

        return properties;
    }

    public ArrayList<Property> getAllPropertyFromDBpediaByTitleArticle_nl(String name){
        ArrayList<Property> properties = new ArrayList<>();
        String path = "http://nl.dbpedia.org/resource/" + name;
        String owlFile = "dbpedia_2016-04.owl";
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        try{
            SearchDBpediaAllDump search = new SearchDBpediaAllDump("ALL_DB_DUMP_NL");
//            Term term = new Term("page", "http://dbpedia.org/resource/Actrius");
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                //System.out.println("Value: "+ doc.get(Constants.PAGE)+ "--"+ doc.get(Constants.PROPERTY)+"--"+doc.get(Constants.VALUE)+"--"+doc.get(Constants.VALUE_TYPE));
                HashMap<String, String> h1 = airpediaOntology.getProperty(doc.get(Constants.PROPERTY));
                Property property = new Property();
                String range = null;
                if (h1 != null) {
                    range = h1.get("range");
                }
                if(range==null||!range.equals("TimePeriod")){
                    property.setName(doc.get(Constants.PROPERTY));
                    property.setValue(doc.get(Constants.VALUE));
                    property.setObject(doc.get(Constants.VALUE_TYPE).equals("object"));
                    property.setRange(range);
                    properties.add(property);
                }else{
                    searchTermPoriod(search, doc.get(Constants.VALUE),airpediaOntology, properties);
                }

            }
        }catch (Exception e){
            e.getMessage();
        }

        return properties;
    }

    private void searchTermPoriod(SearchDBpediaAllDump search, String value, AirpediaOntology airpediaOntology, ArrayList<Property> properties){
        String path = "http://dbpedia.org/resource/" + value;
        try{
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                HashMap<String, String> h1 = airpediaOntology.getProperty(doc.get(Constants.PROPERTY));
                Property property = new Property();
                String range = null;
                if (h1 != null) {
                    range = h1.get("range");
                }
                property.setName(doc.get(Constants.PROPERTY));
                property.setValue(doc.get(Constants.VALUE));
                property.setObject(doc.get(Constants.VALUE_TYPE).equals("object"));
                property.setRange(range);
                properties.add(property);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }

    private void searchTermPoriodName(SearchDBpediaAllDump search, String value, AirpediaOntology airpediaOntology, ArrayList<String> properties){
        String path = "http://dbpedia.org/resource/" + value;
        try{
            Term term = new Term(Constants.PAGE,path );
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search.search(query);
            for(ScoreDoc scoreDoc : hits.scoreDocs) {
                Document doc = search.getDocument(scoreDoc);
                if(!properties.contains(doc.get(Constants.PROPERTY))){
                    properties.add(doc.get(Constants.PROPERTY));
                }
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}
