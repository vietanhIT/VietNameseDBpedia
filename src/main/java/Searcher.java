import org.apache.commons.cli.*;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermDocs;
import org.fbk.cit.hlt.thewikimachine.index.PageTextIndexer;
import org.fbk.cit.hlt.thewikimachine.index.PageTextSearcher;
import org.fbk.cit.hlt.thewikimachine.index.util.AbstractSearcher;
import org.fbk.cit.hlt.thewikimachine.util.CharacterTable;
import org.fbk.cit.hlt.thewikimachine.util.StringTable;
import org.fbk.cit.hlt.thewikimachine.xmldump.AbstractWikipediaExtractor;

import java.io.*;
import java.text.DecimalFormat;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

/**
 * Created by vieta on 17/11/2016.
 */
public class Searcher extends AbstractSearcher {
    static Logger logger = Logger.getLogger(Search.class.getName());

    public static final int DEFAULT_MIN_FREQ = 1000;

    public static final int DEFAULT_MAXIMUM_TEXT_LENGTH = Integer.MAX_VALUE;

    public static final boolean DEFAULT_THREAD_SAFE = true;

    protected static DecimalFormat df = new DecimalFormat("###,###,###,###");

    private static DecimalFormat tf = new DecimalFormat("000,000,000.#");

    private static DecimalFormat ff = new DecimalFormat("###,###,##0.000");

    private static Pattern tabPattern = Pattern.compile(StringTable.HORIZONTAL_TABULATION);

    protected boolean threadSafe;

    private int textLength;

    private Map<String, String> cache;

    private Term keyTerm;

    public Searcher(String indexName) throws IOException {
        this(indexName, DEFAULT_THREAD_SAFE, DEFAULT_MAXIMUM_TEXT_LENGTH);
    }

    public Searcher(String indexName, boolean threadSafe, int textLength) throws IOException {
        super(indexName);
        this.threadSafe = threadSafe;
        this.textLength = textLength;
        keyTerm = new Term("value", "");
        logger.debug(keyTerm);
        logger.trace(toString(10));
    }

    public int getTextLength() {
        return textLength;
    }

    public void setMaximunTextLength(int textLength) {
        this.textLength = textLength;
    }

    public void loadCache(String name) throws IOException {
        loadCache(new File(name));
    }

    public void loadCache(String name, int minFreq) throws IOException {
        loadCache(new File(name), minFreq);
    }

    public void loadCache(File f) throws IOException {
        loadCache(f, DEFAULT_MIN_FREQ);
    }

    public void loadCache(File f, int minFreq) throws IOException {
        logger.info("loading cache from " + f + " (freq>" + minFreq + ")...");
        long begin = System.nanoTime();

        if (threadSafe) {
            logger.info(this.getClass().getName() + "'s cache is thread safe");
            cache = Collections.synchronizedMap(new HashMap<String, String>());
        } else {
            logger.warn(this.getClass().getName() + "'s cache isn't thread safe");
            cache = new HashMap<String, String>();
        }

        LineNumberReader lnr = new LineNumberReader(new InputStreamReader(new FileInputStream(f), "UTF-8"));
        String line;
        int i = 0;
        String[] t;
        int freq = 0;
        String result;
        Document doc;
        TermDocs termDocs;
        while ((line = lnr.readLine()) != null) {
            t = tabPattern.split(line);
            if (t.length == 2) {
                freq = Integer.parseInt(t[0]);
                if (freq < minFreq) {
                    break;
                }
                termDocs = indexReader.termDocs(keyTerm.createTerm(t[1]));
                if (termDocs.next()) {
                    doc = indexReader.document(termDocs.doc());
                    result = fromByte(doc.getBinaryValue("value"));
                    cache.put(t[1], result);
                }
            }
            if ((i % notificationPoint) == 0) {
                System.out.print(CharacterTable.FULL_STOP);
            }
            i++;
        }
        System.out.print(CharacterTable.LINE_FEED);
        lnr.close();
        long end = System.nanoTime();
        logger.info(df.format(cache.size()) + " (" + df.format(indexReader.numDocs()) + ") keys cached in " + tf.format(end - begin) + " ns");
    }

    public String search(String page) {
        //logger.debug("searching " + q + "...");
        //long begin = 0, end = 0;
        //begin = System.nanoTime();
        String result = null;
        if (cache != null) {
            result = cache.get(page);
        }

        if (result != null) {
            //end = System.nanoTime();
            //logger.debug("found in cache in " + tf.format(end - begin) + " ns");
            return result;
        }

        //end = System.nanoTime();
        //logger.debug("not found in cache in " + tf.format(end - begin) + " ns");

        try {
            //begin = System.nanoTime();
            TermDocs termDocs = indexReader.termDocs(keyTerm.createTerm(page));
            //end = System.nanoTime();
            //begin = System.nanoTime();
            if (termDocs.next()) {
                Document doc = indexReader.document(termDocs.doc());
                result = fromByte(doc.getBinaryValue("value"));
                return result;
                //end = System.nanoTime();
                //logger.debug("retrieved in " + tf.format(end - begin) + " ns");
            }
        } catch (IOException e) {
            logger.error(e);
        }

        return new String();
    }

    public String fromByte(byte[] byteArray) throws IOException {
        ByteArrayInputStream byteStream = new ByteArrayInputStream(byteArray);
        DataInputStream dataStream = new DataInputStream(byteStream);

        int length = dataStream.readInt();
        StringBuilder sb = new StringBuilder();
        sb.append(dataStream.readUTF());
        for (int j = 1; j < length && j < textLength; j++) {
            sb.append(CharacterTable.SPACE);
            sb.append(dataStream.readUTF());
        }

        return sb.toString();
    }

    public void interactive() throws Exception {
        InputStreamReader indexReader = null;
        BufferedReader myInput = null;
        long begin = 0, end = 0;
        while (true) {
            System.out.println("\nPlease write a query and type <return> to continue (CTRL C to exit):");

            indexReader = new InputStreamReader(System.in);
            myInput = new BufferedReader(indexReader);
            //String query = myInput.readLine().toString().replace(' ', '_');
            String query = myInput.readLine().toString();

            begin = System.nanoTime();
            String text = search(query);
            end = System.nanoTime();
            logger.info(text + "...");
            logger.info(query + "\t" + text.length() + "\t" + tf.format(end - begin) + " ns");

			/*begin = System.nanoTime();
            Object o = cache.get(query);
			end = System.nanoTime();
			logger.info(query + "\t" + o  + "\t" + tf.format(end - begin) + " ns");*/

        } // end while
    }

    public static void main(String args[]) throws Exception {
        int minFreq = DEFAULT_MIN_FREQ;
        int maximumTextLength = DEFAULT_MAXIMUM_TEXT_LENGTH;
        int notificationPoint = AbstractWikipediaExtractor.DEFAULT_NOTIFICATION_POINT;
        Searcher searcher = new Searcher("index");
        searcher.setMaximunTextLength(maximumTextLength);
        searcher.setNotificationPoint(notificationPoint);
        //searcher.loadCache("index\\_9c.cfs");
        String result = searcher.search("Wales");
        logger.debug(result);
    }
}
