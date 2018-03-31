package com.vietnamesedbpedia.dbpedia;

import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.xmldump.AbstractWikipediaExtractor;
import org.fbk.cit.hlt.thewikimachine.xmldump.AbstractWikipediaXmlDumpParser;
import org.fbk.cit.hlt.thewikimachine.xmldump.WikipediaExtractor;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vieta on 29/11/2016.
 */
public abstract class VNAbstractWikipediaExtractor extends AbstractWikipediaXmlDumpParser implements WikipediaExtractor {
    /**
     * Define a static logger variable so that it references the
     * Logger instance named <code>AbstractWikipediaExtractor</code>.
     */
    static Logger logger = Logger.getLogger(AbstractWikipediaExtractor.class.getName());

    protected PropertiesConfiguration resources;
    protected String categoryPrefix;
    protected String filePrefix;
    protected String specialPrefix;
    protected String imagePrefix;
    protected String wikipediaPrefix;
    protected String portalPrefix;
    protected String templatePrefix;
    protected String mediawikiPrefix;
    protected String disambiguationSuffix;
    protected String helpPrefix;
    protected String projectPrefix;
    protected String infoboxRootCategory;
    protected Pattern disambiguationPattern;
    protected Pattern categoryPattern;
    protected Pattern categoryMainPattern;
    protected Pattern officialSitePattern;
    protected Pattern filePattern;
    protected Pattern simpleCategoryPattern;
    protected Pattern templatePattern;
    protected Pattern simpleTemplatePattern;
    protected Pattern navigationTemplatePattern;


    protected AtomicInteger disambiguationPageCounter = new AtomicInteger();
    protected AtomicInteger redirectPageCounter = new AtomicInteger();
    protected AtomicInteger categoryPageCounter = new AtomicInteger();
    protected AtomicInteger specialPageCounter = new AtomicInteger();
    protected AtomicInteger filePageCounter = new AtomicInteger();
    protected AtomicInteger otherPageCounter = new AtomicInteger();
    protected AtomicInteger imagePageCounter = new AtomicInteger();
    protected AtomicInteger templatePageCounter = new AtomicInteger();
    protected AtomicInteger mediawikiPageCounter = new AtomicInteger();
    protected AtomicInteger wikipediaPageCounter = new AtomicInteger();
    protected AtomicInteger portalPageCounter = new AtomicInteger();
    protected AtomicInteger helpPageCounter = new AtomicInteger();
    protected AtomicInteger projectPageCounter = new AtomicInteger();
    protected AtomicInteger countPageCounter = new AtomicInteger();

    private int numPages;
    private String configurationFolder = "configuration/";

    private Locale locale;

    public static final int DEFAULT_NUM_PAGES = Integer.MAX_VALUE;

    private int notificationPoint;

    public final static int DEFAULT_NOTIFICATION_POINT = 10000;

    boolean compress;

    boolean printHeader;

    public VNAbstractWikipediaExtractor(int numThreads, int numPages, Locale locale) {
        this(numThreads, numPages, locale, null);
    }

    public VNAbstractWikipediaExtractor(int numThreads, int numPages, Locale locale, String configurationFolder) {
        super(numThreads);
        this.numPages = numPages;
        this.locale = locale;
        notificationPoint = DEFAULT_NOTIFICATION_POINT;
        if (configurationFolder != null) {
            if (!configurationFolder.endsWith(File.separator)) {
                configurationFolder += File.separator;
            }
            this.configurationFolder = configurationFolder;
        }
        loadResources();
        printHeader = true;
        compress = DEFAULT_COMPRESS_OUTPUT;
        //logger.debug(numPages + " pages");
    }

    public boolean isCompress() {
        return compress;
    }

    public void setCompress(boolean compress) {
        this.compress = compress;
    }

    public void setNotificationPoint(int notificationPoint) {
        this.notificationPoint = notificationPoint;
    }

    public int getNotificationPoint() {
        return notificationPoint;
    }

