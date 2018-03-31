package com.vietnamesedbpedia.dbpedia;

import de.tudarmstadt.ukp.wikipedia.parser.Content;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.Number;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import com.vietnamesedbpedia.model.Attribute;
import com.vietnamesedbpedia.model.Property;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.analysis.HardTokenizer;
import org.fbk.cit.hlt.thewikimachine.analysis.Tokenizer;
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;
import org.fbk.cit.hlt.thewikimachine.util.FrequencyHashSet;
import org.fbk.cit.hlt.thewikimachine.util.StringKernel;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiMarkupParser;
import info.bliki.wiki.filter.WikipediaParser;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by vieta on 21/2/2017.
 */
public class Test {


    public void compareNumer(String value, String destValue, Property p, Attribute a, HashMap<String, HashMap<String, Double>> results) throws IOException {

        Logger logger = Logger.getLogger(Test.class);
        destValue = destValue.replaceAll("&nbsp;", " ");
        destValue = StringEscapeUtils.unescapeXml(destValue);
        logger.trace("Text cleaned");
        WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
        // Parse page
        ParsedPage parsedPage = wikiMarkupParser.parsePage(destValue);
        if (parsedPage == null) {

        }

        ArrayList<String> textTokens = getTextFromWiki(parsedPage);
        logger.trace("Text tokens: " + textTokens);
        String label = "yes";

        double precision = .01;
        float floatValue = Float.parseFloat(value);
        ArrayList<HashSet> ret = getNumbersFromTokens(textTokens);
        HashSet<Long> intValues = (HashSet<Long>) ret.get(0);
        HashSet<Double> doubleValues = (HashSet<Double>) ret.get(1);

        logger.trace("Value: " + floatValue);
        logger.trace("Tokens: " + textTokens);
        logger.trace("int values: " + intValues);
        logger.trace("double values: " + doubleValues);

        for (Long number : intValues) {
            if (floatValue != 0) {
                if ((Math.abs(floatValue - number) / floatValue) < precision) {
                    logger.error("Added " + p.getName() + "---" + a.getName());
                    results.get(a.getName()).put(p.getName(), 1.0);
//                    results.get(template).add(label);
                    break;
                } else if (number != 0) {
                    long differenceOfDigits = Math.round(Math.log10(Math.floor(floatValue / number)));
                    logger.trace("Difference of digits: " + differenceOfDigits);
                    logger.trace("Pow: " + Math.pow(10, differenceOfDigits));
                    number = (long) (number * Math.pow(10, differenceOfDigits));
                    logger.trace("Number after pow: " + number);
                    if ((Math.abs(floatValue - number) / floatValue) < precision) {
                        logger.error("Added (after pow) " + p.getName() + "---" + a.getName());
                        System.out.println(results.get(p.getName()));
                        results.get(a.getName()).put(p.getName(), 1.0);
//                        results.get(template).add(label);
//                        continue dbpediaLoop;
                        break;
                    }
                }
            } else {
                if (number == 0) {
                    logger.error("Added " + p.getName() + "---" + a.getName());
                    results.get(a.getName()).put(p.getName(), 1.0);
//                    results.get(template).add(label);
//                    continue dbpediaLoop;
                    break;

                }
            }
        }
        for (Double number : doubleValues) {
            if (floatValue != 0) {
                if ((Math.abs(floatValue - number) / floatValue) < precision) {
                    logger.error("Added " + p.getName() + "---" + a.getName());
                    results.get(a.getName()).put(p.getName(), 1.0);
//                    results.get(template).add(label);
//                    continue dbpediaLoop;
                    break;
                } else if (number != 0) {
                    long differenceOfDigits = Math.round(Math.log10(Math.floor(floatValue / number)));
                    logger.trace("Difference of digits: " + differenceOfDigits);
                    logger.trace("Pow: " + Math.pow(10, differenceOfDigits));
                    number = (double) (number * Math.pow(10, differenceOfDigits));
                    logger.trace("Number after pow: " + number);
                    if ((Math.abs(floatValue - number) / floatValue) < precision) {
                        logger.error("Added (after pow) " + p.getName() + "---" + a.getName());
                        results.get(a.getName()).put(p.getName(), 1.0);
//                        results.get(template).add(label);
//                        continue dbpediaLoop;
                        break;
                    }
                }
            } else {
                if (number == 0) {
                    logger.error("Added " + p.getName() + "---" + a.getName());
                    results.get(a.getName()).put(p.getName(), 1.0);
//                    results.get(template).add(label);
//                    continue dbpediaLoop;
                    break;
                }
            }
        }
    }

    public static ArrayList<String> getTextFromWiki(ParsedPage parsedPage) {
        Tokenizer tokenizer = HardTokenizer.getInstance();
        ArrayList<String> sb = new ArrayList<String>();

        String rawContent;
        String[] tokenizedContent;
        List<Content> list;

        for (Section section : parsedPage.getSections()) {
            list = section.getContentList();
            for (int i = 0; i < list.size(); i++) {
                rawContent = list.get(i).getText().toLowerCase();
                if (rawContent.length() > 0) {
                    tokenizedContent = tokenizer.stringArray(rawContent);
                    if (tokenizedContent.length > 0) {
                        sb.addAll(Arrays.asList(tokenizedContent));
                    }

                }
            }
        }
        return sb;
    }

