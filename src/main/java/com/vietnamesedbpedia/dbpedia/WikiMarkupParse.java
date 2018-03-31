package com.vietnamesedbpedia.dbpedia;

/**
 * Created by vietanknb on 30/5/2017.
 */
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.FlushTemplates;
import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.MediaWikiParser;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.regex.Pattern;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.fbk.cit.hlt.core.io.FileUtils;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.clean.CleanWikipedia;

public class WikiMarkupParse {

    public static final String NBSP = "&nbsp;";
    public static final String EMPTY_STRING = "";
    protected Pattern refPattern = Pattern.compile("<ref[^>]*>[^<]+</ref>");
    protected Pattern commentPattern = Pattern.compile("<!--[^-]+-->");
    protected MediaWikiParser parser;
    private static WikiMarkupParse ourInstance = null;

    public static synchronized WikiMarkupParse getInstance() {
        if(ourInstance == null) {
            ourInstance = new WikiMarkupParse();
        }

        return ourInstance;
    }

    private WikiMarkupParse() {
        MediaWikiParseFactory pf = new MediaWikiParseFactory();
        pf.setTemplateParserClass((new FlushTemplates()).getClass());

        this.parser = pf.createParser();
    }

    protected String normalizePageName(String s) {
        return s.length() == 0?s:(Character.isUpperCase(s.charAt(0))?s:s.substring(0, 1).toUpperCase() + s.substring(1, s.length()));
    }

    protected String removeRef(String text) {
        return this.refPattern.matcher(text).replaceAll("");
    }

    protected String removeHtmlComments(String text) {
        return this.commentPattern.matcher(text).replaceAll("");
    }

    protected String replaceNBSP(String text) {
        return text.replace("&nbsp;", " ");
    }

    public ParsedPage parsePage(String text, String[] prefixes) throws IOException {
        return this.parser.parse(CleanWikipedia.clean(this.replaceNBSP(this.removeRef(text)), prefixes, true, true));
    }

    public ParsedPage parsePage(String text) throws IOException {
        return this.parser.parse(this.replaceNBSP(this.removeRef(text)));
    }

    public static void main(String[] args) throws IOException {
        String logConfig = System.getProperty("log-config");
        if(logConfig == null) {
            logConfig = "configuration/log-config.txt";
        }

        PropertyConfigurator.configure(logConfig);
        Options options = new Options();

        try {
            OptionBuilder.withArgName("file");
            OptionBuilder.hasArg();
            OptionBuilder.withDescription("wikipedia file");
            OptionBuilder.isRequired();
            OptionBuilder.withLongOpt("input");
            Option inputOpt = OptionBuilder.create("i");
            options.addOption(inputOpt);
            CommandLineParser parser = new PosixParser();
            CommandLine line = parser.parse(options, args);
            String wikiText = FileUtils.read(new File(line.getOptionValue("input")));
            WikiMarkupParse wikiMarkupParser = getInstance();
            ParsedPage parsedPage = wikiMarkupParser.parsePage(wikiText);
            Iterator var9 = parsedPage.getSections().iterator();

            while(var9.hasNext()) {
                Section section = (Section)var9.next();
                System.out.println(section.getText());
            }
        } catch (ParseException var11) {
            System.out.println("Parsing failed: " + var11.getMessage() + "\n");
            HelpFormatter formatter = new HelpFormatter();
            formatter.printHelp(400, "java -cp dist/thewikimachine.jar org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiMarkupParser", "\n", options, "\n", true);
        }

    }
}

