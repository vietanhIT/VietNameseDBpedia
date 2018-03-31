package com.vietnamesedbpedia.dbpedia;

import java.util.ArrayList;

/**
 * Created by vieta on 22/11/2016.
 */
public class Language {

    public static ArrayList<String> getLanguages(){
        ArrayList<String> languages = new ArrayList<>();
        languages.add(Constants.WIKI_DATA);
        languages.add(Constants.ENGLISH);
        languages.add(Constants.GERMANY);
        languages.add(Constants.ITALIA);
        languages.add(Constants.PORTUGAL);
        languages.add(Constants.SPAIN);
        languages.add(Constants.FRANCE);
        languages.add(Constants.ARABIC);
        languages.add(Constants.AZERBAIJAN);
        languages.add(Constants.BELGIUM);
        languages.add(Constants.BULGARI);
        languages.add(Constants.INDONESIA);
        languages.add(Constants.NETHERLAND);
        languages.add(Constants.TURKEY);
        languages.add(Constants.RUSSIA);
        languages.add(Constants.HUNGARI);

        return languages;
    }
}