    public static ArrayList<HashSet> getNumbersFromTokens(ArrayList<String> input) {
        ArrayList<HashSet> ret = new ArrayList<HashSet>();
        ret.add(new HashSet<Long>()); // Long are on index 0
        ret.add(new HashSet<Double>()); // Float are on index 1

        ArrayList<String> allowedTokens = new ArrayList<String>();
        allowedTokens.add(".");
        allowedTokens.add(",");
        Pattern numbers = Pattern.compile("^[0-9]+$");

        ArrayList<String> temp = new ArrayList<String>();
        ArrayList<Integer> tempTokens = new ArrayList<Integer>();
        FrequencyHashSet<String> tempTokensFreq = new FrequencyHashSet<String>();
        long lastTempToken = -2;
        boolean hasMinus = false;

        for (String token : input) {
            if (token.equals("-") && temp.size() == 0) {
                hasMinus = true;
                continue;
            }
            if (allowedTokens.contains(token)) {
                temp.add(token);
                tempTokensFreq.add(token);
                int thisTempToken = temp.size() - 1;
                if (thisTempToken != lastTempToken + 1) {
                    tempTokens.add(thisTempToken);
                    lastTempToken = thisTempToken;
                    continue;
                }
            }
            if (numbers.matcher(token).find()) {
                temp.add(token);
                continue;
            }

            for (int i = temp.size() - 1; i >= 0; i--) {
                if (tempTokens.contains(i)) {
                    temp.remove(i);
                    tempTokens.remove(tempTokens.indexOf(i));
                    continue;
                }
                break;
            }
            if (temp.size() > 0) {
                Integer hasComma = null;
                if (tempTokens.size() > 0) {
                    int maxValue = Collections.max(tempTokens);
                    if (tempTokensFreq.getZero(temp.get(maxValue)) == 1) {
                        hasComma = maxValue;
                    }
                }

                // Integer
                String finalInt = "";
                String finalFloat = "";
                for (int i = 0; i < temp.size(); i++) {
                    String s = temp.get(i);
                    if (hasComma != null && i == hasComma) {
                        finalFloat += ".";
                    }
                    if (tempTokens.contains(i)) {
                        continue;
                    }
                    finalInt += s;
                    if (hasComma != null) {
                        finalFloat += s;
                    }
                }

                if (finalInt.length() > 0) {
                    try {
                        long number = Long.parseLong((hasMinus ? "-" : "") + finalInt);
                        ret.get(0).add(number);
                    } catch (Exception ignored) {
                    }
                }
                if (finalFloat.length() > 0) {
                    try {
                        double number = Double.parseDouble((hasMinus ? "-" : "") + finalFloat);
                        ret.get(1).add(number);
                    } catch (Exception ignored) {
                    }
                }
            }

            // Reset
            temp = new ArrayList<String>();
            tempTokens = new ArrayList<Integer>();
            tempTokensFreq = new FrequencyHashSet<String>();
            lastTempToken = -2;
            hasMinus = false;
        }
        for (int i = temp.size() - 1; i >= 0; i--) {
            if (tempTokens.contains(i)) {
                temp.remove(i);
                tempTokens.remove(tempTokens.indexOf(i));
                continue;
            }
            break;
        }
        if (temp.size() > 0) {
            Integer hasComma = null;
            if (tempTokens.size() > 0) {
                int maxValue = Collections.max(tempTokens);
                if (tempTokensFreq.getZero(temp.get(maxValue)) == 1) {
                    hasComma = maxValue;
                }
            }

            // Integer
            String finalInt = "";
            String finalFloat = "";
            for (int i = 0; i < temp.size(); i++) {
                String s = temp.get(i);
                if (hasComma != null && i == hasComma) {
                    finalFloat += ".";
                }
                if (tempTokens.contains(i)) {
                    continue;
                }
                finalInt += s;
                if (hasComma != null) {
                    finalFloat += s;
                }
            }

            if (finalInt.length() > 0) {
                try {
                    long number = Long.parseLong((hasMinus ? "-" : "") + finalInt);
                    ret.get(0).add(number);
                } catch (Exception ignored) {
                }
            }
            if (finalFloat.length() > 0) {
                try {
                    double number = Double.parseDouble((hasMinus ? "-" : "") + finalFloat);
                    ret.get(1).add(number);
                } catch (Exception ignored) {
                }
            }
        }

        return ret;
    }

    private static String MAIN_FOLDER = "template_statistic" + File.separator + "infobox-Thong_tin_don_vi_hanh_chinh";

    public static void main(String args[]) {
//         compare1("Thông_tin_nhạc_sĩ");
////        com.vietnamesedbpedia.dbpedia.Test test = new com.vietnamesedbpedia.dbpedia.Test();
//        // try{
////            test.compareNumer("3.3289E9","3.328,9 km²");
////        }catch (Exception e){
////            e.getMessage();
////        }
////
////
////        System.out.println(range);
//
//        //compare
////        File file = new File(MAIN_FOLDER);
////        if (file.isDirectory()) {
////            String names[] = file.list();
////            for (String a : names) {
////                test.compare(a.replace(".txt", ""), com.vietnamesedbpedia.dbpedia.Constants.ENGLISH);
////            }
////        }

        try{
            WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
            // Parse page
            ParsedPage parsedPage = wikiMarkupParser.parsePage("{{Birth date and age|1960|10|30|df=yes}}");
            if (parsedPage == null) {

            }

            ArrayList<String> textTokens = getTextFromWiki(parsedPage);
            System.out.println(textTokens.toArray());
        }catch (Exception e){
            e.getMessage();
        }


    }

