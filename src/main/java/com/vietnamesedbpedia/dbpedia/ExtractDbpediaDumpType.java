package com.vietnamesedbpedia.dbpedia;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.fbk.cit.hlt.thewikimachine.util.DBpediaOntology;
import org.openrdf.model.Statement;
import org.openrdf.rio.ParserConfig;
import org.openrdf.rio.RDFFormat;
import org.openrdf.rio.RDFParser;
import org.openrdf.rio.Rio;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.io.*;
import java.util.regex.Pattern;

/**
 * Created by vieta on 21/11/2016.
 */
public class ExtractDbpediaDumpType {
    static class LuceneStatementManager extends RDFHandlerBase {

        private IndexWriter indexWriter;
        private String lang;
        Pattern itTrick = Pattern.compile("(.*)__.+__1");
//        Logger logger = Logger.getLogger(ExtractDbpediaDumpType.class.getName());
        private int count = 0;

        public LuceneStatementManager(IndexWriter indexWriter, String lang) {
            this.indexWriter = indexWriter;
            this.lang = lang;
        }

        public int getCount() {
            return count;
        }

        @Override
        public void handleStatement(Statement st) {
            String subject = st.getSubject().stringValue();
            String relation = st.getPredicate().stringValue();
            String object = st.getObject().stringValue();

//            String simpleSubject = DBpediaOntology.cleanGenericName(subject);
            String simpleRelation = DBpediaOntology.cleanGenericName(relation);
            String simpleObject = DBpediaOntology.cleanGenericName(object);
//
//            if (simpleRelation.startsWith("http")) {
//                return;
//            }

//            Matcher m = itTrick.matcher(simpleSubject);
//            if (m.find()) {
//                simpleSubject = m.group(1);
//            }

//            System.out.println(simpleSubject);
            //System.out.println(simpleObject);

            Document doc = new Document();
            doc.add(new Field(Constants.PAGE, subject, Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Constants.LANGUAGE, lang, Field.Store.YES, Field.Index.NOT_ANALYZED));
//            doc.add(new Field("property", simpleRelation, Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Constants.TYPE, simpleObject, Field.Store.YES, Field.Index.NOT_ANALYZED));

//            doc.add(new Field("lang-page", lang + ":" + simpleSubject, Field.Store.YES, Field.Index.NOT_ANALYZED));
//            doc.add(new Field("lang-page-property", lang + ":" + simpleSubject + ":" + simpleRelation, Field.Store.YES, Field.Index.NOT_ANALYZED));

            try {
                indexWriter.addDocument(doc);
                count++;
            } catch (Exception e) {
                System.out.println(e.getMessage());
            }

//			System.out.println(subject);
//			System.out.println(relation);
//			System.out.println(object);
//
//			System.out.println(simpleRelation);
//			System.out.println();
        }

    }
    public static void main(String args[]){
//        Logger logger= Logger.getLogger(ExtractDbpediaDumpType.class.getName());
        //logger.debug("hello");
        String outLucene = "type";
        IndexWriter indexWriter=null;
        try {
            indexWriter = new IndexWriter(FSDirectory.open(new File(outLucene)), new WhitespaceAnalyzer());
        } catch (Exception e) {
            //logger.error("Lucene error!");
            System.exit(1);
        }
        String inDBpediaFolder = "dbpedia_type";
        File folder = new File(inDBpediaFolder);
        File[] listOfFiles = folder.listFiles();
        String lang;
        try{
            for(File file : listOfFiles){
                if(file.isFile()){
                    String nameFile = file.getAbsolutePath();
                    if(nameFile.contains("wikidata")){
                        lang = "wikidata";
                    }else{
                        lang = nameFile.substring(nameFile.length() - 6, nameFile.length() - 4);
                    }

                    try{
                        InputStream fileInputStream = new FileInputStream(nameFile);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf8");
                        RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
                        LuceneStatementManager handler = new LuceneStatementManager(indexWriter,lang);
                        rdfParser.setRDFHandler(handler);

                        ParserConfig config = rdfParser.getParserConfig();
                        config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
                        rdfParser.setParserConfig(config);

                        rdfParser.parse(inputStreamReader, "");

                    }catch (Exception e){
                        System.out.println(e.getMessage());
                    }
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        try {
            //logger.info("Optimizing and closing index");
            indexWriter.optimize();
            indexWriter.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
