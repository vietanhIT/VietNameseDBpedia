import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.ExtractorParameters;
import org.fbk.cit.hlt.thewikimachine.util.CharacterTable;
import org.fbk.cit.hlt.thewikimachine.xmldump.AbstractWikipediaExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.WikipediaExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.WikipediaTitleExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplate;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplateParser;

import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vieta on 28/11/2016.
 */
public class ExtractWikipediaTitleAndInfoboxName extends VNAbstractWikipediaExtractor implements WikipediaExtractor {
    static Logger logger = Logger.getLogger(ExtractWikipediaTitleAndInfoboxName.class.getName());

    private PrintWriter titleIdWriter;
    private PrintWriter infoboxWriter;
    private WritableSheet sheet;

    private PrintWriter contentPageTitleWriter;
    WritableWorkbook workbook;
    private Matcher matcher;
    private Pattern pattern;

    public ExtractWikipediaTitleAndInfoboxName(int numThreads, int numPages, Locale locale) {
        super(numThreads, numPages, locale);

    }

    @Override
    public void start(ExtractorParameters extractorParameters) {
        try {

            titleIdWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("title_20_12_draft_2.txt"), "UTF-8")));
            infoboxWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream("infobox_20_12_draft.txt"), "UTF-8")));
            workbook = Workbook.createWorkbook(new File("title_20_12.xls"));
            sheet = workbook.createSheet("Sheet 1", 0);
            //contentPageTitleWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaContentPageFileName()), "UTF-8")));
        } catch (IOException e) {
            logger.error(e);
        }
        startProcess(extractorParameters.getWikipediaXmlFileName());
    }

    @Override
    public void filePage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void disambiguationPage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void categoryPage(String text, String title, int wikiID) {
       // writeTitlePage(title, wikiID,text);
    }

    @Override
    public void templatePage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void redirectPage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void portalPage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void projectPage(String text, String title, int wikiID) {
        //writeTitlePage(title, wikiID,text);
    }

    @Override
    public void contentPage(String text, String title, int wikiID) {
        writeTitlePage(title, wikiID,text);
    }

    private synchronized void writeTitlePage(String title, int wikiID, String text) {
        StringBuilder sb = new StringBuilder();
        try{
//            i++;
//            synchronized (this) {
                if(!title.contains(Constants.REMOVE4)&&!title.contains(Constants.REMOVE3)&&!title.startsWith(Constants.REMOVE2)&&!title.startsWith(Constants.REMOVE6)){
                    pattern = Pattern.compile(Constants.pattern_REMOVE1);
                    matcher = pattern.matcher(title);
                    if(!matcher.find()){
                        pattern = Pattern.compile(Constants.pattern_REMOVE5);
                        matcher = pattern.matcher(title);
                        if(!matcher.find()){
//                            sb.append(title);
//                            sb.append(" ");
//                            sb.append(WikipediaParseTemplateName.nameTemplate(text).replace(" ","_"));
                            titleIdWriter.println(title);
//                            sheet.addCell(new Label(0,i,title));
                            //sheet.addCell(new Label(1,i,WikipediaParseTemplateName.nameTemplate(text).replace(" ","_")));
                            String infobox = WikipediaParseTemplateName.nameTemplate(text);
                            if(!Character.isLetter(infobox.charAt(infobox.length()-1))){
                                infobox = infobox.substring(0,infobox.length()-1);
                            }
                            infoboxWriter.println(infobox);

                        }
                    }
                }
                System.gc();
//                System.out.println(i);
//                sheet.addCell(new Label(0,i,title));
//            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }
    static int i=-1;

    @Override
    public void endProcess() {
        super.endProcess();
        //contentPageTitleWriter.close();
        try{
            titleIdWriter.close();
            infoboxWriter.close();
//            workbook.write();
//            workbook.close();
        }catch (Exception e){
            e.getMessage();
        }
    }

    public static void main(String args[]) throws IOException {
        ExtractorParameters extractorParameters = new ExtractorParameters("viwiki-2016-pages-articles.xml", "testWiki");
        logger.debug(extractorParameters);

        logger.debug("extracting titles (" + extractorParameters.getWikipediaTitleIdFileName() + ")...");
        ExtractWikipediaTitleAndInfoboxName wikipediaExtractor = new ExtractWikipediaTitleAndInfoboxName(10000, 10000, extractorParameters.getLocale());
        wikipediaExtractor.setNotificationPoint(10000);
        wikipediaExtractor.start(extractorParameters);

        logger.info("extraction ended " + new Date());

    }
}
