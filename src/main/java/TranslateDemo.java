import info.bliki.wiki.template.URLEncode;
import jxl.Sheet;
import jxl.Workbook;
import model.Attribute;
import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.util.StringKernel;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vieta on 14/3/2017.
 */
public class TranslateDemo {

    private static final String TRANSLATE_GOOGLE = "https://translate.googleapis.com/translate_a/single?client=gtx&dt=t&dt=bd&dj=1&dt=ex&dt=ld&dt=md&dt=qca&dt=rw&dt=rm&dt=ss&dt=at";
    //private static final String TRANSLATE_BING = "http://api.microsofttranslator.com/V2/Ajax.svc/GetTranslations?";

    //private static String BING_APP_ID = "TNMTZQhyVZzz9q7Anqxy2LLfbNM-Z1Ri5NajOgBZnQsU%2A";
    public static String USER_AGENT = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36";

    public static ArrayList<String> translateQuickGoogle(String original,
                                                         String destination, String input) throws IOException, JSONException {
        String LANGUAGE_TARGET = "&tl=";
        String LANGUAGE_SOURCE = "&sl=";
        String QUERY = "&q=";
        String query = URLEncoder.encode(input, "utf-8");
        String translate = "";
        LANGUAGE_SOURCE += original;
        LANGUAGE_TARGET += destination;

        QUERY += query;

        String url = TRANSLATE_GOOGLE + LANGUAGE_TARGET + LANGUAGE_SOURCE
                + QUERY;

        String data = getUrl(url);

        JSONObject json = new JSONObject(data);

        ArrayList<String> translates = new ArrayList<>();
        if (json.has("dict")) {
            JSONArray arr1 = json.getJSONArray("dict");
            JSONObject ori1 = arr1.getJSONObject(0);
            JSONArray terms = ori1.getJSONArray("terms");
            //System.out.println(removeDiacriticalMarks(new String(terms.getString(9).getBytes("ISO-8859-1"), "utf-8")) +new String(terms.getString(0).getBytes("ISO-8859-1"), "utf-8").length() );
            int size = terms.length();

            for (int i = 0; i < size; i++) {
                translates.add(removeDiacriticalMarks(new String(terms.getString(i).getBytes("ISO-8859-1"), "utf-8")));
            }
        } else {
            JSONArray arr = json.getJSONArray("sentences");

            int lenght = arr.length();
            if (lenght > 0) {

                JSONObject ori;
                for (int i = 0; i < lenght; i++) {
                    ori = arr.getJSONObject(i);
                    try {
                        translate += new String(ori.getString("trans").getBytes("ISO-8859-1"), "utf-8");
                    } catch (Exception e) {
                        // update key bing and retranslate
                    }
                }
            }
            translates.add(removeDiacriticalMarks(translate.toLowerCase()));
        }

        return translates;
    }

    public static String removeDiacriticalMarks(String string) {
        return Normalizer.normalize(string, Normalizer.Form.NFD)
                .replaceAll("\\p{InCombiningDiacriticalMarks}+", "").toLowerCase();
    }

    public static String translateQuickBingApi(String from, String to, String text) {
        String a = "";
        String authBaseUrl = "http://api.microsofttranslator.com/V2/Ajax.svc/Translate?appId=68D088969D79A8B23AF8585CC83EBA2A05A97651&from=" + from + "&to=" + to + "&text=" + URLEncoder.encode(text);
        try {
            URL url = new URL(authBaseUrl);

            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            InputStream is = httpURLConnection.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(is);
            BufferedReader reader = new BufferedReader(inputStreamReader, 8);
            String line;

            while ((line = reader.readLine()) != null) {
                a += line;
            }


            int size = a.length();
            a = a.substring(2, size - 1);
            String respone = httpURLConnection.getResponseMessage();


        } catch (Exception e) {
            e.getMessage();
        }
        return a;
    }