    private static void compare1(String namefile) {
        Test test = new Test();
//

        //test.compareWithMappingFile();
        try {
            HashMap<String, String> attrAndCountMap = new HashMap<>();
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            File f = new File("template_statistic", namefile + ".xls");
            Workbook w = Workbook.getWorkbook(f,ws);
            Sheet sheet = w.getSheet(0);
            ArrayList<Attribute> attributes = new ArrayList<>();
            int count = 0;

            for (int i = 0; i < sheet.getRows(); i++) {
                Attribute attribute = new Attribute();
//                if (sheet.getCell(0, i).getContents().contains("authority")) {
//                    count++;
//                }
                attribute.setName(sheet.getCell(0, i).getContents());
                attribute.setCount(sheet.getCell(1, i).getContents());
                attrAndCountMap.put(sheet.getCell(0, i).getContents(),sheet.getCell(1, i).getContents() );
                attributes.add(attribute);
                System.out.println(sheet.getCell(0, i).getContents());
            }

            HashMap<String, String> mapVietAtrrToEnAttr = new HashMap<>();

            //neu ban mau la thong tin vien chuc:
            if(namefile.equals("Thông_tin_viên_chức")){
                try{
                    File file = new File("template_statistic","Thông_tin_viên_chức.txt");
                    FileReader fileReader = new FileReader(file);
                    BufferedReader bufferedReader = new BufferedReader(fileReader);

                    String line;

                    while ((line = bufferedReader.readLine())!=null){
                        if(line.contains("|")){
                            String[] a= line.replace("|","").trim().split(Pattern.quote("="));
                            if(a.length==2){
                                mapVietAtrrToEnAttr.put(a[0].trim(),a[1].trim() );
                            }
                        }
                    }

                    bufferedReader.close();
                    fileReader.close();
                }catch (Exception e){
                    e.getMessage();
                }

            }

            HashMap<String,String> h1 = Search1.getViInterLanguageLink("Bản_mẫu:"+namefile);
            String nameInfobox ="";
            if(h1.size()>0){
               nameInfobox = h1.get("Bản_mẫu:"+namefile).replace("Template:","").toLowerCase();
            }

            if(namefile.equals("Thông_tin_diễn_viên")){
                nameInfobox = "infobox_actor";
            }else if(namefile.equals("Thông_tin_hồ")){
                nameInfobox = "infobox_lake";
            }else if(namefile.equals("Thông_tin_núi")){
                nameInfobox = "infobox_mountain";
            }else if(namefile.equals("Thông_tin_về_sân_vận_động")){
                nameInfobox = "infobox_stadium";
            }

            test.compareWithMappingFile(attributes, namefile + "_result", nameInfobox, mapVietAtrrToEnAttr);
            System.out.println(count);
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }

    public void compareWithMappingFile(ArrayList<Attribute> attributes, String fileResult, String nameInfobox, HashMap<String, String> mapVietAtrrToEnAttr) {
        String name = fileResult + ".xls";
        HashMap<Attribute, String> resultMapping = new HashMap<>();
        HashMap<Attribute, String> totalMapping = new HashMap<>();

        HashMap<String, HashMap<String, String>> hashMap = ReadMappingFromFile.readFileMapping();
        ArrayList<Attribute> noMappingAttributes = new ArrayList<>();
        //ArrayList<Attribute> attributes = getDataAttribute(name + ".txt");
        HashMap<String, String> mapping = hashMap.get(nameInfobox);
        Set<String> templateProperties = mapping.keySet();

        System.out.println("contain: " +templateProperties.toString() +"  ---  "+ templateProperties.contains("latNS"));
        for (Attribute a : attributes) {
            if(mapVietAtrrToEnAttr.size()>0){
                String en_attr = mapVietAtrrToEnAttr.get(a.getName());
                if(en_attr!=null){
                    if(templateProperties.contains(en_attr)||templateProperties.contains(en_attr.toLowerCase())){
                        System.out.println(a.getName() + " --- "+ en_attr + " --- "+ mapping.get(en_attr));
                        resultMapping.put(a, mapping.get(en_attr));
                        totalMapping.put(a, mapping.get(en_attr));
                    }
                }
            }
            if (templateProperties.contains(a.getName())||templateProperties.contains(a.getName().toLowerCase())) {
                resultMapping.put(a, mapping.get(a.getName()));
                totalMapping.put(a, mapping.get(a.getName()));
            } else {
                if(!resultMapping.containsKey(a)&&!noMappingAttributes.contains(a)){
                    resultMapping.put(a, "");
                    noMappingAttributes.add(a);
                }
            }
        }

//        if(mapVietAtrrToEnAttr.size()>0){
//            Set<String> vietattrs = mapVietAtrrToEnAttr.keySet();
//            for(String a: vietattrs){
//                String en_attr = mapVietAtrrToEnAttr.get(a);
//                if (templateProperties.contains(en_attr)) {
//                    resultMapping.remove(a);
//                    resultMapping.put(a, mapping.get(mapVietAtrrToEnAttr.get(a)));
//                    totalMapping.remove(a);
//                    totalMapping.put(a, mapping.get(mapVietAtrrToEnAttr.get(a)));
//                }
//            }
//        }

        HashMap<Attribute, String> totalMappingExtends = new HashMap<>();

        try {
            Set<Attribute> keys = totalMapping.keySet();
            for (Attribute a : keys) {
                for (Attribute b : noMappingAttributes) {
                    try {
                        if (!a.getName().contains("_")) {
                            String a_name_model = modelString(a.getName());
                            if (b.getName().contains(a_name_model)) {
                                String c = b.getName().replace(a_name_model, "");
                                //System.out.println("c: " + c + "---" + b.getName() + "--- " + a.getName() + " --- " + StringUtils.isNumeric(c));
                                if (StringUtils.isNumeric(c)) {
                                    //System.out.println("yes");
                                    System.out.println(a.getName() + " --- " + b.getName());
                                    resultMapping.remove(b);
                                    resultMapping.put(b, resultMapping.get(a));
                                    totalMappingExtends.put(b, resultMapping.get(a));
                                }
                            }
                        } else {
                            String[] asplits;
                            if (!a.getName().contains(" ")) {
                                asplits = a.getName().split("_");
                            } else {
                                //System.out.println(a.getName());
                                asplits = a.getName().split(" ");
                            }
                            String[] bsplits;
                            if (!b.getName().contains(" ")) {
                                bsplits = b.getName().split("_");
                            } else {
                                bsplits = b.getName().split(" ");
                            }

                            if (bsplits.length >= asplits.length) {  /// ==
                                String c = b.getName().replace("_", "");

                                for (String d : asplits) {
                                    c = c.replace(d, "");
                                    //System.out.println(a.getName() + "size: " + asplits.length + "---" + b.getName()+ "size: " + asplits.length  + "--- c: "+ c);

                                }
                                //System.out.println("c: " + c);
                                if (StringUtils.isNumeric(c) || c.trim().equals("")) {
                                    System.out.println(a.getName() + " --- " + b.getName());
                                    resultMapping.remove(b);
                                    resultMapping.put(b, resultMapping.get(a));
                                    totalMappingExtends.put(b, resultMapping.get(a));
                                }

                            }
                        }
                    } catch (Exception e) {


                        System.out.println("Error: " + a.getName() + "--" + b.getName() + "--" + e.getMessage() + " --- ");
                    }


                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("error: " + e.getMessage() + "---" + Thread.currentThread().getStackTrace()[2].getLineNumber());
        }

        Set<Attribute> keys = totalMappingExtends.keySet();
        for (Attribute a : keys) {
            totalMapping.remove(a);
            totalMapping.put(a, totalMappingExtends.get(a));
            noMappingAttributes.remove(a);
        }

        System.out.println("Size: " + attributes.size());
        System.out.println("Mapping: " + totalMapping.size());
        System.out.println("No Mapping: " + noMappingAttributes.size());
        System.out.println("New extends " + totalMappingExtends.size());
        //writeFileTxt(name + "_result.txt", resultMapping);
        writeToFileExcel(name, resultMapping);
    }

    private void writeToFileExcel(String name, HashMap<Attribute, String> resultMapping) {
        try {
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            WritableWorkbook workbook = Workbook.createWorkbook(new File("template_statistic",name), ws);
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

            Set<Attribute> attributes = resultMapping.keySet();
            int index = 0;
            for (Attribute a : attributes) {
                sheet.addCell(new Label(0, index, a.getName()));
                sheet.addCell(new Number(1, index, Double.valueOf(a.getCount())));
                sheet.addCell(new Label(2, index, resultMapping.get(a)));
                if(resultMapping.get(a)==null||resultMapping.get(a).equals("")){
                    sheet.addCell(new Number(3, index, 0.0));
                }else{
                    sheet.addCell(new Number(3, index, Double.valueOf(a.getCount())));
                }
                index++;
            }

            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    private void writeFileTxt(String nameFile, HashMap<String, String> hashMap) {
        try {
            FileWriter fileWriter = new FileWriter(nameFile);
            BufferedWriter bf = new BufferedWriter(fileWriter);
            Set<String> keys = hashMap.keySet();
            for (String key : keys) {
                String line = key + " " + hashMap.get(key);
                bf.write(line + "\n");
            }

            bf.close();
            fileWriter.close();
        } catch (Exception e) {
            e.getMessage();
        }
    }

    public void compare(String name, String language) {
        Logger logger = Logger.getLogger(Test.class);
        logger.debug("Start");
        HashMap<String, HashMap<String, Double>> results = new HashMap<>();
        ArrayList<Property> properties = new ArrayList<>();
        ArrayList<Attribute> attributes = new ArrayList<>();
        String owlFile = Constant.ONTOLOGY_2016;
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        String nameTran = "";
        try {
            SearchDBpediaAllDump searchDBpediaAllDump = new SearchDBpediaAllDump("ALL_DB_DUMP");
            HashMap<String, String> h4;
            if(language.equals(Constants.ENGLISH)){
                h4 = Search1.getViInterLanguageLink(name);
            }else if(language.equals(Constants.GERMANY)){
                h4 = Search1.getViInterLanguageLink_de(name);
            }else{
                h4 = Search1.getViInterLanguageLink_nl(name);
            }
            nameTran = h4.get(name);
            // Ho_Chi_Minh
//             // Ho_Chi_Minh
//            // Ho_Chi_Minh
            if(language.equals(Constants.ENGLISH)){
                properties = searchDBpediaAllDump.getAllPropertyFromDBpediaByTitleArticle(nameTran);
            }else if(language.equals(Constants.GERMANY)){
                properties = searchDBpediaAllDump.getAllPropertyFromDBpediaByTitleArticle_de(nameTran);
            }else {
                properties = searchDBpediaAllDump.getAllPropertyFromDBpediaByTitleArticle_nl(nameTran);
            }

            attributes = getDataAttribute(MAIN_FOLDER + File.separator + name + ".txt");
//            for (Property p : properties) {
//                results.put(p.getName(), new HashMap<String, Double>());
//            }

            for (Attribute a : attributes) {
                HashMap<String, Double> hashMap = new HashMap<String, Double>();
                results.put(a.getName(), hashMap);
            }

//            Set<String> hhh1 = results.keySet();
//            for(String a: hhh1){
//                System.out.println(a);
//                System.out.println(results.get(a));
//            }

            for (Property p : properties) {
                if (!p.isObject()) {
                    HashMap<String, String> h1 = airpediaOntology.getProperty(p.getName());
//                    HashMap<String, String> h1 = new HashMap<>();
                    if (h1 != null) {
                        String range = h1.get("range");
                        logger.error(p.getName());
                        logger.error(range);
                        if (range != null) {
                            if (range.equals("string")) {
                                for (Attribute a : attributes) {
                                    StringKernel sk;
                                    double k;
                                    int ngramsLengthForString = 2;
                                    double lambda = .2;
                                    int tokenLimit = 50;
//                                    logger.error("VALUE: " +p.getValue() );
                                    Tokenizer tokenizer = HardTokenizer.getInstance();
                                    String[] tokenizedValue = tokenizer.stringArray(StringEscapeUtils.unescapeJava(p.getValue()).toLowerCase().replace("_", " "));

//                                    logger.error("tokenizedValue: " +Arrays.toString(tokenizedValue) );

                                    String destValue = a.getValue();

                                    destValue = destValue.replaceAll("&nbsp;", " ");
                                    destValue = StringEscapeUtils.unescapeXml(destValue);
//                                    logger.trace("Text cleaned");
                                    WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
                                    // Parse page
                                    ParsedPage parsedPage = wikiMarkupParser.parsePage(destValue);
                                    if (parsedPage == null) {

                                    }

                                    ArrayList<String> textTokens = getTextFromWiki(parsedPage);
                                    boolean tooManyTokens = false;
                                    if (textTokens.size() > tokenLimit) {
                                        tooManyTokens = true;
                                    }

//                                    logger.error("textTokens: " +textTokens);
                                    if (!tooManyTokens) {
                                        for (int j = ngramsLengthForString; j > 0; j--) {
                                            sk = new StringKernel(lambda, j);
                                            k = sk.getNormalized(tokenizedValue, textTokens.toArray(new String[textTokens.size()]));
                                            if (k > 0) {
                                                logger.error("Added " + p.getName() + " " + a.getName() + " " + k);
                                                //results.get(p.getName()).put(a.getName(), k);
                                                results.get(a.getName()).put(p.getName(), k);
//                                    results.get(template).add(label, k);
                                                break;
                                            }
                                        }
                                    }

                                    List<String> links = getLinksFromWiki(parsedPage);
                                    logger.error(links.toString());
                                    ArrayList<String> newTokens = new ArrayList<String>();
                                    for (String link : links) {
                                        logger.error("Searching translation for " + link);
                                        HashMap<String, String> h3 = new HashMap<>();
                                        if(language.equals(Constants.ENGLISH)){
                                            h3 = Search1.getViInterLanguageLink(link);
                                        }else if(language.equals(Constants.GERMANY)){
                                            h3 = Search1.getViInterLanguageLink_de(link);
                                        }else{
                                            h3 = Search1.getViInterLanguageLink_nl(link);
                                        }
                                        String stringTranslation = h3.get(link);
                                        if (stringTranslation == null || stringTranslation.equals("")) {
                                            logger.error("Value translation not found");
                                            continue;
                                        }
                                        logger.error("Translation found: " + stringTranslation);
                                        String[] parts = stringTranslation.toLowerCase().split("_");
                                        newTokens.addAll(Arrays.asList(parts));
                                    }
                                    if (newTokens.size() > 0 && newTokens.size() <= tokenLimit) {
                                        for (int j = ngramsLengthForString; j > 0; j--) {
                                            sk = new StringKernel(lambda, j);
                                            k = sk.getNormalized(newTokens.toArray(new String[newTokens.size()]), tokenizedValue);
                                            if (k > 0) {
                                                logger.error("Added " + " " + p.getName() + " " + a.getName());
//                                        results.get(p.getName()).put(a.getName(), k);
                                                results.get(a.getName()).put(p.getName(), k);
//                                    results.get(template).add(label, k);
                                                break;
                                            }
                                        }
                                    }
                                }
                            }
                            if (range.equals("double") ||
                                    range.equals("float") ||
                                    range.equals("nonNegativeInteger") ||
                                    range.equals("positiveInteger") ||
                                    range.equals("integer")) {
                                for (Attribute a : attributes) {
                                    compareNumer(p.getValue(), a.getValue(), p, a, results);
                                }
                            } else if (!range.equals("string")) {
                                for (Attribute a : attributes) {
                                    String destValue = a.getValue();

                                    destValue = destValue.replaceAll("&nbsp;", " ");
                                    destValue = StringEscapeUtils.unescapeXml(destValue);
                                    WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
                                    // Parse page
                                    ParsedPage parsedPage = wikiMarkupParser.parsePage(destValue);
                                    if (parsedPage == null) {

                                    }

                                    ArrayList<String> textTokens = getTextFromWiki(parsedPage);
                                    if(textTokens.size()==0){
                                        String[] values = destValue.split(Pattern.quote("|"));
                                        for(String v: values){
                                            v = v.replaceAll("\\W", "");
                                            textTokens.add(v);
                                        }
                                    }
                                    String value = p.getValue();
                                    if (range.equals("date")) {

                                        if (value.startsWith("-")) { // value Dbpedia value, textTokens Wikipedia value
                                            value = value.substring(1);
                                        }

                                        String[] parts = value.split("-");
                                        int day = Integer.parseInt(parts[2]);
                                        int month = Integer.parseInt(parts[1]);
                                        int year = Integer.parseInt(parts[0]);

                                        if (countOfNumberInText(textTokens) >= 3 && textTokens.contains(Integer.toString(year)) && textTokens.contains(Integer.toString(day)) && (textTokens.contains(Integer.toString(month)))) {
                                            // results.get(p.getName()).put(a.getName(), 1.0);
                                            results.get(a.getName()).put(p.getName(), 1.0);
                                            continue;
                                        }
                                        if (countOfNumberInText(textTokens) >= 1 && textTokens.contains(Integer.toString(year))) {
                                            //.33
                                            //results.get(p.getName()).put(a.getName(), 0.33);
                                            results.get(a.getName()).put(p.getName(), 0.33);
                                        }

                                        if (countOfNumberInText(textTokens) >= 2 && textTokens.contains(month)) {
                                            results.get(p.getName()).put(a.getName(), 0.33);
                                            if (textTokens.contains(Integer.toString(day))) {
                                                //results.get(p.getName()).put(a.getName(), 0.66);
                                                results.get(a.getName()).put(p.getName(), 0.66);
                                            }
                                        }

                                        continue;
                                    }
                                    if (range.equals("gYearMonth")) {
                                        if (value.startsWith("-")) {
                                            value = value.substring(1);
                                        }

                                        String[] parts = value.split("-");
                                        int month = Integer.parseInt(parts[1]);
                                        int year = Integer.parseInt(parts[0]);
                                        //String monthName = months[month];
                                        if (countOfNumberInText(textTokens) >= 1 && textTokens.contains(Integer.toString(year))) {
                                            //results.get(p.getName()).put(a.getName(), 0.5);
                                            results.get(a.getName()).put(p.getName(), 0.5);
                                        }
                                        if (countOfNumberInText(textTokens) >= 1 && textTokens.contains(Integer.toString(month))) {
                                            //results.get(p.getName()).put(a.getName(), 0.5);
                                            results.get(a.getName()).put(p.getName(), 0.5);
                                        }
                                        if (countOfNumberInText(textTokens) >= 2 && textTokens.contains(Integer.toString(year)) && textTokens.contains(Integer.toString(month))) {
                                            //results.get(p.getName()).put(a.getName(), 1.0);
                                            results.get(a.getName()).put(p.getName(), 1.0);
                                        }
                                        continue;
                                    }
                                    if (range.equals("gYear")) {
                                        int year = Integer.parseInt(value);
                                        if (countOfNumberInText(textTokens) >= 1 && textTokens.contains(Integer.toString(year))) {
                                            //results.get(p.getName()).put(a.getName(), 1.0);
                                            results.get(a.getName()).put(p.getName(), 1.0);
                                        }
                                        continue;
                                    }
                                }
                            }
                        }
                    } else {
                        for (Attribute a : attributes) {
                            StringKernel sk;
                            double k;
                            int ngramsLengthForString = 2;
                            double lambda = .2;
                            int tokenLimit = 50;
//                                    logger.error("VALUE: " +p.getValue() );
                            Tokenizer tokenizer = HardTokenizer.getInstance();
                            String[] tokenizedValue = tokenizer.stringArray(StringEscapeUtils.unescapeJava(p.getValue()).toLowerCase().replace("_", " "));

//                                    logger.error("tokenizedValue: " +Arrays.toString(tokenizedValue) );

                            String destValue = a.getValue();

                            destValue = destValue.replaceAll("&nbsp;", " ");
                            destValue = StringEscapeUtils.unescapeXml(destValue);
//                                    logger.trace("Text cleaned");
                            WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
                            // Parse page
                            ParsedPage parsedPage = wikiMarkupParser.parsePage(destValue);
                            if (parsedPage == null) {

                            }

                            ArrayList<String> textTokens = getTextFromWiki(parsedPage);
                            boolean tooManyTokens = false;
                            if (textTokens.size() > tokenLimit) {
                                tooManyTokens = true;
                            }

//                                    logger.error("textTokens: " +textTokens);
                            if (!tooManyTokens) {
                                for (int j = ngramsLengthForString; j > 0; j--) {
                                    sk = new StringKernel(lambda, j);
                                    k = sk.getNormalized(tokenizedValue, textTokens.toArray(new String[textTokens.size()]));
                                    if (k > 0) {
                                        logger.error("Added " + p.getName() + " " + a.getName() + " " + k);
                                        //results.get(p.getName()).put(a.getName(), k);
                                        results.get(a.getName()).put(p.getName(), k);
//                                    results.get(template).add(label, k);
                                        break;
                                    }
                                }
                            }

                            List<String> links = getLinksFromWiki(parsedPage);
                            logger.error(links.toString());
                            ArrayList<String> newTokens = new ArrayList<String>();
                            for (String link : links) {
                                logger.error("Searching translation for " + link);
                                HashMap<String, String> h3;
                                if(language.equals(Constants.ENGLISH)){
                                    h3  = Search1.getViInterLanguageLink(link);
                                }else if(language.equals(Constants.GERMANY)){
                                    h3 = Search1.getViInterLanguageLink_de(link);
                                }else{
                                    h3 = Search1.getViInterLanguageLink_nl(link);
                                }
                                String stringTranslation = h3.get(link);
                                if (stringTranslation == null || stringTranslation.equals("")) {
                                    logger.error("Value translation not found");
                                    continue;
                                }
                                logger.error("Translation found: " + stringTranslation);
                                String[] parts = stringTranslation.toLowerCase().split("_");
                                newTokens.addAll(Arrays.asList(parts));
                            }
                            if (newTokens.size() > 0 && newTokens.size() <= tokenLimit) {
                                for (int j = ngramsLengthForString; j > 0; j--) {
                                    sk = new StringKernel(lambda, j);
                                    k = sk.getNormalized(newTokens.toArray(new String[newTokens.size()]), tokenizedValue);
                                    if (k > 0) {
                                        logger.error("Added " + " " + p.getName() + " " + a.getName());
//                                        results.get(p.getName()).put(a.getName(), k);
                                        results.get(a.getName()).put(p.getName(), k);
//                                    results.get(template).add(label, k);
                                        break;
                                    }
                                }
                            }
                        }
                    }

                } else {
                    for (Attribute a : attributes) {
                        int ngramsLengthForString = 2;
                        double lambda = .2;
                        int tokenLimit = 50;
//                                    logger.error("VALUE: " +p.getValue() );
                        Tokenizer tokenizer = HardTokenizer.getInstance();
                        String[] tokenizedValue = tokenizer.stringArray(StringEscapeUtils.unescapeJava(p.getValue()).toLowerCase().replace("_", " "));

//                                    logger.error("tokenizedValue: " +Arrays.toString(tokenizedValue) );

                        String destValue = a.getValue();

                        destValue = destValue.replaceAll("&nbsp;", " ");
                        destValue = StringEscapeUtils.unescapeXml(destValue);
//                                    logger.trace("Text cleaned");
                        WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
                        // Parse page
                        ParsedPage parsedPage = wikiMarkupParser.parsePage(destValue);
                        if (parsedPage == null) {

                        }

                        ArrayList<String> textTokens = getTextFromWiki(parsedPage);
                        boolean tooManyTokens = false;
                        if (textTokens.size() > tokenLimit) {
                            tooManyTokens = true;
                        }
                        HashMap<String, String> h3;
                        if(language.equals(Constants.ENGLISH)){
                            h3 = Search1.getInterLanguageLink(p.getValue());
                        }else if(language.equals(Constants.GERMANY)){
                            h3 = Search1.getInterLanguageLink_de(p.getValue());
                        }else{
                            h3 = Search1.getInterLanguageLink_nl(p.getValue());
                        }
                        String valueTranslation = h3.get(p.getValue());
                        if (valueTranslation == null) {
                            valueTranslation = p.getValue();
                        }
                        logger.error("value trans: " + valueTranslation);

                        String[] translatedTokenizedValue = {};
                        if (valueTranslation != null) {
                            translatedTokenizedValue = tokenizer.stringArray(valueTranslation.toLowerCase().replace("_", " "));
                        }

                        List<String> links = getLinksFromWiki(parsedPage);
                        logger.error(links.toString());

                        if (valueTranslation != null && links.contains(valueTranslation)) {
                            logger.error("Added " + p.getName() + " --- " + a.getName() + " ---- " + links.toString() + " --- " + valueTranslation);
                            double t = 0.0;
//                            if(results.get(p.getName()).containsKey(a.getName())){
//                               t = (results.get(p.getName()).get(a.getName()) + 1.0 )/ links.size();
//                                results.get(p.getName()).remove(a.getName());
//                                results.get(p.getName()).put(a.getName(),t);
//                            }else{
//                            results.get(p.getName()).put(a.getName(), 1.0 / links.size());
                            results.get(a.getName()).put(p.getName(), 1.0 / links.size());
                            //}

//                            results.get(template).add(label, 1.0 / links.size());
                        } else {
                            if (!tooManyTokens) {
                                for (int j = ngramsLengthForString; j > 0; j--) {
                                    StringKernel sk = new StringKernel(lambda, j);
                                    double k;

                                    logger.trace("Using string kernel with: " + Arrays.toString(tokenizedValue));
                                    k = sk.getNormalized(tokenizedValue, textTokens.toArray(new String[textTokens.size()]));
                                    if (k > 0) {
                                        logger.error("Added " + p.getName() + " --- " + a.getName() + "---" + k);
                                        //results.get(p.getName()).put(a.getName(), k);
                                        results.get(a.getName()).put(p.getName(), k);
//                                        results.get(globalLabel).add(label, k);
                                        continue;
                                    }

                                    logger.trace("Using string kernel with translation: " + Arrays.toString(translatedTokenizedValue));
                                    k = sk.getNormalized(translatedTokenizedValue, textTokens.toArray(new String[textTokens.size()]));
                                    if (k > 0) {
                                        logger.error("Added " + p.getName() + " --- " + a.getName());
//                                        results.get(p.getName()).put(a.getName(), k);
                                        results.get(a.getName()).put(p.getName(), k);
//                                        results.get(globalLabel).add(label, k);
                                        continue;
                                    }
                                }
                            }
                        }


                    }
                }
            }

        } catch (Exception e) {
            System.out.println("error: " + e.getMessage() + "---" + Thread.currentThread().getStackTrace()[2].getLineNumber());
            e.getMessage();
        }

        HashMap<String, HashMap<String, Double>> resultAftermax = new HashMap<>();

        Set<String> keys = results.keySet();
        for (String key : keys) {
            HashMap<String, Double> h1 = results.get(key);
            logger.error("Result: " + key + " -- " + h1.toString());
            resultAftermax.put(key, new HashMap<String, Double>());
            Set<String> keys1 = h1.keySet();
            double max = 0.0;
            for (String a : keys1) {
                if (h1.get(a) > max) {
                    max = h1.get(a);
                }
            }

            for (String a : keys1) {
                if (h1.get(a) == max) {
                    resultAftermax.get(key).put(a, h1.get(a));
                }
            }
        }
        int index = 0;
        Set<String> keys1 = resultAftermax.keySet();
        //ArrayList<String> map = new ArrayList<>();
        HashMap<String, HashMap<String, Double>> map = new HashMap<>();

        for (String a : keys1) {
            HashMap<String, Double> hashMap = resultAftermax.get(a);
            if (hashMap.size() >= 2) {
                Set<String> k2 = hashMap.keySet();
                int count = 0;
                for (String b : k2) {
                    if (hashMap.get(b) == 1.0) {
                        count++;
                        if (count == 2) {
                            break;
                        }
                    }
                }
                if (count == 2) {
                    for (String b : k2) {
                        if (hashMap.get(b) == 1.0) {
                            if (!map.containsKey(a)) {
                                HashMap<String, Double> hashMap1 = new HashMap<>();
                                hashMap1.put(b, 1.0);
                                map.put(a, hashMap1);
                            } else {
                                map.get(a).put(b, 1.0);
                            }
                        }
                    }
                }
            }
        }

        HashMap<String, ArrayList<String>> listGGTranslateAttributeViToEn = new HashMap<>();
        HashMap<String, ArrayList<String>> listGGTranslatePropertyEnToVi = new HashMap<>();
        HashMap<String, String> listBingTranslateAttributeViToEn = new HashMap<>();
        HashMap<String, String> listBingTranslatePropertyEnToVi= new HashMap<>();

        try{
            SearchDBpediaAllDump searchDBpediaAllDump = new SearchDBpediaAllDump("ALL_DB_DUMP");
            ArrayList<String> propertieNames = searchDBpediaAllDump.getAllPropertyNameFromDBpediaByTitleArticle(nameTran);
            listGGTranslateAttributeViToEn = TranslateDemo.getListGoogleTranslateViToEn(attributes);
            listGGTranslatePropertyEnToVi = TranslateDemo.getListGoogleTranslateEnToVi(propertieNames);
            listBingTranslateAttributeViToEn = TranslateDemo.getBingTranslateViToEn(attributes);
            listBingTranslatePropertyEnToVi = TranslateDemo.getBingTranslateEnToVi(propertieNames);
        }catch (Exception e){
            logger.error(e.getMessage());
        }

        //tim nhung thuoc tinh cung anh xa vao nhieu property sau do translate hoac dung wordnet de so sanh

        Set<String> k1s = map.keySet();
        HashMap<String, String> extendsMap = new HashMap<>();
        ArrayList<String> listExtends = new ArrayList<>();
        try{
            for (String m : k1s) {
                //String[] split = m.split(Pattern.quote(";"));
                String a = modelString(m).trim();
                String a_remove_mark = TranslateDemo.removeDiacriticalMarks(a);
//                ArrayList<String> list_a_translate_gg_en = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickGoogle("vi", "en", a);
//                String a_translate_bing_en = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickBingApi("vi", "en", a).toLowerCase();
                ArrayList<String> list_a_translate_gg_en = listGGTranslateAttributeViToEn.get(a);
                String a_translate_bing_en = listBingTranslateAttributeViToEn.get(a);
                Set<String> k2s  = map.get(m).keySet();
                for(String n: k2s){
                    String b = TranslateDemo.splitString(n);
                    try {
//                        ArrayList<String> list_b_translate_gg_vi = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickGoogle("en", "vi", b);
//                        String b_translate_bing_vi = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickBingApi("en", "vi", b).toLowerCase();
                        ArrayList<String> list_b_translate_gg_vi = listGGTranslatePropertyEnToVi.get(n);
                        String b_translate_bing_vi = listBingTranslatePropertyEnToVi.get(n);
                        logger.error("Translate: " + a + "( gg: " + list_a_translate_gg_en.toString() + ") ( bing: " + a_translate_bing_en + " )" + " " + b);
                        logger.error("Translate: " + b + "( gg: " + list_b_translate_gg_vi.toString() + ") ( bing: " + b_translate_bing_vi + " )" + " " + a);
                        logger.error(a_translate_bing_en.equals(b) + " ----- " + list_a_translate_gg_en.contains(b));
                        logger.error(b_translate_bing_vi.equals(a) + " ----- " + list_b_translate_gg_vi.contains(a));
                        if (a_translate_bing_en.equals(b) || list_a_translate_gg_en.contains(b)||b_translate_bing_vi.equals(a) || list_b_translate_gg_vi.contains(a_remove_mark)) {
                            logger.error(m + " --- " + n);
                            resultAftermax.get(m).clear();
                            //logger.error(resultAftermax.get(a).size());
                            resultAftermax.get(m).put(n,1.0);
                            extendsMap.put(a,n);
                            listExtends.add(m);
                        }else {
                            ArrayList<String> listSynset;
                            listSynset = TestWordNet.getSynsets(b);
                            if(listSynset.size()>0){
                                for(String s: listSynset){
                                    s = s.toLowerCase();
                                    if(s.equals(a_translate_bing_en) || list_a_translate_gg_en.contains(s)){
                                        logger.error(m + " --- " + n);
                                        resultAftermax.get(m).clear();
                                        //logger.error(resultAftermax.get(a).size());
                                        resultAftermax.get(m).put(n,1.0);
                                        extendsMap.put(a,n);
                                        listExtends.add(m);
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.getMessage();
                    }

                }


            }
        }catch (Exception e){
            e.getMessage();
        }

        for(String m: listExtends){
            map.remove(m);
        }

        for(Attribute a: attributes){
            String m_a = modelString(a.getName());
            Set<String> k2s = extendsMap.keySet();
            for(String k: k2s){
                if(m_a.equalsIgnoreCase(k)){
                    resultAftermax.get(a.getName()).clear();
                    resultAftermax.get(a.getName()).put(extendsMap.get(k),1.0);
                }
            }
        }

        //so sanh nhan
        try{
            SearchDBpediaAllDump searchDBpediaAllDump = new SearchDBpediaAllDump("ALL_DB_DUMP");
            ArrayList<String> propertieNames = searchDBpediaAllDump.getAllPropertyNameFromDBpediaByTitleArticle(nameTran);
            logger.error("Translate a name");
            for(Attribute a: attributes){
                String a_name = modelString(a.getName()).trim();
                String a_name_remove_mark = TranslateDemo.removeDiacriticalMarks(a_name);
//                ArrayList<String> list_a_translate_gg_en = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickGoogle("vi", "en", a_name);
//                String a_translate_bing_en = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickBingApi("vi", "en", a_name).toLowerCase();
                ArrayList<String> list_a_translate_gg_en = listGGTranslateAttributeViToEn.get(a_name);
                String a_translate_bing_en = listBingTranslateAttributeViToEn.get(a_name);
                for(String p: propertieNames){
                    //translate a
                    String p_name = TranslateDemo.splitString(p);
                    try {
//                        ArrayList<String> list_b_translate_gg_vi = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickGoogle("en", "vi", p_name);
//                        String b_translate_bing_vi = com.vietnamesedbpedia.dbpedia.TranslateDemo.translateQuickBingApi("en", "vi", p_name).toLowerCase();
                        ArrayList<String> list_b_translate_gg_vi = listGGTranslatePropertyEnToVi.get(p);
                        String b_translate_bing_vi = listBingTranslatePropertyEnToVi.get(p);
                        logger.error("Translate: " + a_name + "( gg: " + list_a_translate_gg_en.toString() + ") ( bing: " + a_translate_bing_en + " )" + " " + p_name);
                        logger.error("Translate: " + p_name + "( gg: " + list_b_translate_gg_vi.toString() + ") ( bing: " + b_translate_bing_vi + " )" + " " + a_name);
                        logger.error(a_translate_bing_en.equals(p_name) + " ----- " + list_a_translate_gg_en.contains(p_name));
                        logger.error(b_translate_bing_vi.equals(a_name) + " ----- " + list_b_translate_gg_vi.contains(a_name_remove_mark));
                        if (a_translate_bing_en.equals(p_name) || list_a_translate_gg_en.contains(p_name)||b_translate_bing_vi.equals(a_name) || list_b_translate_gg_vi.contains(a_name_remove_mark)) {
                            logger.error(a.getName() + " --- " + p);
                            resultAftermax.get(a.getName()).clear();
                            //logger.error(resultAftermax.get(a).size());
                            resultAftermax.get(a.getName()).put(p,1.0);
                        }else {
                            ArrayList<String> listSynset;
                            listSynset = TestWordNet.getSynsets(p_name);
                            if(listSynset.size()>0){
                                for(String s: listSynset){
                                    s = s.toLowerCase();
                                    if(s.equals(a_translate_bing_en) || list_a_translate_gg_en.contains(s)){
                                        logger.error(a.getName() + " --- " + p);
                                        resultAftermax.get(a.getName()).clear();
                                        //logger.error(resultAftermax.get(a).size());
                                        resultAftermax.get(a.getName()).put(p,1.0);
                                        break;
                                    }
                                }
                            }
                        }

                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            }
        }catch (Exception e){
            e.getMessage();
        }

        keys1 = resultAftermax.keySet();
        HashMap<String, HashMap<String, Integer>> countMap = new HashMap<>();
        for(String a: keys1) {
            String a_model = modelString(a);
            if (!countMap.containsKey(a_model)) {
                countMap.put(a_model, new HashMap<String, Integer>());
            }
        }
        // map chua các nhập nhằng
        ArrayList<String> listModel = new ArrayList<>();

        Set<String> ps = map.keySet();
        for(String p: ps){
            String p_model = modelString(p);
            listModel.add(p_model);
            HashMap<String, Double> h2s = map.get(p);
            Set<String> k2s = h2s.keySet();
            HashMap<String, Integer> h3s = countMap.get(p_model);
            for(String a: k2s){
                int count = 0;
                if(h3s.containsKey(a)){
                    count = h3s.get(a);
                }
                count++;
                h3s.put(a, count);
            }
        }

        Set<String> k4s = map.keySet();

        for(String a: keys1){
            if(!k4s.contains(a)){
                String a_model = modelString(a);
                if(listModel.contains(a_model)){
                    Set<String> k2s = countMap.keySet();
                    for(String b: k2s){
                        if(a_model.equals(b)){
                            HashMap<String, Double> h2s = resultAftermax.get(a);
                            Set<String> k2ss = h2s.keySet();
                            HashMap<String, Integer> h3s = countMap.get(b);
                            for(String c: k2ss){
                                int count = 0;
                                if(h3s.containsKey(c)){
                                    count = h3s.get(c);
                                }
                                count++;
                                h3s.put(c, count);
                            }
                        }
                    }
                }
            }
        }

        //using voting rule
        Set<String> keyCountMaps = countMap.keySet();
        HashMap<String, String> resultCount = new HashMap<>();
        for(String a: keyCountMaps){
            HashMap<String, Integer> hashMap = countMap.get(a);
            Collection<Integer> counts = hashMap.values();
            //HashMap<String, Integer> newHashMap = new HashMap<>();
            if(counts!=null){
                if(counts.size()>0){
                    Integer max = Collections.max(counts);
                    Set<String> k2s = hashMap.keySet();
                    for(String k: k2s){
                        if(hashMap.get(k).equals(max)){
                            //newHashMap.put(k, max);
                            resultCount.put(a, k);
                        }
                    }
                }
            }
        }

        //update
        keyCountMaps = resultCount.keySet();
        for(String b: keyCountMaps){
            for(String a: keys1){
                String a_model = modelString(a);
                if(a_model.equals(b)){
                    HashMap<String, Double> hashMap = resultAftermax.get(a);
                    hashMap.clear();
                    hashMap.put(resultCount.get(b),1.0 );

                }
            }
        }


        try {
            if(language.equals(Constants.ENGLISH)){
                name = name +"_en";
            }else if(language.equals(Constants.GERMANY)){
                name = name + "_de";
            }else{
                 name = name + "_nl";
            }
            WritableWorkbook workbook = Workbook.createWorkbook(new File("template_statistic" + File.separator + "infobox-Thong_tin_don_vi_hanh_chinh-result" + File.separator + name + ".xls"));
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

            for (String k : keys1) {
                HashMap<String, Double> h1 = resultAftermax.get(k);
                Set<String> k1 = h1.keySet();
                for (String c : k1) {
                    sheet.addCell(new Label(0, index, k));
                    sheet.addCell(new Label(1, index, c));
                    sheet.addCell(new Label(2, index, String.valueOf(h1.get(c))));
                    index++;
                }
            }

            workbook.write();
            workbook.close();
        } catch (Exception e) {
            e.getMessage();
        }

        System.out.println("end");
    }

    public static String modelString(String input) {
        StringBuilder output = new StringBuilder();
        if (input.contains("_")) {
            input = input.replace("_", " ");
        }
        char[] listChar = input.trim().toCharArray();
        for (char a : listChar) {
            if (!NumberUtils.isNumber(String.valueOf(a))) {
                output.append(a);
            }
        }
        return output.toString().trim();
    }


    private int countOfNumberInText(List<String> textTokens) {
        int size = textTokens.size();
        int count = 0;
        for (String a : textTokens) {
            if (StringUtils.isNumeric(a)) {
                count++;
            }
        }
        return count;
    }

    public static List<String> getLinksFromWiki(ParsedPage parsedPage) {
        List<String> ret = new ArrayList<String>();
        List<Link> links = parsedPage.getLinks();
        for (Link link : links) {
            if (link.getType() == Link.type.INTERNAL) {
                ret.add(link.getTarget());
            }
        }
        return ret;
    }

    public static ArrayList<Attribute> getDataAttribute(String nameFile) {

        ArrayList<Attribute> attributes = new ArrayList<>();
        try {
            File file = new File(nameFile);
            BufferedReader bufferedReader = new BufferedReader(new FileReader(file));

            String line;
            Attribute attribute = null;
            boolean isPart = false;

            while ((line = bufferedReader.readLine()) != null) {
                line = line.trim();
                if (line.charAt(0) == '|') {
                    isPart = true;
                    String a[] = line.trim().split(Pattern.quote("="));
                    attribute = new Attribute();
                    if (a.length > 1) {
                        String value = a[1].trim();
                        attribute.setName(a[0].replace("|", "").trim());
                        attribute.setValue(value);
                        attributes.add(attribute);
                    }
                } else if (isPart) {
                    String value = attribute.getValue();
                    value += line;
                    attribute.setValue(value);
                }
            }

            bufferedReader.close();
        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return attributes;
    }
}
