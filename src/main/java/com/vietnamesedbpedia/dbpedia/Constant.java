package com.vietnamesedbpedia.dbpedia;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by vieta on 5/4/2017.
 */
public class Constant {
    //page-inter-language
    public static final String PAGE = "page";
    public static final String LANGUAGE = "language";
    public static final String VALUE = "value";
    //page-type
    public static final String TYPE = "type";
    public static final String PROPERTY = "property";
    public static final String VALUE_TYPE = "value_type";
    public static final String VALUE_TYPE_OBJECT = "object";
//    public static final

    //language
    public static final String WIKI_DATA = "http://wikidata.dbpedia.org";
    public static final String ENGLISH = "http://dbpedia.org";
    public static final String GERMANY = "http://de.dbpedia.org";
    public static final String ITALIA = "http://it.dbpedia.org";
    public static final String PORTUGAL = "http://pt.dbpedia.org";
    public static final String SPAIN = "http://es.dbpedia.org";
    public static final String FRANCE = "http://fr.dbpedia.org";
    public static final String ARABIC = "http://ar.dbpedia.org";
    public static final String AZERBAIJAN ="http://az.dbpedia.org";
    public static final String BELGIUM = "http://be.dbpedia.org";
    public static final String BULGARI = "http://bg.dbpedia.org";
    public static final String INDONESIA = "http://id.dbpedia.org";
    public static final String NETHERLAND = "http://nl.dbpedia.org";
    public static final String TURKEY = "http://tr.dbpedia.org";
    public static final String RUSSIA = "http://ru.dbpedia.org";
    public static final String HUNGARI = "http://hu.dbpedia.org";

    public static final String VIETNAMESE = "http://vi.dbpedia.org";

    //index folder
    public static final String VIETNAMESE_INDEX = "index-vi";
    public static final String ENGLISH_INDEX = "index-en";
    public static final String GERMANY_INDEX = "index-de";
    public static final String NETHERLAND_INDEX = "index-nl";
    public static final String ONTOLOGY_2016 = "dbpedia_2016-04.owl";

    public static final String TYPE_THING = "http://www.w3.org/2002/07/owl#Thing";

    public static final String RESULT_FILE_NAME = "result.txt";

    public static final String REMOVE1 = "_tháng_";
    public static final String REMOVE2 = "Theo_";
    public static final String REMOVE3 = "(định_hướng)";
    public static final String REMOVE4 = "_(số)";
    public static final String pattern_REMOVE1 = "\\d_tháng_\\d";
    public static final String pattern_REMOVE5 = "Tháng_\\d_năm_\\d";
    public static final String REMOVE6 = "Hàng_không_năm_";

    public static final String LINK_TEMPLATE = "http://mappings.dbpedia.org/server/templatestatistics/vi/?template=";


    public static void main(String []args){
        Pattern pattern = Pattern.compile(pattern_REMOVE5);
        Matcher matcher= pattern.matcher("lkl_tháng_2");

        System.out.println(matcher.find());
    }
}
