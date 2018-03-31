package com.vietnamesedbpedia.program;

import com.vietnamesedbpedia.dbpedia.GetClassDBpedia;
import de.tudarmstadt.ukp.wikipedia.parser.Content;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.fbk.cit.hlt.thewikimachine.analysis.HardTokenizer;
import org.fbk.cit.hlt.thewikimachine.analysis.Tokenizer;
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;
import org.fbk.cit.hlt.thewikimachine.util.FrequencyHashSet;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiMarkupParser;

import java.io.File;
import java.net.URLDecoder;
import java.util.*;
import java.util.regex.Pattern;

/**
 * Created by vietanknb on 25/4/2017.
 */
public class DbpediaBox {
    private VBox contentBox;
    private static WebView webView;
    private HBox header = new HBox();
    private Image next = new Image(new File("image/next.png").toURI().toString());
    private Image nextHover = new Image(new File("image/next_hover.png").toURI().toString());
    private Image back = new Image(new File("image/back.png").toURI().toString());
    private Image backHover = new Image(new File("image/back_hover.png").toURI().toString());
    private Image reload = new Image(new File("image/redo.png").toURI().toString());
    private Image reloadHover = new Image(new File("image/redo_hover.png").toURI().toString());
    private ImageView nextImage = new ImageView();
    private ImageView backImage = new ImageView();
    private ImageView reloadImage = new ImageView();
    private static TextField urlWeb = new TextField();

    private static String foot = "</table>\n" +
            "    </div>\n" +
            "<!-- row  -->\n" +
            "</div>\n" +
            "</div>\n" +
            "<!-- footer -->\n" +
            "<div class=\"footer\">\n" +
            "    <div class=\"container\">\n" +
            "\t<a href=\"http://virtuoso.openlinksw.com\" title=\"OpenLink Virtuoso\"><img class=\"powered_by\" src=\""+new File("dbpediapage/statics/images/virt_power_no_border.png").toURI().toString()+"\" alt=\"Powered by OpenLink Virtuoso\"/></a>&nbsp; &nbsp;\n" +
            "\t<a href=\"http://linkeddata.org/\"><img alt=\"This material is Open Knowledge\" src=\""+new File("dbpediapage/statics/images/LoDLogo.gif").toURI().toString()+"\"/></a> &nbsp; &nbsp;\n" +
            "\t<a href=\"http://com.vietnamesedbpedia.dbpedia.org/sparql\"><img alt=\"W3C Semantic Web Technology\" src=\""+new File("dbpediapage/statics/images/sw-sparql-blue.png").toURI().toString()+"\"/></a> &nbsp;  &nbsp;\n" +
            "\t<a href=\"http://www.opendefinition.org/\"><img alt=\"This material is Open Knowledge\" src=\""+new File("dbpediapage/statics/images/od_80x15_red_green.png").toURI().toString()+"\"/></a>&nbsp; &nbsp;\n" +
            "\t<span style=\"display:none;\" about=\"\"\n" +
            "\t    resource=\"http://www.w3.org/TR/rdfa-syntax\"\n" +
            "\t    rel=\"dc:conformsTo\" xmlns:dc=\"http://purl.org/dc/terms/\">\n" +
            "\t    <a href=\"http://validator.w3.org/check?uri=referer\">\n" +
            "\t\t<img src=\"http://www.w3.org/Icons/valid-xhtml-rdfa\" alt=\"Valid XHTML + RDFa\" height=\"27px\" />\n" +
            "\t    </a>\n" +
            "\t</span>\n" +
            "\t<br />\n" +
            "\t<div class=\"text-muted\">\n" +
            "\t    This content was extracted from <a href=\"http://en.wikipedia.org/wiki/Hanoi\">Wikipedia</a> and is licensed under the <a href=\"http://creativecommons.org/licenses/by-sa/3.0/\">Creative Commons Attribution-ShareAlike 3.0 Unported License</a>\n" +
            "\n" +
            "\t</div>\n" +
            "    </div>\n" +
            "</div>\n" +
            "<!-- #footer -->\n" +
            "<!--#content-->\n" +
            "    <!--script type=\"text/javascript\">\n" +
            "     Place any Javascript code e.g. Google Analytics scripts\n" +
            "    </script-->\n" +
            "    \n" +
            "    <script type=\"text/javascript\" src=\""+new File("dbpediapage/statics/js/jquery.min.js").toURI().toString()+"\"></script>\n" +
            "    <script type=\"text/javascript\" src=\""+new File("dbpediapage/statics/js/bootstrap.min.js").toURI().toString()+"\"></script>\n" +
            "    <script type=\"text/javascript\" src=\""+new File("dbpediapage/statics/js/com.vietnamesedbpedia.dbpedia.js").toURI().toString()+"\"></script>\n" +
            "</body>\n" +
            "</html>\n";
    private static String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
            "    xmlns:dbpprop=\"http://com.vietnamesedbpedia.dbpedia.org/property/\"\n" +
            "    xmlns:foaf=\"http://xmlns.com/foaf/0.1/\"\n" +
            "    version=\"XHTML+RDFa 1.0\"\n" +
            "    xml:lang=\"en\"\n" +
            ">\n" +
            "\n" +
            "\n" +
            "<!-- header -->\n" +
            "<head profile=\"http://www.w3.org/1999/xhtml/vocab\">\n" +
            "    <meta charset=\"utf-8\" />\n" +
            "    <meta http-equiv=\"X-UA-Compatible\" content=\"IE=edge\" />\n" +
            "    <meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\" />\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\""+new File("dbpediapage/statics/css/bootstrap.min.css").toURI().toString()+"\" />\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\""+new File("dbpediapage/statics/css/com.vietnamesedbpedia.dbpedia.css").toURI().toString()+"\" />";