    public int getNumPages() {
        return numPages;
    }

    public Locale getLocale() {
        return locale;
    }

    public void loadResources() {
        logger.info("loading " + locale.toLanguageTag() + " resources...");
        try {
            // resources = ResourceBundle.getBundle("thewikimachineresources", locale);
            resources = new PropertiesConfiguration();
            resources.setEncoding("UTF-8");
            resources.setListDelimiter('\t');

            String configurationFileName = configurationFolder + locale.toLanguageTag() + ".properties";
            logger.debug("Configuration file: " + configurationFileName);
            File configurationFile = new File(configurationFileName);
            if (!configurationFile.exists()) {
                throw new IOException("File " + configurationFile.getAbsolutePath() + " does not exist");
            }
            resources.load(new FileReader(configurationFile));
            logger.debug(resources);
            if (resources.getString("NAVBOX_TEMPLATE") != null) {
                try {
                    navigationTemplatePattern = Pattern.compile(resources.getString("NAVBOX_TEMPLATE"));
                    logger.debug("NAVBOX_TEMPLATE " + resources.getString("NAVBOX_TEMPLATE"));
                } catch (Exception e) {
                    logger.warn(e.getMessage());
                }
            }
            if (resources.getString("INFOBOX_LABEL") != null) {
                infoboxRootCategory = resources.getString("INFOBOX_LABEL");
                logger.debug("INFOBOX_LABEL " + infoboxRootCategory);
            }

            if (resources.getString("CATEGORY_LABEL") != null) {
                categoryPrefix = resources.getString("CATEGORY_LABEL") + ":";
                logger.debug("CATEGORY_LABEL " + categoryPrefix);
            }

            if (resources.getString("FILE_LABEL") != null) {
                //filePattern = Pattern.compile("\\[\\[" + resources.getString("FILE_LABEL") + ":([^\\]\\|]+)[\\|\\]]");
                filePattern = Pattern.compile("[:=]\\s?([^\\.\\|\\[\\]:=]+)\\.(svg|jpg|png|gif|jpeg)", Pattern.CASE_INSENSITIVE);
                filePrefix = resources.getString("FILE_LABEL") + ":";
                logger.debug("FILE_LABEL " + filePrefix);
            }

            if (resources.getString("SPECIAL_LABEL") != null) {
                specialPrefix = resources.getString("SPECIAL_LABEL") + ":";
                logger.debug("SPECIAL_LABEL " + specialPrefix);
            }

            if (resources.getString("IMAGE_LABEL") != null) {
                imagePrefix = resources.getString("IMAGE_LABEL") + ":";
                logger.debug("IMAGE_LABEL " + imagePrefix);
            }

            if (resources.getString("WIKIPEDIA_LABEL") != null) {
                wikipediaPrefix = resources.getString("WIKIPEDIA_LABEL") + ":";
                logger.debug("WIKIPEDIA_LABEL " + wikipediaPrefix);
            }

            if (resources.getString("PORTAL_LABEL") != null) {
                portalPrefix = resources.getString("PORTAL_LABEL") + ":";
                logger.debug("PORTAL_LABEL " + portalPrefix);
            }

            if (resources.getString("TEMPLATE_LABEL") != null) { //Template
                templatePrefix = resources.getString("TEMPLATE_LABEL") + ":";
                logger.debug("TEMPLATE_LABEL " + templatePrefix);
            }

            if (resources.getString("MEDIAWIKI_LABEL") != null) {
                mediawikiPrefix = resources.getString("MEDIAWIKI_LABEL") + ":";
                logger.debug("MEDIAWIKI_LABEL " + mediawikiPrefix);
            }

            if (resources.getString("HELP_LABEL") != null) {
                helpPrefix = resources.getString("HELP_LABEL") + ":";
                logger.debug("HELP_LABEL " + helpPrefix);
            }

            if (resources.getString("PROJECT_LABEL") != null) {
                projectPrefix = resources.getString("PROJECT_LABEL") + ":";
                logger.debug("PROJECT_LABEL " + projectPrefix);
            }

            if (resources.getString("DISAMBIGUATION_LABEL") != null) {
                disambiguationSuffix = "(" + resources.getString("DISAMBIGUATION_LABEL") + ")";
                logger.debug("DISAMBIGUATION_LABEL " + disambiguationSuffix);
            }

            if (resources.getString("DISAMBIGUATION_PATTERN") != null) {
                disambiguationPattern = Pattern.compile(resources.getString("DISAMBIGUATION_PATTERN"), Pattern.CASE_INSENSITIVE);
                logger.debug("DISAMBIGUATION_PATTERN " + disambiguationPattern);
            }

            if (resources.getString("CATEGORY_LABEL") != null) {
                categoryPattern = Pattern.compile("\\[\\[(" + resources.getString("CATEGORY_LABEL") + ":([^\\]]+))\\]\\]", Pattern.CASE_INSENSITIVE);
                categoryMainPattern = Pattern.compile("\\[\\[(" + resources.getString("CATEGORY_LABEL") + ":([^\\]\\|]+))\\|\\s*\\]\\]", Pattern.CASE_INSENSITIVE);
                simpleCategoryPattern = Pattern.compile(resources.getString("CATEGORY_LABEL") + ":([^\\]]+)", Pattern.CASE_INSENSITIVE);
                logger.debug("CATEGORY_PATTERN " + categoryPattern);
                logger.debug("SIMPLE_CATEGORY_PATTERN " + simpleCategoryPattern);
            }

			/*if (resources.getString("OFFICIAL_SITE_PATTERN") != null) {
				officialSitePattern = Pattern.compile("\\[\\[(" + resources.getString("OFFICIAL_SITE_PATTERN") + ":([^\\]]+))\\]\\]");
				logger.debug("OFFICIAL_SITE_PATTERN " + categoryPattern);
			}*/

            if (resources.getString("TEMPLATE_LABEL") != null) {
                templatePattern = Pattern.compile("\\[\\[(" + resources.getString("TEMPLATE_LABEL") + ":([^\\]]+))\\]\\]");
                // simpleTemplatePattern = Pattern.compile(resources.getString("TEMPLATE_LABEL") + ":([^\\]]+)");
                simpleTemplatePattern = Pattern.compile(resources.getString("TEMPLATE_LABEL") + ":([^\\]/]+)");
                logger.debug("TEMPLATE_PATTERN " + templatePattern);
                logger.debug("SIMPLE_TEMPLATE_PATTERN " + simpleTemplatePattern);
            }

        } catch (IOException e) {
            logger.error(e);
            System.exit(0);
        } catch (Exception e) {
            logger.error(e);
        }
    }

