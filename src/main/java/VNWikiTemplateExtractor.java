import java.io.*;
import java.util.*;

import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.util.FreqSet;
import org.fbk.cit.hlt.thewikimachine.ExtractorParameters;
import org.fbk.cit.hlt.thewikimachine.xmldump.AbstractWikipediaExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.WikipediaExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.WikipediaTemplateExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplateParser;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplate;

/**
 * Created by vieta on 19/2/2017.
 */
public class VNWikiTemplateExtractor extends AbstractWikipediaExtractor implements WikipediaExtractor {
    /**
     * Define a static logger variable so that it references the
     * Logger instance named <code>WikipediaTemplateExtractor</code>.
     */
    static Logger logger = Logger.getLogger(WikipediaTemplateExtractor.class.getName());

    private PrintWriter templateNameWriter;

    private PrintWriter templateFreqWriter;

    private PrintWriter templateMapWriter;

    private PrintWriter templateMapWriterWithRepetitions;

    private PrintWriter templateMapWriterProp;

    private FreqSet templateFreqSet;

    public VNWikiTemplateExtractor(int numThreads, int numPages, Locale locale) {
        super(numThreads, numPages, locale);
        templateFreqSet = new FreqSet();

    }

    @Override
    public void start(ExtractorParameters extractorParameters) {
        // String prefix = extractorParameters.getWikipediaTemplateFilePrefixName();
        try {

            templateNameWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaTemplateFileNames().get("name")), "UTF-8")));
            templateFreqWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaTemplateFileNames().get("freq")), "UTF-8")));
            templateMapWriter = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaTemplateFileNames().get("map")), "UTF-8")));
            templateMapWriterWithRepetitions = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaTemplateFileNames().get("map-rep")), "UTF-8")));
            templateMapWriterProp = new PrintWriter(new BufferedWriter(new OutputStreamWriter(new FileOutputStream(extractorParameters.getWikipediaTemplateFileNames().get("map-prop")), "UTF-8")));

        } catch (IOException e) {
            logger.error(e);
        }
        startProcess(extractorParameters.getWikipediaXmlFileName());
    }

    @Override
    public void filePage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void disambiguationPage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void categoryPage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void redirectPage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void portalPage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    @Override
    public void projectPage(String text, String title, int wikiID) {
        //To change body of implemented methods use File | Settings | File Templates.
    }


    @Override
    public void templatePage(String text, String title, int wikiID) {
        synchronized (this) {
            templateNameWriter.println(title);
        }
    }

    @Override
    public void contentPage(String text, String title, int wikiID) {
        ArrayList<WikiTemplate> listOfTemplates = WikiTemplateParser.parse(text, false);

        Set<String> set = new HashSet<String>();
        Set<String> keySet = new HashSet<String>();

        StringBuffer toBeWrittenMap = new StringBuffer();
        StringBuffer toBeWrittenMapRep = new StringBuffer();
        StringBuffer toBeWrittenMapProp = new StringBuffer();

        int i = 0;
        for (WikiTemplate t : listOfTemplates) {
            HashMap<String, String> parts = t.getHashMapOfParts();
            Set keys = parts.keySet();
            String name = t.getFirstPart();
            if (name == null || name.length() == 0) {
                continue;
            }
            if (name.startsWith("#")) {
                continue;
            }
            name = normalizePageName(name.trim()).replace(' ', '_');
            String toBeWritten;

            if (!set.contains(name)) {
                toBeWritten = title + "\t" + name + "\t" + i + "\t" + wikiID;
                toBeWrittenMap.append(toBeWritten).append("\n");
                synchronized (this) {
                    templateFreqSet.add(name);
                }
                set.add(name);
                i++;
            }
            toBeWritten = title + "\t" + name + "\t" + t.getPartsCount() + "\t" + t.getNlCount() + "\t" + t.getKeyValueParts();
            toBeWrittenMapRep.append(toBeWritten).append("\n");

            for (Object key : keys) {
                String keyName = (String) key;
                String keyNameToSave = name + ";" + keyName;
                if (!keySet.contains(keyNameToSave)) {
                    toBeWritten = title + "\t" + name + "\t" + keyName;
                    toBeWrittenMapProp.append(toBeWritten).append("\n");
                    keySet.add(keyNameToSave);
                }
            }
        }

        synchronized (this) {
            templateMapWriter.print(toBeWrittenMap);
            templateMapWriterWithRepetitions.print(toBeWrittenMapRep);
            templateMapWriterProp.print(toBeWrittenMapProp);
        }
    }

    @Override
    public void endProcess() {
        super.endProcess();
        SortedMap<Integer, List<String>> smap = templateFreqSet.toSortedMap();
        int count = 0;
        for (Integer freq : smap.keySet()) {
            List<String> list = smap.get(freq);
            for (String aList : list) {
                templateFreqWriter.println(freq + "\t" + aList);
                count += freq;
            }
        }
        logger.info(count + " pages with at least one template");

        templateFreqWriter.println(count + " pages with at least one template");
        templateNameWriter.flush();
        templateNameWriter.close();
        templateFreqWriter.flush();
        templateFreqWriter.close();
        templateMapWriter.flush();
        templateMapWriter.close();
        templateMapWriterWithRepetitions.flush();
        templateMapWriterWithRepetitions.close();
        templateMapWriterProp.flush();
        templateMapWriterProp.close();

    }

	public static void main(String argv[]) throws IOException {
		int threads = 1000;

		int size = 1000;
        ExtractorParameters extractorParameters = new ExtractorParameters("viwiki-2016-pages-articles.xml", "19_02");
		WikipediaTemplateExtractor writer = new WikipediaTemplateExtractor(threads, size, extractorParameters.getLocale());
		writer.start(extractorParameters);
	}

}