    public static String infoboxName;

    public DbpediaBox(VBox contentBox){
        this.contentBox = contentBox;
    }

    public void createDbpediaBox(){
        header.setSpacing(10);
        header.setPadding(new Insets(10, 10, 10, 10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #EEEEEE;");

        HBox.setHgrow(urlWeb, Priority.ALWAYS);
        nextImage.setFitWidth(16);
        nextImage.setFitHeight(16);
        nextImage.setImage(next);

        backImage.setFitWidth(16);
        backImage.setFitHeight(16);
        backImage.setImage(back);

        reloadImage.setFitWidth(16);
        reloadImage.setFitHeight(16);
        reloadImage.setImage(reload);

        header.getChildren().setAll(backImage, nextImage, reloadImage, urlWeb);

        webView = new WebView();

        setAction();
    }

    public WebView getWebView(){
        return webView;
    }

    private void setAction(){
        MouseEntered mouseEntered = new MouseEntered();
        reloadImage.setOnMouseEntered(mouseEntered);
        nextImage.setOnMouseEntered(mouseEntered);
        backImage.setOnMouseEntered(mouseEntered);

        MouseExited mouseExited = new MouseExited();
        reloadImage.setOnMouseExited(mouseExited);
        nextImage.setOnMouseExited(mouseExited);
        backImage.setOnMouseExited(mouseExited);

        MouseClicked mouseClicked = new MouseClicked();
        reloadImage.setOnMouseClicked(mouseClicked);
        nextImage.setOnMouseClicked(mouseClicked);
        backImage.setOnMouseClicked(mouseClicked);

    }

    private class MouseEntered implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if(object.equals(reloadImage)){
                reloadImage.setImage(reloadHover);
            }else if(object.equals(nextImage)){
                nextImage.setImage(nextHover);
            }else if(object.equals(backImage)){
                backImage.setImage(backHover);
            }
        }
    }