    public void printLog() {
        if (printHeader) {
            logger.info("pages\tcontent\tredirect\tdisambig\tcategory\tfile\ttime\tdate");
            printHeader = false;
        }
        //String s = String.format("Pages: %10s, Content: %10s, Redirect: %10s, Disambiguation: %10s, Category: %10s, %7s ms, %s", decimalFormat.format(generalCount.intValue()), decimalFormat.format(countPageCounter), decimalFormat.format(redirectPageCounter), decimalFormat.format(disambiguationPageCounter), decimalFormat.format(categoryPageCounter), decimalFormat.format(genEnd.longValue() - genBegin.longValue()), new Date());
        //String s = String.format("%9s %9s %9s %9s %9s %7s %s", decimalFormat.format(generalCount.intValue()), decimalFormat.format(countPageCounter), decimalFormat.format(redirectPageCounter), decimalFormat.format(disambiguationPageCounter), decimalFormat.format(categoryPageCounter), decimalFormat.format(genEnd.longValue() - genBegin.longValue()), new Date());
        //logger.info(s);
        try {
            logger.info(
                    decimalFormat.format(generalCount.intValue()) + "\t" +
                            decimalFormat.format(countPageCounter) + "\t" +
                            decimalFormat.format(redirectPageCounter) + "\t" +
                            decimalFormat.format(disambiguationPageCounter) + "\t" +
                            decimalFormat.format(categoryPageCounter) + "\t" +
                            decimalFormat.format(filePageCounter) + "\t" +
                            decimalFormat.format(genEnd.longValue() - genBegin.longValue()) + "\t" +
                            new Date()
            );
        } catch (Exception e) {
            logger.warn(e.getMessage());
        }
    }