    public static String getUrl(String urlQuery) throws IOException {
        HttpURLConnection connection = null;
        BufferedReader reader;
        try {
            URL url = new URL(urlQuery);
            connection = (HttpURLConnection) url.openConnection();
            connection
                    .setRequestProperty("User-Agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_10_1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/42.0.2311.152 Safari/537.36");
            InputStream in = connection.getInputStream();
            InputStreamReader streamReader = new InputStreamReader(in, "iso-8859-1");
            reader = new BufferedReader(streamReader, 8);
            StringBuilder stringBuilder = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                stringBuilder.append(line + "\n");
            }
            return stringBuilder.toString();
        } catch (Exception e) {
            if (connection != null)
                connection.disconnect();

            return "";
        }

    }

    static HashMap<String, String> propertyhashMap = new HashMap<>();

    private static void parseSourcePropertyFromServer() {
        final Logger logger = Logger.getLogger(TranslateDemo.class);
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Document document = Jsoup.connect("http://mappings.dbpedia.org/server/ontology/classes/Settlement")
                            .header("Accept-Encoding", "gzip, deflate")
                            .userAgent("Mozilla/5.0 (Windows NT 6.1; WOW64; rv:23.0) Gecko/20100101 Firefox/23.0")
                            .maxBodySize(0)
                            .timeout(600000)
                            .get();

                    Elements elements = document.select("tr");
                    for (Element e : elements) {
                        Elements elements1 = e.select("td");
                        if (elements1.size() >= 4) {
                            String propertyName = elements1.get(0).text();
                            if (propertyName.contains("(edit)")) {
                                propertyhashMap.put(propertyName, splitString(propertyName.replace("(edit)", "").trim()));
                            }
                        }
                    }
                    logger.debug("end");
                } catch (Exception e) {
                    logger.error(e.getMessage());
                }
            }
        }).start();
    }

    static HashMap<String, String> nameAttrs = new HashMap<>();

    public static String splitString(String a) {
        String newString = "";
        char[] list = a.toCharArray();
        ArrayList<Integer> listIndex = new ArrayList<>();
        listIndex.add(0);
        int index = 0;
        for (char b : list) {
            if (Character.isUpperCase(b)) {
                listIndex.add(index);
            }
            index++;
        }
        listIndex.add(a.length());
        int size = listIndex.size();

        for (int i = 1; i < size; i++) {
            newString += " " + a.substring(listIndex.get(i - 1), listIndex.get(i)).toLowerCase();
        }

        System.out.println(newString.trim());
        return newString.trim();
    }

    public static void main(String[] args) {
//        Logger logger = Logger.getLogger(TranslateDemo.class);
//        TranslateDemo.parseSourcePropertyFromServer();
//        String namefile = "Thông_tin_đơn_vị_hành_chính_Việt_Nam";
//        try {
//            File f = new File("template_statistic", namefile+".xls");
//            Workbook w = Workbook.getWorkbook(f);
//            Sheet sheet = w.getSheet(0);
//
//
//
//            for (int i = 0; i < sheet.getRows(); i++) {
//                String name = sheet.getCell(0,i).getContents();
//                String trans = translateQuickGoogle(name,"vi","en").toLowerCase().trim();
//                nameAttrs.put(name, trans);
//            }
//            logger.debug(nameAttrs.size());
//
//            w.close();
//        } catch (Exception e) {
//            e.getMessage();
//        }
//        //compare using string kernel
//        HashMap<String, HashMap<String,Double>> mapping = new HashMap<>();
//        StringKernel sk;
//        double k;
//        int ngramsLengthForString = 2;
//        double lambda = .2;
//        Set<String> attrkeys = nameAttrs.keySet();
//        Set<String> prokeys = propertyhashMap.keySet();
//        for(String a: attrkeys){
//            for(String b: prokeys){
//                for (int j = ngramsLengthForString; j > 0; j--) {
//                    sk = new StringKernel(lambda, j);
//                    String s = nameAttrs.get(a);
//                    String t = propertyhashMap.get(b);
//                    double k0 = sk.get(s, t);
//                    double k1 = sk.get(s, s);
//                    double k2 = sk.get(t, t);
//                    k = k0 / Math.sqrt(k1 * k2);
//                    if (k > 0) {
//                        if(!mapping.containsKey(a)){
//                            HashMap<String, Double> hashMap = new HashMap<>();
//                            hashMap.put(b, k);
//                            mapping.put(a, hashMap);
//                        }else{
//                            mapping.get(a).put(b,k);
//                        }
//                        logger.debug(a + " --- " + b + " ---- " + k);
//                        break;
//                    }
//                }
//            }
//        }
        //logger.debug("end");
        try {
            for(int i=0; i<1000; i++){
                Thread.sleep(1000);
                String a = translateQuickBingApi("vi","en","bắt đầu");
//
                System.out.println(a);
            }
            //ArrayList<String> b = translateQuickGoogle("en", "vi", "office");

//            System.out.println(g.equals("successor"));
        } catch (Exception e) {
            e.getMessage();
        }
//        new TranslateDemo().splitString("demographicsAsOf");
    }

    public static HashMap<String, ArrayList<String>>  getListGoogleTranslateViToEn(ArrayList<Attribute> attributes){
        HashMap<String, ArrayList<String>> listGGTranslate = new HashMap<>();

        try{
            for(Attribute a: attributes){
                String w = a.getName();
                String w_model = Test.modelString(w);
                if(!listGGTranslate.containsKey(w_model)){
                    ArrayList<String> list_w_gg_translate = translateQuickGoogle("vi","en",w_model);
                    listGGTranslate.put(w_model,list_w_gg_translate);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return listGGTranslate;
    }

    public static HashMap<String, ArrayList<String>>  getListGoogleTranslateEnToVi(ArrayList<String> properties){
        HashMap<String, ArrayList<String>> listGGTranslate = new HashMap<>();

        try{
            for(String w: properties){
                String w_model = splitString(w);
                if(!listGGTranslate.containsKey(w)){
                    ArrayList<String> list_w_gg_translate = translateQuickGoogle("en","vi",w_model);
                    listGGTranslate.put(w,list_w_gg_translate);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
        return listGGTranslate;
    }

    public static HashMap<String, String> getBingTranslateViToEn(ArrayList<Attribute> attributes){
        HashMap<String, String> translateList = new HashMap<>();

        try{
            for(Attribute a: attributes){
                String w = a.getName();
                String w_model = Test.modelString(w);
                if(!translateList.containsKey(w_model)){
                    String w_bing_translate = translateQuickBingApi("vi","en",w_model);
                    translateList.put(w_model,w_bing_translate);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return translateList;
    }

    public static HashMap<String, String> getBingTranslateEnToVi(ArrayList<String> properties){
        HashMap<String, String> translateList = new HashMap<>();

        try{
            for(String w: properties){
                String w_model = splitString(w);
                if(!translateList.containsKey(w)){
                    String w_bing_translate = translateQuickBingApi("en","vi",w_model);
                    translateList.put(w,w_bing_translate);
                }
            }
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

        return translateList;
    }

}