    private class MouseExited implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if(object.equals(reloadImage)){
                reloadImage.setImage(reload);
            }else if(object.equals(nextImage)){
                nextImage.setImage(next);
            }else if(object.equals(backImage)){
                backImage.setImage(back);
            }
        }
    }

    private class MouseClicked implements EventHandler<MouseEvent>{
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            //progressIndicator.setVisible(true);
            //title = textField.getText();
            if(object.equals(reloadImage)){
//                webEngine.reload();
            }else if(object.equals(nextImage)){
//                webEngine.load("https://vi.wikipedia.org/wiki/"+ URLEncoder.encode(title));
//                InfoboxBox.isLoad = false;
            }else if(object.equals(backImage)){

            }
        }
    }

    public static class DBpediaPage extends Thread{
        @Override
        public void run() {
            super.run();
            HashMap<String, String> mapping = new HashMap<>();
            File file = new File("mapping_vi",infoboxName.replace(" ","_")+"_result.xls");
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");

            String owlFile = "dbpedia_2016-04.owl";
            AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);

            try{
                Workbook w = Workbook.getWorkbook(file,ws);
                Sheet sheet = w.getSheet(0);
                for (int i = 0; i < sheet.getRows(); i++) {
                    String pttr = sheet.getCell(2,i).getContents();
                    if(!pttr.equals("")){
                        String attr = sheet.getCell(0,i).getContents();
                        mapping.put(attr, pttr);
                    }

                }

                Set<String> keys = InfoboxBox.infoboxAtrribute.keySet();
                String value;
                String pttr;

                WebEngine webEngine = webView.getEngine();
                StringBuilder stringBuilder = new StringBuilder();

                urlWeb.setText("vi.com.vietnamesedbpedia.dbpedia.org/page/"+WikiBox.title);
                stringBuilder.append(head);
                stringBuilder.append("<title>Thông tin về:"+WikiBox.title+"</title>");
                stringBuilder.append("</head>\n" +
                        "\n" +
                        "<div class=\"navbar navbar-default navbar-fixed-top\">\n" +
                        "    <div class=\"container\">\n" +
                        "\t<div class=\"navbar-header\">\n" +
                        "\t    <button type=\"button\" class=\"navbar-toggle collapsed\" data-toggle=\"collapse\" data-target=\"#dbp-navbar\" aria-expanded=\"false\">\n" +
                        "\t\t<span class=\"sr-only\">Toggle navigation</span>\n" +
                        "\t\t<span class=\"icon-bar\"></span>\n" +
                        "\t\t<span class=\"icon-bar\"></span>\n" +
                        "\t\t<span class=\"icon-bar\"></span>\n" +
                        "\t    </button>\n" +
                        "\t    <a class=\"navbar-brand\" href=\"http://wiki.com.vietnamesedbpedia.dbpedia.org/about\" title=\"About DBpedia\" style=\"color: #2c5078\">\n" +
                        "\t    <img class=\"img-responsive\" src=\""+new File("dbpediapage/statics/images/dbpedia_logo_land_120.png").toURI().toString()+"\" alt=\"About DBpedia\" style=\"display: inline-block;  margin-top: -12px\"/>\n" +
                        "\t    </a>\n" +
                        "\t</div>\n" +
                        "\n" +
                        "\t\n" +
                        "    </div>\n" +
                        "</div>\n" +
                        "\n" +
                        "<div class=\"container\">\n" +
                        "<!-- page-header -->\n" +
                        "    <div class=\"page-header\">");
                stringBuilder.append("<h1 id=\"title\">About:\n" +
                        "\t    <a href=\"http://com.vietnamesedbpedia.dbpedia.org/resource/Hanoi\">"+WikiBox.title+"</a>\n" +
                        "\t</h1>");

                stringBuilder.append("<div class=\"page-resource-uri\">\n" +
                        "\t    An Entity of Type : <a href=\"http://com.vietnamesedbpedia.dbpedia.org/resource/Municipalities_of_Vietnam\">MunicipalitÃ©s du ViÃªt Nam</a>,\n" +
                        "\t    from Named Graph : <a href=\"http://vi.com.vietnamesedbpedia.dbpedia.org\">http://vi.com.vietnamesedbpedia.dbpedia.org</a>,\n" +
                        "\t    within Data Space : <a href=\"http://vi.com.vietnamesedbpedia.dbpedia.org\">vi.com.vietnamesedbpedia.dbpedia.org</a>\n" +
                        "\t</div>\n" +
                        "    </div>\n" +
                        "<!-- page-header -->\n" +
                        "<!-- row -->\n" +
                        "    <div class=\"row\">\n" +
                        "\t<div class=\"col-xs-12\">\n" +
                        "\t    <!-- proptable -->\n" +
                        "\t    <table class=\"description table table-striped\">\n" +
                        "\t    <tr>\n" +
                        "\t\t<th class=\"col-xs-3\">Property</th>\n" +
                        "\t\t<th class=\"col-xs-9\">Value</th>\n" +
                        "\t    </tr>");

                for(String k: keys){
                    if(mapping.containsKey(k.toLowerCase())){
                        value = InfoboxBox.infoboxAtrribute.get(k);
                        pttr = mapping.get(k.toLowerCase());
//                        System.out.println(pttr +"    " +value);
                        WikiMarkupParser wikiMarkupParser = WikiMarkupParser.getInstance();
                        // Parse page
                        ParsedPage parsedPage = wikiMarkupParser.parsePage(value);

                        HashMap<String, String> h1 = airpediaOntology.getProperty(pttr);
                        ArrayList<HashSet> ret = getNumbersFromTokens(getTextFromWiki(parsedPage));
                        HashSet<Long> v_long = (HashSet<Long>) ret.get(0);
                        HashSet<Double> v_double = (HashSet<Double>) ret.get(1);
                        List<String> linkList = getLinksFromWiki(parsedPage);
                        int size = linkList.size();

                        if (h1 != null) {
//                            System.out.println(pttr + " range: "+ h1.get("range"));
                            String range = h1.get("range");
                            if(range!=null){
                                if(range.equals("double") ||
                                        range.equals("float") ||
                                        range.equals("nonNegativeInteger") ||
                                        range.equals("positiveInteger") ||
                                        range.equals("integer")){

                                    if(!range.equals("double")){
                                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+pttr+"\"><small>dbo:</small>"+pttr+"</a>\n" +
                                                "</td><td><ul>\n" +
                                                "\t<li><span class=\"literal\"><span property=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\">"+v_long+"</span><small> (xsd:"+range+")</small></span></li>\n" +
                                                "</ul></td></tr>");
                                    }else{
                                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+pttr+"\"><small>dbo:</small>"+pttr+"</a>\n" +
                                                "</td><td><ul>\n" +
                                                "\t<li><span class=\"literal\"><span property=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\">"+v_double+"</span><small> (xsd:"+range+")</small></span></li>\n" +
                                                "</ul></td></tr>");
                                    }


                                }else if(range.equals("date")|| range.equals("gYear") || range.equals("gYearMonth")){
                                    stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+pttr+"\"><small>dbo:</small>"+pttr+"</a>\n" +
                                            "</td><td><ul>\n" +
                                            "\t<li><span class=\"literal\"><span property=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\">"+v_long+"</span><small> (xsd:"+range+")</small></span></li>\n" +
                                            "</ul></td></tr>");
                                }else{
//                                    System.out.println("String: "+pttr);

                                    stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+pttr+"\"><small>dbo:</small>"+pttr+"</a>\n" +
                                            "</td><td><ul>\n");
                                    if(size>0){
                                        for(int i=0; i<size; i++){
                                            stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\" href=\"http://com.vietnamesedbpedia.dbpedia.org/resource/"+linkList.get(i)+"\"><small>dbr</small>:"+ linkList.get(i)+"</a></span></li>");
                                        }
                                    }

                                    for(Long numLong: v_long){
                                        stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\">"+numLong+"</span></span></li>\n");
                                    }

                                    stringBuilder.append("</ul></td></tr>");
//                                    System.out.println(value);
                                }
                            }else{
                                stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+pttr+"\"><small>dbo:</small>"+pttr+"</a>\n" +
                                        "</td><td><ul>\n");
                                if(size>0){
                                    for(int i=0; i<size; i++){
                                        stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\" href=\"http://com.vietnamesedbpedia.dbpedia.org/resource/"+linkList.get(i)+"\"><small>dbr</small>:"+ linkList.get(i)+"</a></span></li>");
                                    }
                                }

                                for(Long numLong: v_long){
                                    stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:"+pttr+"\" xmlns:dbo=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/\">"+numLong+"</span></span></li>\n");
                                }

                                stringBuilder.append("</ul></td></tr>");
                            }
                        }else{
                            System.out.println("NULL :" + pttr);
                            if(pttr.equals("foaf:name")){
                                stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://xmlns.com/foaf/0.1/name\"><small>foaf:</small>name</a>\n" +
                                        "</td><td><ul>\n" +
                                        "\t<li><span class=\"literal\"><span property=\"foaf:name\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xml:lang=\"vi\">"+value+"</span><small> (vi)</small></span></li></ul></td></tr>");
                            }else if (pttr.equals("foaf:homepage")){
                                stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://xmlns.com/foaf/0.1/name\"><small>foaf:</small>homepage</a>\n" +
                                        "</td><td><ul>\n" +
                                        "\t<li><span class=\"literal\"><span property=\"foaf:homepage\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xml:lang=\"vi\"><a>"+value+"</a></span><small> (vi)</small></span></li></ul></td></tr>");
                            }else if(pttr.equals("latd")){
                                stringBuilder.append("/tr><tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/2003/01/geo/wgs84_pos#lat\"><small>geo:</small>lat</a>\n" +
                                        "</td><td><ul>\n" +
                                        "\t<li><span class=\"literal\"><span property=\"geo:lat\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\">"+v_double+"</span><small> (xsd:float)</small></span></li>\n" +
                                        "</ul></td></tr>");

                            }else if(pttr.equals("longd")){
                                stringBuilder.append("/tr><tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/2003/01/geo/wgs84_pos#lat\"><small>geo:</small>long</a>\n" +
                                        "</td><td><ul>\n" +
                                        "\t<li><span class=\"literal\"><span property=\"geo:lat\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\">"+v_double+"</span><small> (xsd:float)</small></span></li>\n" +
                                        "</ul></td></tr>");

                            }

                        }
                        if (parsedPage != null) {
//                            System.out.println(getLinksFromWiki(parsedPage));
//                            System.out.println(getNumbersFromTokens(getTextFromWiki(parsedPage)));
                        }

                    }
                }

                ArrayList<String> list = GetClassDBpedia.getDBpediaClassFromTitle(WikiBox.title);
//                System.out.println(list.toString());

                stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"><small>rdf:</small>type</a>\n" +
                        "</td><td><ul>");
                stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"rdf:type\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" href=\"http://www.w3.org/2002/07/owl#Thing\"><small>owl</small>:Thing</a></span></li>");
                for(String a: list){
                    stringBuilder.append("\t<li><span class=\"literal\"><a class=\"uri\" rel=\"rdf:type\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" href=\"http://com.vietnamesedbpedia.dbpedia.org/ontology/"+a+"\"><small>dbo</small>:"+a+"</a></span></li>");
                }
                stringBuilder.append("</ul></td></tr>");
                stringBuilder.append(foot);
                webEngine.loadContent(stringBuilder.toString());
                webEngine.setJavaScriptEnabled(true);
                webEngine.locationProperty().addListener(new WebViewListener());

            }catch (Exception e){
                e.getMessage();
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


    public HBox getHeader(){
        return header;
    }

    private static class WebViewListener implements ChangeListener<String>{

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            urlWeb.setText(URLDecoder.decode(newValue));
        }
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

}
