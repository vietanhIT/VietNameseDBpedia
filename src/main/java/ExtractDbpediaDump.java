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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vieta on 14/11/2016.
 */

public class ExtractDbpediaDump {

    static WriteFile writeFile = new WriteFile("index-draft.txt");

    static class LuceneStatementManager extends RDFHandlerBase {

        private IndexWriter indexWriter;
        private String lang;
        Pattern itTrick = Pattern.compile("(.*)__.+__1");
        Logger logger = Logger.getLogger(ExtractDbpediaDump.class.getName());
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

            String simpleSubject = DBpediaOntology.cleanGenericName(subject);
            String simpleRelation = DBpediaOntology.cleanGenericName(relation);
//            String simpleObject = DBpediaOntology.cleanGenericName(object);
//
//            if (simpleRelation.startsWith("http")) {
//                return;
//            }

            Matcher m = itTrick.matcher(simpleSubject);
            if (m.find()) {
                simpleSubject = m.group(1);
            }

//            System.out.println(simpleSubject);
//            System.out.println(object);

            Document doc = new Document();
            doc.add(new Field(Constants.PAGE, simpleSubject, Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Constants.LANGUAGE, lang, Field.Store.YES, Field.Index.NOT_ANALYZED));
//            doc.add(new Field("property", simpleRelation, Field.Store.YES, Field.Index.NOT_ANALYZED));
            doc.add(new Field(Constants.VALUE, object, Field.Store.YES, Field.Index.NOT_ANALYZED));

//            doc.add(new Field("lang-page", lang + ":" + simpleSubject, Field.Store.YES, Field.Index.NOT_ANALYZED));
//            doc.add(new Field("lang-page-property", lang + ":" + simpleSubject + ":" + simpleRelation, Field.Store.YES, Field.Index.NOT_ANALYZED));

            try {
                indexWriter.addDocument(doc);
                count++;
            } catch (Exception e) {
                logger.error(e.getMessage());
            }

			System.out.println(simpleSubject);
            writeFile.write1Line(simpleSubject);
//			System.out.println(relation);
//			System.out.println(object);

//			System.out.println(simpleRelation);
//			System.out.println();
        }

    }
    public static void main(String args[]){
        Logger logger= Logger.getLogger(ExtractDbpediaDump.class.getName());
        //logger.debug("hello");
        String outLucene = "index-nl"; //index-vi, index-en, index-de
        IndexWriter indexWriter=null;
        try {
            indexWriter = new IndexWriter(FSDirectory.open(new File(outLucene)), new WhitespaceAnalyzer());
        } catch (Exception e) {
            //logger.error("Lucene error!");
            System.exit(1);
        }
        String inDBpediaFolder = "nldump"; //vidump, endump,dedump
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
                        LuceneStatementManager handler = new LuceneStatementManager(indexWriter,lang);
                        rdfParser.setRDFHandler(handler);

                        ParserConfig config = rdfParser.getParserConfig();
                        config.addNonFatalError(BasicParserSettings.VERIFY_DATATYPE_VALUES);
                        rdfParser.setParserConfig(config);

                        rdfParser.parse(inputStreamReader, "");

                    }catch (Exception e){
                        logger.error(e.getMessage());
                    }
                }
            }
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        try {
            //logger.info("Optimizing and closing index");
            indexWriter.optimize();
            indexWriter.close();
            writeFile.close();
        } catch (IOException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
    }
}