    public void endProcess() {
        genEnd.set(System.currentTimeMillis());
        printLog();
        //super.endProcess();
    }

    public void getPage(String text, String title, int wikiID, String redirectToPage) {

        int i = generalCount.incrementAndGet();

		if (i > numPages) {
			logger.info("Exit after " + i + " content pages (" + numPages + ")");
			endProcess();
			System.exit(0);
		}

        if ((i % getNotificationPoint()) == 0) {
            genEnd.set(System.currentTimeMillis());
            printLog();
            genBegin.set(System.currentTimeMillis());
        }

        if (redirectToPage != null) {
            //logger.debug(Thread.currentThread().getName());
            redirectPage(redirectToPage, title, wikiID);
            redirectPageCounter.incrementAndGet();
            return;
        }

        //System.out.println("Prefix: "+ title+ " " +title.startsWith(templatePrefix.replace(" ","_")));

        if (title.endsWith(disambiguationSuffix)) {
            //logger.debug(Thread.currentThread().getName());
            disambiguationPage(text, title, wikiID);
            disambiguationPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(categoryPrefix)) {
            //logger.debug(Thread.currentThread().getName());
            //logger.debug(title);
            categoryPage(text, title, wikiID);
            categoryPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(filePrefix)) {
            //logger.debug(title);
            filePage(text, title, wikiID);
            filePageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(specialPrefix)) {
            specialPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(imagePrefix)) {
            imagePageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(wikipediaPrefix)) {
            wikipediaPageCounter.incrementAndGet();
            otherPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(portalPrefix)) {
            portalPage(text, title, wikiID);
            portalPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(templatePrefix)) {
            templatePage(text, title, wikiID);
            templatePageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(mediawikiPrefix)) {
            mediawikiPageCounter.incrementAndGet();
            otherPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(helpPrefix)) {
            helpPageCounter.incrementAndGet();
            otherPageCounter.incrementAndGet();
            return;
        }

        if (title.startsWith(projectPrefix)) {
            projectPage(text, title, wikiID);
            projectPageCounter.incrementAndGet();
            otherPageCounter.incrementAndGet();
            return;
        }

        if (text.length() > 0) {
            text = StringEscapeUtils.unescapeXml(text);
//			try {
//				FileOutputStream fileOutputStream = new FileOutputStream(new File("getText.txt"));
//				try {
//					//fileOutputStream.write(title.getBytes());
//					fileOutputStream.write(text.getBytes());
//					fileOutputStream.write("\n".getBytes());
//					fileOutputStream.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			} catch (FileNotFoundException e) {
//				e.printStackTrace();
//			}
//			System.out.println(text);
            if (disambiguationPattern != null) {
                Matcher disambiguationMatcher = disambiguationPattern.matcher(text);
                if (disambiguationMatcher.find()) {
                    disambiguationPage(text, title, wikiID);
                    disambiguationPageCounter.incrementAndGet();
                }
                else {
                    contentPage(text, title, wikiID);
                    countPageCounter.incrementAndGet();
                }
            }
            else {
                contentPage(text, title, wikiID);
                countPageCounter.incrementAndGet();
            }
        }
    }

    /**
     * Returns the string capitalized if it is not.
     */
    public static String normalizePageName(String s) {
        if (s.length() == 0) {
            return s;
        }

        if (Character.isUpperCase(s.charAt(0))) {
            return s;
        }

        return s.substring(0, 1).toUpperCase() + s.substring(1, s.length());
    }
}
