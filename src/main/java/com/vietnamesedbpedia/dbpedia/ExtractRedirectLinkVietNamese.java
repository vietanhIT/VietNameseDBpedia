package com.vietnamesedbpedia.dbpedia;

import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.fbk.cit.hlt.thewikimachine.util.DBpediaOntology;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vietanknb on 15/5/2017.
 */
public class ExtractRedirectLinkVietNamese  {
    static class HandleRedirect extends RDFHandlerBase {

        private IndexWriter indexWriter;
        private String lang;
        Pattern itTrick = Pattern.compile("(.*)__.+__1");

        public HandleRedirect(IndexWriter indexWriter, String lang) {
            this.indexWriter = indexWriter;
            this.lang = lang;
        }

        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            super.handleStatement(st);
            String subject = st.getSubject().stringValue();
            String simpleSubject = DBpediaOntology.cleanGenericName(subject);
            String object = st.getObject().stringValue();
            String simpleObject = DBpediaOntology.cleanGenericName(object);


            Matcher m = itTrick.matcher(simpleSubject);
            if (m.find()) {
                simpleSubject = m.group(1);
            }
            Document doc = new Document();
            doc.add(new Field(Constants.PAGE, simpleSubject, Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Constants.VALUE, simpleObject, Field.Store.YES, Field.Index.NOT_ANALYZED));

//            System.out.println(simpleSubject +"  " + simpleObject);
            try {
                indexWriter.addDocument(doc);
            } catch (Exception e) {
//                logger.error(e.getMessage());
            }

        }
    }

    public static void main(String args[]){
//        Logger logger = Logger.getLogger(ExtractRedirectLinkVietNamese.class.getName());
        String inDBpediaFolder = "redirect-vi";
        String outLucene = "index-redirect-vi";
        IndexWriter indexWriter=null;
        try {
            indexWriter = new IndexWriter(FSDirectory.open(new File(outLucene)), new WhitespaceAnalyzer());
        } catch (Exception e) {
            //logger.error("Lucene error!");
            System.exit(1);
        }
        File folder = new File(inDBpediaFolder);
        File[] listOfFiles = folder.listFiles();
        try{
            for(File file : listOfFiles){
                if(file.isFile()){
                    String nameFile = file.getAbsolutePath();
                    String lang = nameFile.substring(nameFile.length() - 6, nameFile.length() - 4);
                    try{
                        InputStream fileInputStream = new FileInputStream(nameFile);
                        InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream,"utf8");
                        RDFParser rdfParser = Rio.createParser(RDFFormat.NTRIPLES);
                        HandleRedirect handler = new HandleRedirect(indexWriter, lang);
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

//        try{
//            getClassDBpedia.closeWriteFile();
//        }catch (Exception e){
//            e.getMessage();
//        }

        try {
            //logger.info("Optimizing and closing index");
            indexWriter.optimize();
            indexWriter.close();
            //writeFile.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
