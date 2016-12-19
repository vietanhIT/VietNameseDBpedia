import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.util.DBpediaOntology;
import org.openrdf.model.Statement;
import org.openrdf.rio.*;
import org.openrdf.rio.helpers.BasicParserSettings;
import org.openrdf.rio.helpers.RDFHandlerBase;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by vieta on 23/11/2016.
 */
public class ExtractTitleDbpedia {
    static GetClassDBpedia getClassDBpedia = new GetClassDBpedia();
    static WriteFile writeFile = new WriteFile("title_vi.txt");

    static class HandleTitle extends RDFHandlerBase{
        @Override
        public void handleStatement(Statement st) throws RDFHandlerException {
            super.handleStatement(st);
            String subject = st.getSubject().stringValue();
            subject = DBpediaOntology.cleanGenericName(subject);
//            getClassDBpedia.getDBpediaClass(subject);
            writeFile.write1Line(subject);
            System.out.println(subject);
        }
    }

    public static void main(String args[]){
        Logger logger = Logger.getLogger(ExtractTitleDbpedia.class.getName());
        String inDBpediaFolder = "title-vi";
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
                        HandleTitle handler = new HandleTitle();
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

        try{
            getClassDBpedia.closeWriteFile();
        }catch (Exception e){
            e.getMessage();
        }
    }
}
