package com.vietnamesedbpedia.dbpedia;

/**
 * Created by vietanknb on 30/5/2017.
 */
//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by Fernflower decompiler)
//

import de.tudarmstadt.ukp.wikipedia.parser.mediawiki.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class MediaWikiParseFactory {

    public enum Language {
        abkhazian,
        afar,
        afrikaans,
        akan,
        albanian,
        alemannic,
        amharic,
        anglo_saxon,
        arabic,
        aragonese,
        armenian,
        aromanian,
        assamese,
        assyrian_neo_aramaic,
        asturian,
        avar,
        aymara,
        azeri,
        bambara,
        banyumasan,
        bashkir,
        basque,
        bavarian,
        belarusian,
        belarusian_tarashkevitsa,
        bengali,
        bihari,
        bishnupriya_manipuri,
        bislama,
        bosnian,
        breton,
        buginese,
        bulgarian,
        burmese,
        buryat_russia,
        cantonese,
        catalan,
        cebuano,
        central_bicolano,
        chamorro,
        chechen,
        cherokee,
        cheyenne,
        chichewa,
        chinese,
        choctaw,
        chuvash,
        classical_chinese,
        cornish,
        corsican,
        cree,
        crimean_tatar,
        croatian,
        czech,
        danish,
        divehi,
        dutch,
        dutch_low_saxon,
        dzongkha,
        emilian_romagnol,
        english,
        esperanto,
        estonian,
        ewe,
        faroese,
        fijian,
        finnish,
        franco_provencal_arpitan,
        french,
        friulian,
        fula,
        galician,
        georgian,
        german,
        gilaki,
        gothic,
        greek,
        greenlandic,
        guarani,
        gujarati,
        haitian,
        hakka,
        hausa,
        hawaiian,
        hebrew,
        herero,
        hindi,
        hiri_motu,
        hungarian,
        icelandic,
        ido,
        igbo,
        ilokano,
        indonesian,
        interlingua,
        interlingue,
        inuktitut,
        inupiak,
        irish,
        italian,
        japanese,
        javanese,
        kabyle,
        kalmyk,
        kannada,
        kanuri,
        kapampangan,
        kashmiri,
        kashubian,
        kazakh,
        khmer,
        kikuyu,
        kinyarwanda,
        kirghiz,
        kirundi,
        klingon,
        komi,
        kongo,
        korean,
        kuanyama,
        kurdish,
        ladino,
        lak,
        lao,
        latin,
        latvian,
        ligurian,
        limburgian,
        lingala,
        lithuanian,
        lojban,
        lombard,
        low_saxon,
        lower_sorbian,
        luganda,
        luxembourgish,
        macedonian,
        malagasy,
        malay,
        malayalam,
        maltese,
        manx,
        maori,
        marathi,
        marshallese,
        mazandarani,
        min_dong,
        min_nan,
        moldovan,
        mongolian,
        muscogee,
        nahuatl,
        nauruan,
        navajo,
        ndonga,
        neapolitan,
        nepali,
        newar_nepal_bhasa,
        norfolk,
        norman,
        northern_sami,
        norwegian_bokmal,
        norwegian_nynorsk,
        novial,
        occitan,
        old_church_slavonic,
        oriya,
        oromo,
        ossetian,
        pali,
        pangasinan,
        papiamentu,
        pashto,
        pennsylvania_german,
        persian,
        piedmontese,
        polish,
        portuguese,
        punjabi,
        quechua,
        ripuarian,
        romani,
        romanian,
        romansh,
        russian,
        samoan,
        samogitian,
        sango,
        sanskrit,
        sardinian,
        saterland_frisian,
        scots,
        scottish_gaelic,
        serbian,
        serbo_croatian,
        sesotho,
        shona,
        sichuan_yi,
        sicilian,
        simple_english,
        sindhi,
        sinhalese,
        slovak,
        slovenian,
        somali,
        spanish,
        sundanese,
        swahili,
        swati,
        swedish,
        tagalog,
        tahitian,
        tajik,
        tamil,
        tarantino,
        tatar,
        telugu,
        tetum,
        thai,
        tibetan,
        tigrinya,
        tok_pisin,
        tokipona,
        tongan,
        tsonga,
        tswana,
        tumbuka,
        turkish,
        turkmen,
        twi,
        udmurt,
        ukrainian,
        upper_sorbian,
        urdu,
        uyghur,
        uzbek,
        venda,
        venetian,
        vietnamese,
        volapuek,
        voro,
        walloon,
        waray_waray,
        welsh,
        west_flemish,
        west_frisian,
        wolof,
        wu,
        xhosa,
        yiddish,
        yoruba,
        zamboanga_chavacano,
        zazaki,
        zealandic,
        zhuang,
        zulu,
        _test
    }

//    private final Log logger = LogFactory.getLog(this.getClass());
    private Class parserClass;
    private Class templateParserClass;
    private String lineSeparator;
    private List<String> deleteTemplates;
    private List<String> parseTemplates;
    private List<String> categoryIdentifers;
    private List<String> languageIdentifers;
    private List<String> imageIdentifers;
    private boolean showImageText;
    private boolean deleteTags;
    private boolean showMathTagContent;
    private boolean calculateSrcSpans;

    public MediaWikiParseFactory() {
        this.initVariables();
    }

    public MediaWikiParseFactory(Language language) {
        this.initVariables();
        if(language.equals(Language.german)) {
            this.initGermanVariables();
        } else if(language.equals(Language.english)) {
            this.initEnglishVariables();
        } else {
//            this.logger.warn("No language specific parser for " + language.toString() + " available. Using default values.");
        }

    }

    private void initVariables() {
        this.lineSeparator = "LF";
        this.parserClass = ModularParser.class;
        this.imageIdentifers = new ArrayList();
        this.categoryIdentifers = new ArrayList();
        this.languageIdentifers = new ArrayList();
        this.deleteTemplates = new ArrayList();
        this.parseTemplates = new ArrayList();
        this.showImageText = false;
        this.deleteTags = true;
        this.showMathTagContent = true;
        this.calculateSrcSpans = false;
        this.templateParserClass = ShowTemplateNamesAndParameters.class;
        this.initLanguages();
    }

    private void initLanguages() {
        this.languageIdentifers.add("aa");
        this.languageIdentifers.add("ab");
        this.languageIdentifers.add("af");
        this.languageIdentifers.add("am");
        this.languageIdentifers.add("an");
        this.languageIdentifers.add("ar");
        this.languageIdentifers.add("as");
        this.languageIdentifers.add("av");
        this.languageIdentifers.add("ay");
        this.languageIdentifers.add("az");
        this.languageIdentifers.add("ba");
        this.languageIdentifers.add("be");
        this.languageIdentifers.add("bg");
        this.languageIdentifers.add("bh");
        this.languageIdentifers.add("bi");
        this.languageIdentifers.add("bm");
        this.languageIdentifers.add("bn");
        this.languageIdentifers.add("bo");
        this.languageIdentifers.add("br");
        this.languageIdentifers.add("bs");
        this.languageIdentifers.add("ca");
        this.languageIdentifers.add("ce");
        this.languageIdentifers.add("ch");
        this.languageIdentifers.add("co");
        this.languageIdentifers.add("cr");
        this.languageIdentifers.add("cs");
        this.languageIdentifers.add("cv");
        this.languageIdentifers.add("cy");
        this.languageIdentifers.add("da");
        this.languageIdentifers.add("de");
        this.languageIdentifers.add("dk");
        this.languageIdentifers.add("dv");
        this.languageIdentifers.add("dz");
        this.languageIdentifers.add("ee");
        this.languageIdentifers.add("el");
        this.languageIdentifers.add("en");
        this.languageIdentifers.add("eo");
        this.languageIdentifers.add("es");
        this.languageIdentifers.add("et");
        this.languageIdentifers.add("eu");
        this.languageIdentifers.add("fa");
        this.languageIdentifers.add("ff");
        this.languageIdentifers.add("fi");
        this.languageIdentifers.add("fj");
        this.languageIdentifers.add("fo");
        this.languageIdentifers.add("fr");
        this.languageIdentifers.add("fy");
        this.languageIdentifers.add("ga");
        this.languageIdentifers.add("gd");
        this.languageIdentifers.add("gl");
        this.languageIdentifers.add("gn");
        this.languageIdentifers.add("gu");
        this.languageIdentifers.add("gv");
        this.languageIdentifers.add("ha");
        this.languageIdentifers.add("he");
        this.languageIdentifers.add("hi");
        this.languageIdentifers.add("hr");
        this.languageIdentifers.add("ht");
        this.languageIdentifers.add("hu");
        this.languageIdentifers.add("hy");
        this.languageIdentifers.add("ia");
        this.languageIdentifers.add("id");
        this.languageIdentifers.add("ie");
        this.languageIdentifers.add("ig");
        this.languageIdentifers.add("ii");
        this.languageIdentifers.add("ik");
        this.languageIdentifers.add("io");
        this.languageIdentifers.add("is");
        this.languageIdentifers.add("it");
        this.languageIdentifers.add("iu");
        this.languageIdentifers.add("ja");
        this.languageIdentifers.add("jv");
        this.languageIdentifers.add("ka");
        this.languageIdentifers.add("kg");
        this.languageIdentifers.add("ki");
        this.languageIdentifers.add("kk");
        this.languageIdentifers.add("kl");
        this.languageIdentifers.add("km");
        this.languageIdentifers.add("kn");
        this.languageIdentifers.add("ko");
        this.languageIdentifers.add("ks");
        this.languageIdentifers.add("ku");
        this.languageIdentifers.add("kv");
        this.languageIdentifers.add("kw");
        this.languageIdentifers.add("ky");
        this.languageIdentifers.add("la");
        this.languageIdentifers.add("lb");
        this.languageIdentifers.add("li");
        this.languageIdentifers.add("ln");
        this.languageIdentifers.add("lo");
        this.languageIdentifers.add("lt");
        this.languageIdentifers.add("lv");
        this.languageIdentifers.add("mg");
        this.languageIdentifers.add("mh");
        this.languageIdentifers.add("mi");
        this.languageIdentifers.add("mk");
        this.languageIdentifers.add("ml");
        this.languageIdentifers.add("mn");
        this.languageIdentifers.add("mo");
        this.languageIdentifers.add("mr");
        this.languageIdentifers.add("ms");
        this.languageIdentifers.add("mt");
        this.languageIdentifers.add("my");
        this.languageIdentifers.add("na");
        this.languageIdentifers.add("nb");
        this.languageIdentifers.add("ne");
        this.languageIdentifers.add("ng");
        this.languageIdentifers.add("nl");
        this.languageIdentifers.add("nn");
        this.languageIdentifers.add("no");
        this.languageIdentifers.add("nv");
        this.languageIdentifers.add("ny");
        this.languageIdentifers.add("oc");
        this.languageIdentifers.add("os");
        this.languageIdentifers.add("pa");
        this.languageIdentifers.add("pl");
        this.languageIdentifers.add("ps");
        this.languageIdentifers.add("pt");
        this.languageIdentifers.add("qu");
        this.languageIdentifers.add("rm");
        this.languageIdentifers.add("rn");
        this.languageIdentifers.add("ro");
        this.languageIdentifers.add("ru");
        this.languageIdentifers.add("rw");
        this.languageIdentifers.add("sa");
        this.languageIdentifers.add("sc");
        this.languageIdentifers.add("sd");
        this.languageIdentifers.add("se");
        this.languageIdentifers.add("sg");
        this.languageIdentifers.add("sh");
        this.languageIdentifers.add("si");
        this.languageIdentifers.add("sk");
        this.languageIdentifers.add("sl");
        this.languageIdentifers.add("sm");
        this.languageIdentifers.add("sn");
        this.languageIdentifers.add("so");
        this.languageIdentifers.add("sq");
        this.languageIdentifers.add("sr");
        this.languageIdentifers.add("ss");
        this.languageIdentifers.add("st");
        this.languageIdentifers.add("su");
        this.languageIdentifers.add("sv");
        this.languageIdentifers.add("sw");
        this.languageIdentifers.add("ta");
        this.languageIdentifers.add("te");
        this.languageIdentifers.add("tg");
        this.languageIdentifers.add("th");
        this.languageIdentifers.add("ti");
        this.languageIdentifers.add("tk");
        this.languageIdentifers.add("tl");
        this.languageIdentifers.add("tn");
        this.languageIdentifers.add("to");
        this.languageIdentifers.add("tr");
        this.languageIdentifers.add("ts");
        this.languageIdentifers.add("tt");
        this.languageIdentifers.add("tw");
        this.languageIdentifers.add("ty");
        this.languageIdentifers.add("ug");
        this.languageIdentifers.add("uk");
        this.languageIdentifers.add("ur");
        this.languageIdentifers.add("uz");
        this.languageIdentifers.add("ve");
        this.languageIdentifers.add("vi");
        this.languageIdentifers.add("vo");
        this.languageIdentifers.add("wa");
        this.languageIdentifers.add("wo");
        this.languageIdentifers.add("xh");
        this.languageIdentifers.add("yi");
        this.languageIdentifers.add("yo");
        this.languageIdentifers.add("za");
        this.languageIdentifers.add("zh");
        this.languageIdentifers.add("zu");
        this.languageIdentifers.add("als");
        this.languageIdentifers.add("ang");
        this.languageIdentifers.add("arc");
        this.languageIdentifers.add("ast");
        this.languageIdentifers.add("bug");
        this.languageIdentifers.add("ceb");
        this.languageIdentifers.add("chr");
        this.languageIdentifers.add("chy");
        this.languageIdentifers.add("csb");
        this.languageIdentifers.add("frp");
        this.languageIdentifers.add("fur");
        this.languageIdentifers.add("got");
        this.languageIdentifers.add("haw");
        this.languageIdentifers.add("ilo");
        this.languageIdentifers.add("jbo");
        this.languageIdentifers.add("ksh");
        this.languageIdentifers.add("lad");
        this.languageIdentifers.add("lmo");
        this.languageIdentifers.add("nah");
        this.languageIdentifers.add("nap");
        this.languageIdentifers.add("nds");
        this.languageIdentifers.add("nrm");
        this.languageIdentifers.add("pam");
        this.languageIdentifers.add("pap");
        this.languageIdentifers.add("pdc");
        this.languageIdentifers.add("pih");
        this.languageIdentifers.add("pms");
        this.languageIdentifers.add("rmy");
        this.languageIdentifers.add("scn");
        this.languageIdentifers.add("sco");
        this.languageIdentifers.add("tet");
        this.languageIdentifers.add("tpi");
        this.languageIdentifers.add("tum");
        this.languageIdentifers.add("udm");
        this.languageIdentifers.add("vec");
        this.languageIdentifers.add("vls");
        this.languageIdentifers.add("war");
        this.languageIdentifers.add("xal");
        this.languageIdentifers.add("simple");
    }

    private void initGermanVariables() {
        this.templateParserClass = FlushTemplates.class;
        this.imageIdentifers.add("Bild");
        this.imageIdentifers.add("Image");
        this.imageIdentifers.add("Datei");
        this.categoryIdentifers.add("Kategorie");
        this.languageIdentifers.remove("de");
    }

    private void initEnglishVariables() {
        this.templateParserClass = FlushTemplates.class;
        this.imageIdentifers.add("Image");
        this.imageIdentifers.add("File");
        this.imageIdentifers.add("media");
        this.categoryIdentifers.add("Category");
        this.languageIdentifers.remove("en");
    }

    private String resolveLineSeparator() {
        if(this.lineSeparator.equals("CRLF")) {
            return "\r\n";
        } else if(this.lineSeparator.equals("LF")) {
            return "\n";
        } else {
//            this.logger.error("LineSeparator is UNKNOWN: \"" + this.lineSeparator + "\"\n" + "Set LineSeparator to \"LF\" or \"CRLF\" for a Error free configuration");
            return this.lineSeparator;
        }
    }

    public MediaWikiParser createParser() {
//        this.logger.debug("Selected Parser: " + this.parserClass);
        if(this.parserClass != ModularParser.class) {
//            this.logger.error("Parser Class Not Found!");
            return null;
        } else {
            ModularParser mwgp = new ModularParser("\n", this.languageIdentifers, this.categoryIdentifers, this.imageIdentifers, this.showImageText, this.deleteTags, this.showMathTagContent, this.calculateSrcSpans, (MediaWikiTemplateParser)null);
            StringBuilder sb = new StringBuilder();
            sb.append(this.lineSeparator + "languageIdentifers: ");
            Iterator i$ = this.languageIdentifers.iterator();

            String s;
            while(i$.hasNext()) {
                s = (String)i$.next();
                sb.append(s + " ");
            }

            sb.append(this.lineSeparator + "categoryIdentifers: ");
            i$ = this.categoryIdentifers.iterator();

            while(i$.hasNext()) {
                s = (String)i$.next();
                sb.append(s + " ");
            }

            sb.append(this.lineSeparator + "imageIdentifers: ");
            i$ = this.imageIdentifers.iterator();

            while(i$.hasNext()) {
                s = (String)i$.next();
                sb.append(s + " ");
            }

//            this.logger.debug(sb.toString());
//            this.logger.debug("Selected TemplateParser: " + this.templateParserClass);
            Object mwtp;
            if(this.templateParserClass == GermanTemplateParser.class) {
                i$ = this.deleteTemplates.iterator();

//                String s;
                while(i$.hasNext()) {
                    s = (String)i$.next();
//                    this.logger.debug("DeleteTemplate: '" + s + "'");
                }

                i$ = this.parseTemplates.iterator();

                while(i$.hasNext()) {
                    s = (String)i$.next();
//                    this.logger.debug("ParseTemplate: '" + s + "'");
                }

                mwtp = new GermanTemplateParser(mwgp, this.deleteTemplates, this.parseTemplates);
            } else if(this.templateParserClass == FlushTemplates.class) {
                mwtp = new FlushTemplates();
            } else {
                if(this.templateParserClass != ShowTemplateNamesAndParameters.class) {
//                    this.logger.error("TemplateParser Class Not Found!");
                    return null;
                }

                mwtp = new ShowTemplateNamesAndParameters();
            }

            mwgp.setTemplateParser((MediaWikiTemplateParser)mwtp);
            return mwgp;
        }
    }

    public void addDeleteTemplate(String deleteTemplate) {
        this.deleteTemplates.add(deleteTemplate);
    }

    public void addParseTemplate(String parseTemplate) {
        this.parseTemplates.add(parseTemplate);
    }

    public Class getParserClass() {
        return this.parserClass;
    }

    public void setParserClass(Class parserClass) {
        this.parserClass = parserClass;
    }

    public Class getTemplateParserClass() {
        return this.templateParserClass;
    }

    public void setTemplateParserClass(Class templateParserClass) {
        this.templateParserClass = templateParserClass;
    }

    public List<String> getDeleteTemplates() {
        return this.deleteTemplates;
    }

    public void setDeleteTemplates(List<String> deleteTemplates) {
        this.deleteTemplates = deleteTemplates;
    }

    public String getLineSeparator() {
        return this.lineSeparator;
    }

    public void setLineSeparator(String lineSeparator) {
        this.lineSeparator = lineSeparator;
    }

    public List<String> getParseTemplates() {
        return this.parseTemplates;
    }

    public void setParseTemplates(List<String> parseTemplates) {
        this.parseTemplates = parseTemplates;
    }

    public List<String> getLanguageIdentifers() {
        return this.languageIdentifers;
    }

    public void setLanguageIdentifers(List<String> languageIdentifers) {
        this.languageIdentifers = languageIdentifers;
    }

    public List<String> getCategoryIdentifers() {
        return this.categoryIdentifers;
    }

    public void setCategoryIdentifers(List<String> categoryIdentifers) {
        this.categoryIdentifers = categoryIdentifers;
    }

    public List<String> getImageIdentifers() {
        return this.imageIdentifers;
    }

    public void setImageIdentifers(List<String> imageIdentifers) {
        this.imageIdentifers = imageIdentifers;
    }

    public boolean getShowImageText() {
        return this.showImageText;
    }

    public void setShowImageText(boolean showImageText) {
        this.showImageText = showImageText;
    }

    public boolean getDeleteTags() {
        return this.deleteTags;
    }

    public void setDeleteTags(boolean deleteTags) {
        this.deleteTags = deleteTags;
    }

    public boolean getShowMathTagContent() {
        return this.showMathTagContent;
    }

    public void setShowMathTagContent(boolean showMathTagContent) {
        this.showMathTagContent = showMathTagContent;
    }

    public boolean getCalculateSrcSpans() {
        return this.calculateSrcSpans;
    }

    public void setCalculateSrcSpans(boolean calculateSrcSpans) {
        this.calculateSrcSpans = calculateSrcSpans;
    }
}
