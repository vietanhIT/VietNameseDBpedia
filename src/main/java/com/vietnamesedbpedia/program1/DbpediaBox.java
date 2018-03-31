package com.vietnamesedbpedia.program1;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXDialog;
import com.jfoenix.controls.JFXDialogLayout;
import com.vietnamesedbpedia.dbpedia.Constants;
import com.vietnamesedbpedia.dbpedia.GetClassDBpedia;
import com.vietnamesedbpedia.dbpedia.Test;
import com.vietnamesedbpedia.dbpedia.WikiMarkupParse;
import de.tudarmstadt.ukp.wikipedia.parser.Content;
import de.tudarmstadt.ukp.wikipedia.parser.Link;
import de.tudarmstadt.ukp.wikipedia.parser.ParsedPage;
import de.tudarmstadt.ukp.wikipedia.parser.Section;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import org.apache.commons.lang.math.NumberUtils;
import org.fbk.cit.hlt.thewikimachine.analysis.HardTokenizer;
import org.fbk.cit.hlt.thewikimachine.analysis.Tokenizer;
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;
import org.fbk.cit.hlt.thewikimachine.util.FrequencyHashSet;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplate;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplateParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.regex.Pattern;

/**
 * Created by vietanknb on 25/4/2017.
 */
public class DbpediaBox {
    private VBox contentBox;
    private static WebView webView = new WebView();
    private HBox header = new HBox();
    private Image next = new Image(new File("image/next.png").toURI().toString());
    private Image nextHover = new Image(new File("image/next_hover.png").toURI().toString());
    private Image back = new Image(new File("image/back.png").toURI().toString());
    private Image backHover = new Image(new File("image/back_hover.png").toURI().toString());
    private Image reload = new Image(new File("image/redo.png").toURI().toString());
    private Image reloadHover = new Image(new File("image/redo_hover.png").toURI().toString());
    private Image file = new Image(new File("image/file.png").toURI().toString());
    private ImageView nextImage = new ImageView();
    private ImageView backImage = new ImageView();
    private ImageView reloadImage = new ImageView();
    private ImageView fileImage = new ImageView();
    private static TextField urlWeb = new TextField();
    private static ArrayList<String> list = new ArrayList<>();
    public static HashMap<String, String> infoboxAtrribute = new HashMap<>();
    public static HashMap<String, String> mapping = new HashMap<>();
    private static ProgressIndicator progressIndicator = new ProgressIndicator();
    private static StackPane contentPane = new StackPane();
    static WebEngine webEngine = webView.getEngine();
    private static ArrayList<String> linksRedo = new ArrayList<>();
    private static ArrayList<String> linksUndo = new ArrayList<>();

    private static String foot = "</tbody> </table>\n" +
            "    </div>\n" +
            "<!-- row  -->\n" +
            "</div>\n" +
            "</div>\n" +
            "<!-- footer -->\n" +
            "<div class=\"footer\">\n" +
            "    <div class=\"container\">\n" +
            "\t<a href=\"http://virtuoso.openlinksw.com\" title=\"OpenLink Virtuoso\"><img class=\"powered_by\" src=\"" + new File("dbpediapage/statics/images/virt_power_no_border.png").toURI().toString() + "\" alt=\"Powered by OpenLink Virtuoso\"/></a>&nbsp; &nbsp;\n" +
            "\t<a href=\"http://linkeddata.org/\"><img alt=\"This material is Open Knowledge\" src=\"" + new File("dbpediapage/statics/images/LoDLogo.gif").toURI().toString() + "\"/></a> &nbsp; &nbsp;\n" +
            "\t<a href=\"http://dbpedia.org/sparql\"><img alt=\"W3C Semantic Web Technology\" src=\"" + new File("dbpediapage/statics/images/sw-sparql-blue.png").toURI().toString() + "\"/></a> &nbsp;  &nbsp;\n" +
            "\t<a href=\"http://www.opendefinition.org/\"><img alt=\"This material is Open Knowledge\" src=\"" + new File("dbpediapage/statics/images/od_80x15_red_green.png").toURI().toString() + "\"/></a>&nbsp; &nbsp;\n" +
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
            "    <script type=\"text/javascript\" src=\"" + new File("dbpediapage/statics/js/jquery.min.js").toURI().toString() + "\"></script>\n" +
            "    <script type=\"text/javascript\" src=\"" + new File("dbpediapage/statics/js/bootstrap.min.js").toURI().toString() + "\"></script>\n" +
            "    <script type=\"text/javascript\" src=\"" + new File("dbpediapage/statics/js/dbpedia.js").toURI().toString() + "\"></script>\n" +
            "</body>\n" +
            "</html>\n";
    private static String head = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>\n" +
            "<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML+RDFa 1.0//EN\" \"http://www.w3.org/MarkUp/DTD/xhtml-rdfa-1.dtd\">\n" +
            "<html xmlns=\"http://www.w3.org/1999/xhtml\"\n" +
            "    xmlns:dbpprop=\"http://dbpedia.org/property/\"\n" +
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
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + new File("dbpediapage/statics/css/bootstrap.min.css").toURI().toString() + "\" />\n" +
            "    <link rel=\"stylesheet\" type=\"text/css\" href=\"" + new File("dbpediapage/statics/css/dbpedia.css").toURI().toString() + "\" />";

    public static String infoboxName;

    private static String subject;
    private static String predicate;
    private static String object;

    public DbpediaBox(VBox contentBox) {
        this.contentBox = contentBox;
    }

    public void createDbpediaBox() {
        header.setSpacing(10);
        header.setPadding(new Insets(10, 10, 10, 10));
        header.setAlignment(Pos.CENTER);
        header.setStyle("-fx-background-color: #EEEEEE;");

        HBox.setHgrow(urlWeb, Priority.ALWAYS);
        VBox.setVgrow(contentPane, Priority.ALWAYS);

        nextImage.setFitWidth(16);
        nextImage.setFitHeight(16);
        nextImage.setImage(next);

        backImage.setFitWidth(16);
        backImage.setFitHeight(16);
        backImage.setImage(back);

        reloadImage.setFitWidth(16);
        reloadImage.setFitHeight(16);
        reloadImage.setImage(reload);

        fileImage.setFitWidth(16);
        fileImage.setFitHeight(16);
        fileImage.setImage(file);

        header.getChildren().setAll(backImage, nextImage, reloadImage, urlWeb, fileImage);

        progressIndicator.setMaxWidth(32);
        progressIndicator.setMaxHeight(32);
        progressIndicator.setStyle(" -fx-progress-color: #BDBDBD;");
        progressIndicator.setVisible(false);
        contentPane.getChildren().addAll(webView, progressIndicator);


        setAction();
        setData();
    }

    public StackPane getWebViewPane() {
        return contentPane;
    }

    private void setAction() {
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
        fileImage.setOnMouseClicked(mouseClicked);
    }

    private class MouseEntered implements EventHandler<MouseEvent> {

        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if (object.equals(reloadImage)) {
                reloadImage.setImage(reloadHover);
            } else if (object.equals(nextImage)) {
                nextImage.setImage(nextHover);
            } else if (object.equals(backImage)) {
                backImage.setImage(backHover);
            }
        }
    }

    private class MouseExited implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            if (object.equals(reloadImage)) {
                reloadImage.setImage(reload);
            } else if (object.equals(nextImage)) {
                nextImage.setImage(next);
            } else if (object.equals(backImage)) {
                backImage.setImage(back);
            }
        }
    }

    private static boolean isUndo = false;

    private class MouseClicked implements EventHandler<MouseEvent> {
        @Override
        public void handle(MouseEvent event) {
            Object object = event.getSource();
            //progressIndicator.setVisible(true);
            //title = textField.getText();
            if (object.equals(reloadImage)) {
                webEngine.reload();
            } else if (object.equals(nextImage)) {
                if (!linksRedo.isEmpty()) {
                    String link = linksRedo.get(linksRedo.size() - 1);
                    if (!link.equals("")) {
//                        linksUndo.add(link);
                        try {
                            webEngine.load(link);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        urlWeb.setText(link);
                    } else {
//                        linksUndo.add("");
                        try {
                            DbpediaBox.load();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    linksRedo.remove(linksRedo.size() - 1);
                }
            } else if (object.equals(backImage)) {
//                if(!links.empty()){
//                    String link = links.pop();
//                    if(!link.equals("")){
////                        linksUndo.add(link);
//                        try{
//                            webEngine.load(link);
//                        }catch (Exception e){
//                            e.getMessage();
//                        }
//                        urlWeb.setText(link);
//                    }else{
////                        linksUndo.add("");
//                        try{
//                            DbpediaBox.load();
//                        }catch (Exception e){
//                            e.getMessage();
//                        }
//                    }
//                }
                if (!linksUndo.isEmpty()) {
                    isUndo = true;
                    String link = linksUndo.get(linksUndo.size() - 1);
                    if (!link.equals("")) {
//                        linksUndo.add(link);
                        try {
                            webEngine.load(link);
                        } catch (Exception e) {
                            e.getMessage();
                        }
                        urlWeb.setText(link);
                    } else {
//                        linksUndo.add("");
                        try {
                            DbpediaBox.load();
                        } catch (Exception e) {
                            e.getMessage();
                        }
                    }
                    linksUndo.remove(linksUndo.size() - 1);

                }
            } else if (object.equals(fileImage)) {
                System.out.println("Click");
                new ShowRDF().showRDF(WikiBox.title + ".rdf");
            }
        }
    }


    private static final ExecutorService pool = Executors.newFixedThreadPool(10);

    public static Future<String> load() throws IOException {
        progressIndicator.setVisible(true);
        return pool.submit(new Callable<String>() {
            @Override
            public String call() throws Exception {
                subject = "<vi.dbpedia.org/page/" + WikiBox.title + ">";
                String link = "https://vi.wikipedia.org/w/index.php?title=" + URLEncoder.encode(WikiBox.title, "UTF-8") + "&action=edit";
                System.out.println(URLEncoder.encode(WikiBox.title, "UTF-8"));
                URL url = new URL(link);
                InputStream is = url.openStream();
//                System.out.println(url.getPath());
                Document document = Jsoup.parse(is, "UTF-8", "");
                System.out.println(document.text());
                Elements textArea = document.select("textarea");

                ArrayList<WikiTemplate> listOfTemplates = WikiTemplateParser.parse(textArea.text(), false);

                boolean isFound = false;
                if (!infoboxAtrribute.isEmpty()) {
                    infoboxAtrribute.clear();
                }
                if (!linksRedo.isEmpty()) {
                    linksRedo.removeAll(linksRedo);
                }
                if (!linksUndo.isEmpty()) {
                    linksUndo.removeAll(linksUndo);
                }

                for (WikiTemplate wikiTemplate : listOfTemplates) {
                    // System.out.println(wikiTemplate.getFirstPart());
                    if (list.contains(Test.modelString(wikiTemplate.getFirstPart()))) {
                        HashMap<String, String> parts = wikiTemplate.getHashMapOfParts();
                        Set<String> keys = parts.keySet();
                        for (String k : keys) {
                            if (!parts.get(k).equals("")) {
//                                System.out.println(k+"----"+parts.get(k));
                                infoboxAtrribute.put(k, parts.get(k));
                            }
                        }
                        infoboxName = Test.modelString(wikiTemplate.getFirstPart());
                        isFound = true;
                        break;
                    }

                }

                if (!isFound) {
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            showMessage("");
                        }
                    });
                    return "";
                }

                System.out.println("Infobox size: " + infoboxAtrribute.size());


                if (mapping.isEmpty()) {
                    mapping.clear();
                }

                HashMap<String, Integer> countProperty = new HashMap<>();
                HashMap<String, ArrayList<String>> multiProperty = new HashMap<>();
                File file = new File("mapping_vi", infoboxName.replace(" ", "_") + "_result.xls");
                WorkbookSettings ws = new WorkbookSettings();
                ws.setEncoding("Cp1252");
                System.out.println("File: " + file.isFile());

                try {
                    Workbook w = Workbook.getWorkbook(file, ws);
                    Sheet sheet = w.getSheet(0);
                    System.out.println(sheet.getRows());
                    System.out.println(sheet.getCell(0, 0).getContents());
                    System.out.println(sheet.getCell(2, 0).getContents());
                    for (int i = 0; i < sheet.getRows(); i++) {
                        String pttr = sheet.getCell(2, i).getContents();
//                        System.out.println(pttr.equals(""));
                        if (!pttr.equals("")) {
                            System.out.println("next");
                            addCount(countProperty, pttr);
                            String attr = sheet.getCell(0, i).getContents();
                            mapping.put(attr, pttr);
                        }

                    }
                    System.out.println("airpedia");
                    System.out.println("Mapping size: " + mapping.size());
                    String owlFile = Constants.ONTOLOGY_2016;
//                    System.out.println(new File(owlFile).isFile());
                    AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
                    System.out.println(airpediaOntology.isLowerProp());

                    Set<String> keys = infoboxAtrribute.keySet();
                    String value;
                    String pttr;

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(head);
                    stringBuilder.append("<title>Thông tin về:" + WikiBox.title + "</title>");
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
                            "\t    <a class=\"navbar-brand\" href=\"http://wiki.dbpedia.org/about\" title=\"About DBpedia\" style=\"color: #2c5078\">\n" +
                            "\t    <img class=\"img-responsive\" src=\"" + new File("dbpediapage/statics/images/dbpedia_logo_land_120.png").toURI().toString() + "\" alt=\"About DBpedia\" style=\"display: inline-block;  margin-top: -12px\"/>\n" +
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
                            "\t    <a href=\"#\">" + WikiBox.title + "</a>\n" +
                            "\t</h1>");

                    stringBuilder.append("<div class=\"page-resource-uri\">\n" +
                            "\t    An Entity " +
                            "\t    from Named Graph : <a href=\"http://vi.dbpedia.org\">http://vi.dbpedia.org</a>,\n" +
                            "\t    within Data Space : <a href=\"http://vi.dbpedia.org\">vi.dbpedia.org</a>\n" +
                            "\t</div>\n" +
                            "    </div>\n" +
                            "<!-- page-header -->\n" +
                            "<!-- row -->\n" +
                            "    <div class=\"row\">\n" +
                            "\t<div class=\"col-xs-12\">\n" +
                            "\t    <!-- proptable -->\n" +
                            "\t    <table class=\"description table table-striped\"> <tbody>\n" +
                            "<tr> <th class=\"col-xs-3\">Property</th>\n" +
                            "\t\t<th class=\"col-xs-9\">Value</th> </tr>");

                    FileWriter fileWriter = new FileWriter(new File("result_program", WikiBox.title + ".rdf"));
                    BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
                    ArrayList<String> listAttrTerm = new ArrayList<>();
                    int numberTermPeriod = numberTermPeriod(keys, listAttrTerm);
                    for (String k : keys) {
                        System.out.println("next 111");
//                        if (!checkAttributeContainNumber(k)) {
//                            if (!listAttrTerm.contains(k)) {
                                if (mapping.containsKey(k.toLowerCase()) || mapping.containsKey(k)) {
                                    String v_date = "";
                                    value = infoboxAtrribute.get(k);
                                    pttr = mapping.get(k.toLowerCase());
                                    System.out.println(pttr + "    " + value);
                                    if (value.contains("{{") && value.contains("}}") && !value.contains("<")) {
                                        value = value.replace("{{", "").replace("}}", "");
                                        if (value.contains("|")) {
                                            String[] vs = value.split(Pattern.quote("|"));
                                            if (vs.length > 3) {
//                                        value = vs[1] + " "+ vs[2] + " " + vs[3];
                                                v_date = vs[3] + "-" + vs[2] + "-" + vs[1];
                                            }
                                        }

                                    }
                                    if (value.contains("Tháng") || value.contains("tháng")) {
                                        if (value.contains("<")) {
                                            value = value.split("<")[0];
                                        }
                                        ArrayList<Integer> listDate = getDateFromString(value);
                                        if (listDate.size() >= 3) {
                                            v_date = listDate.get(0) + "-" + listDate.get(1) + "-" + listDate.get(2);
                                        }
                                    }
                                    WikiMarkupParse wikiMarkupParser = WikiMarkupParse.getInstance();
                                    // Parse page
                                    System.out.println("Wiki: " + wikiMarkupParser.toString());
                                    ParsedPage parsedPage = wikiMarkupParser.parsePage(value);
                                    System.out.println("Parse: " + parsedPage.toString());
                                    HashMap<String, String> h1 = airpediaOntology.getProperty(pttr);
//                            System.out.println("h1: "+h1.size());
                                    ArrayList<HashSet> ret = getNumbersFromTokens(getTextFromWiki(parsedPage));
                                    HashSet<Long> v_long = (HashSet<Long>) ret.get(0);
                                    HashSet<Double> v_double = (HashSet<Double>) ret.get(1);
                                    Iterator<Long> longIterator = null;
                                    if (!v_long.isEmpty()) {
                                        longIterator = v_long.iterator();
                                    }
                                    Iterator<Double> doubleIterator = null;
                                    if (!v_double.isEmpty()) {
                                        doubleIterator = v_double.iterator();
                                    }

                                    System.out.println("Long: " + v_long + " Double: " + v_double);

                                    List<String> linkList = getLinksFromWiki(parsedPage);
                                    int size = linkList.size();

                                    if (h1 != null) {
//                            System.out.println(pttr + " range: "+ h1.get("range"));
                                        String range = h1.get("range");
                                        if (range != null) {
                                            if (range.equals("double") ||
                                                    range.equals("float") ||
                                                    range.equals("nonNegativeInteger") ||
                                                    range.equals("positiveInteger") ||
                                                    range.equals("integer")) {

                                                if (!range.equals("double")) {
                                                    if(longIterator!=null){
                                                        long v = longIterator.next();
                                                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                                "</td><td><ul>\n" +
                                                                "\t<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + v + "</span><small> (xsd:" + range + ")</small></span></li>\n" +
                                                                "</ul></td></tr>");
                                                        predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                                        object = "\"" + v + "\"^^" + "<http://www.w3.org/2001/XMLSchema#" + range + ">";
                                                        bufferedWriter.write(subject + " " + predicate + " " + object);
                                                        bufferedWriter.newLine();
                                                    }
                                                } else {
                                                    if(doubleIterator!=null){
                                                        double v = doubleIterator.next();
                                                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                                "</td><td><ul>\n" +
                                                                "\t<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + v + "</span><small> (xsd:" + range + ")</small></span></li>\n" +
                                                                "</ul></td></tr>");
                                                        predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                                        object = "\"" + v + "\"^^" + "<http://www.w3.org/2001/XMLSchema#" + range + ">";
                                                        bufferedWriter.write(subject + " " + predicate + " " + object);
                                                        bufferedWriter.newLine();
                                                    }else{
                                                        long v = longIterator.next();
                                                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                                "</td><td><ul>\n" +
                                                                "\t<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + v + "</span><small> (xsd:" + range + ")</small></span></li>\n" +
                                                                "</ul></td></tr>");
                                                        predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                                        object = "\"" + v + "\"^^" + "<http://www.w3.org/2001/XMLSchema#" + range + ">";
                                                        bufferedWriter.write(subject + " " + predicate + " " + object);
                                                        bufferedWriter.newLine();
                                                    }
                                                }


                                            } else if (range.equals("date") || range.equals("gYear") || range.equals("gYearMonth")) {


                                                if (range.equals("gYear")) {
                                                    v_date = longIterator.next().toString();
                                                } else if (range.equals("gYearMonth")) {
                                                    long m = 0;
                                                    long y = 0;
                                                    if (v_long.size() == 2) {
                                                        while (longIterator.hasNext()) {
                                                            long temp = longIterator.next();
                                                            if (temp > 31) y = temp;
                                                            m = temp;
                                                        }
                                                        v_date = m + "-" + y;
                                                    }
                                                } else if (range.equals("date")) {
                                                    long m = 0;
                                                    long y = 0;
                                                    if (v_long.size() == 2) {
                                                        while (longIterator.hasNext()) {
                                                            long temp = longIterator.next();
                                                            if (temp > 31) y = temp;
                                                            m = temp;
                                                        }
                                                        v_date = m + "-" + y;
                                                    }
                                                    if (v_long.size() == 1) {
                                                        v_date = longIterator.next().toString();
                                                    }
                                                }
                                                if (v_date.equals("")) {
                                                    v_date = v_long.toString();
                                                }
                                                stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                        "</td><td><ul>\n" +
                                                        "\t<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + v_date + "</span><small> (xsd:" + range + ")</small></span></li>\n" +
                                                        "</ul></td></tr>");
                                                predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                                object = "\"" + v_date + "\"^^" + "<http://www.w3.org/2001/XMLSchema#" + range + ">";
                                                bufferedWriter.write(subject + " " + predicate + " " + object);
                                                bufferedWriter.newLine();
                                            } else {
//                                    System.out.println("String: "+pttr);
                                                if (countProperty.get(pttr) == 1) {
                                                    stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                            "</td><td><ul>\n");
                                                    predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                                    if (size > 0) {
                                                        for (int i = 0; i < size; i++) {
                                                            stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\" href=\"http://dbpedia.org/resource/" + linkList.get(i) + "\"><small>dbr</small>:" + linkList.get(i) + "</a></span></li>");
                                                            object = "<vi.dbpedia.org/page/" + linkList.get(i) + ">";
                                                            bufferedWriter.write(subject + " " + predicate + " " + object);
                                                            bufferedWriter.newLine();
                                                        }
                                                    }

                                                    for (Long numLong : v_long) {
                                                        stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + numLong + "</span></span></li>\n");
                                                        object = "\"" + v_long + "\"";
                                                        bufferedWriter.write(subject + " " + predicate + " " + object);
                                                        bufferedWriter.newLine();
                                                    }
                                                    if (size == 0 && v_long.size() == 0) {
                                                        stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + value + "</span></span></li>\n");
                                                        object = "\"" + value + "\"";
                                                        bufferedWriter.write(subject + " " + predicate + " " + object);
                                                        bufferedWriter.newLine();
                                                    }

                                                    stringBuilder.append("</ul></td></tr>");
                                                } else {
                                                    for (int i = 0; i < size; i++) {
                                                        saveMultiPropertyValue(multiProperty, pttr, linkList.get(i));
                                                    }
                                                }

//                                    System.out.println(value);
                                            }
                                        } else {
                                            stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + pttr + "\"><small>dbo:</small>" + pttr + "</a>\n" +
                                                    "</td><td><ul>\n");
                                            predicate = "<http://dbpedia.org/ontology/" + pttr + ">";
                                            if (size > 0) {
                                                for (int i = 0; i < size; i++) {
                                                    stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\" href=\"http://dbpedia.org/resource/" + linkList.get(i) + "\"><small>dbr</small>:" + linkList.get(i) + "</a></span></li>");
                                                    object = "<vi.dbpedia.org/page/" + linkList.get(i) + ">";
                                                    bufferedWriter.write(subject + " " + predicate + " " + object);
                                                    bufferedWriter.newLine();
                                                }
                                            }

                                            for (Long numLong : v_long) {
                                                stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + numLong + "</span></span></li>\n");
                                                object = "\"" + v_long + "\"";
                                                bufferedWriter.write(subject + " " + predicate + " " + object);
                                                bufferedWriter.newLine();
                                            }

                                            if (size == 0 && v_long.size() == 0) {
                                                stringBuilder.append("<li><span class=\"literal\"><span property=\"dbo:" + pttr + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\">" + value + "</span></span></li>\n");
                                                object = "\"" + value + "\"";
                                                bufferedWriter.write(subject + " " + predicate + " " + object);
                                                bufferedWriter.newLine();
                                            }

                                            stringBuilder.append("</ul></td></tr>");
                                        }
                                    } else {
                                        System.out.println("NULL :" + pttr);
                                        if (pttr.equals("foaf:name")) {
                                            stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://xmlns.com/foaf/0.1/name\"><small>foaf:</small>name</a>\n" +
                                                    "</td><td><ul>\n" +
                                                    "\t<li><span class=\"literal\"><span property=\"foaf:name\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xml:lang=\"vi\">" + value + "</span><small> (vi)</small></span></li></ul></td></tr>");
                                            predicate = "<http://xmlns.com/foaf/0.1/name>";
                                            object = "\"" + value + "\"" + "@vi";
                                            bufferedWriter.write(subject + " " + predicate + " " + object);
                                            bufferedWriter.newLine();
                                        } else if (pttr.equals("foaf:homepage")) {
                                            stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://xmlns.com/foaf/0.1/name\"><small>foaf:</small>homepage</a>\n" +
                                                    "</td><td><ul>\n" +
                                                    "\t<li><span class=\"literal\"><span property=\"foaf:homepage\" xmlns:foaf=\"http://xmlns.com/foaf/0.1/\" xml:lang=\"vi\"><a>" + value + "</a></span><small> (vi)</small></span></li></ul></td></tr>");
                                            predicate = "<http://xmlns.com/foaf/0.1/homepage>";
                                            object = "\"" + value + "\"";
                                            bufferedWriter.write(subject + " " + predicate + " " + object);
                                            bufferedWriter.newLine();
                                        } else if (pttr.equals("latd")) {
                                            double v = doubleIterator.next();
                                            stringBuilder.append("/tr><tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/2003/01/geo/wgs84_pos#lat\"><small>geo:</small>lat</a>\n" +
                                                    "</td><td><ul>\n" +
                                                    "\t<li><span class=\"literal\"><span property=\"geo:lat\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\">" + v + "</span><small> (xsd:float)</small></span></li>\n" +
                                                    "</ul></td></tr>");

                                        } else if (pttr.equals("longd")) {
                                            double v = doubleIterator.next();
                                            stringBuilder.append("/tr><tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/2003/01/geo/wgs84_pos#lat\"><small>geo:</small>long</a>\n" +
                                                    "</td><td><ul>\n" +
                                                    "\t<li><span class=\"literal\"><span property=\"geo:lat\" xmlns:geo=\"http://www.w3.org/2003/01/geo/wgs84_pos#\">" + v + "</span><small> (xsd:float)</small></span></li>\n" +
                                                    "</ul></td></tr>");

                                        }

                                    }
                                    if (parsedPage != null) {
//                            System.out.println(getLinksFromWiki(parsedPage));
//                            System.out.println(getNumbersFromTokens(getTextFromWiki(parsedPage)));
                                    }
//
//                                }
//                            }

                        } else {

                        }
                    }

                    Set<String> properties = multiProperty.keySet();

                    for (String p : properties) {
                        stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/" + p + "\"><small>dbo:</small>" + p + "</a>\n" +
                                "</td><td><ul>\n");
                        ArrayList<String> links = multiProperty.get(p);
                        predicate = "<http://dbpedia.org/ontology/" + p + ">";
                        for (String l : links) {
                            stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:" + p + "\" xmlns:dbo=\"http://dbpedia.org/ontology/\" href=\"http://dbpedia.org/resource/" + l + "\"><small>dbr</small>:" + l + "</a></span></li>");
                            object = "<vi.dbpedia.org/page/" + l + ">";
                            bufferedWriter.write(subject + " " + predicate + " " + object);
                            bufferedWriter.newLine();
                        }
                        stringBuilder.append("</ul></td></tr>");
                    }

//                    if (numberTermPeriod > 0) {
//                        stringBuilder.append("<tr class=\"even\"><td class=\"property\"><a class=\"uri\" href=\"http://dbpedia.org/ontology/termPeriod\"><small>dbo:</small>termPeriod</a>\n" +
//                                "</td><td><ul>");
//                        for (int i = 1; i <= numberTermPeriod; i++) {
//                            stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"dbo:termPeriod\" xmlns:dbo=\"http://dbpedia.org/ontology/\" href=\"http://vi.dbpedia.org/resource/" + WikiBox.title + "_" + i + "\"><small>dbr</small>:" + WikiBox.title + "_" + i + "</a></span></li>");
//                        }
//                        stringBuilder.append("</ul></td></tr>");
//                    }


                    ArrayList<String> listType = GetClassDBpedia.getDBpediaClassFromTitle(WikiBox.title);
//                System.out.println(list.toString());

                    stringBuilder.append("<tr class=\"odd\"><td class=\"property\"><a class=\"uri\" href=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#type\"><small>rdf:</small>type</a>\n" +
                            "</td><td><ul>");
                    stringBuilder.append("<li><span class=\"literal\"><a class=\"uri\" rel=\"rdf:type\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" href=\"http://www.w3.org/2002/07/owl#Thing\"><small>owl</small>:Thing</a></span></li>");
                    for (String a : listType) {
                        stringBuilder.append("\t<li><span class=\"literal\"><a class=\"uri\" rel=\"rdf:type\" xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\" href=\"http://dbpedia.org/ontology/" + a + "\"><small>dbo</small>:" + a + "</a></span></li>");
                    }
                    stringBuilder.append("</ul></td></tr>");
                    stringBuilder.append(foot);
                    final String a = stringBuilder.toString();


                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            updateUI(a);
                        }
                    });

                    bufferedWriter.close();
                    fileWriter.close();


                } catch (Exception e) {
                    System.out.println("Exception: " + e.getMessage());
                    e.printStackTrace();
                }
                return null;
            }
        });
    }

    private static void addCount(HashMap<String, Integer> countProperty, String property) {
        if (!countProperty.containsKey(property)) {
            countProperty.put(property, 1);
        } else {
            int c = countProperty.get(property);
            c++;
            countProperty.put(property, c);
        }
    }

    private static boolean checkAttributeContainNumber(String attr) {
        String[] attrs = attr.trim().split(" ");
        if (isNumeric(attrs[attrs.length - 1])) {
            return true;
        }
        return false;
    }

    private static int numberTermPeriod(Set<String> attrs, ArrayList<String> listAttr) {
        int count = -1;
        for (String a : attrs) {
            StringBuilder output = new StringBuilder();
            StringBuilder number = new StringBuilder();
            if (a.contains("_")) {
                a = a.replace("_", " ");
            }
            char[] listChar = a.trim().toCharArray();
            for (char b : listChar) {
                if (!NumberUtils.isNumber(String.valueOf(b))) {
                    output.append(b);
                } else {
                    number.append(b);
                }
            }
            if (number.toString().length() > 0) {
                if (Integer.parseInt(number.toString()) > count) {
                    count = Integer.parseInt(number.toString());
                }
                if (!listAttr.contains(output.toString().trim())) {
                    listAttr.add(output.toString().trim());
                }
            }


        }
        return (count < 30) ? count + 1 : 0;
    }

    private static void saveMultiPropertyValue(HashMap<String, ArrayList<String>> multiProperty, String pttr, String value) {
        if (multiProperty.containsKey(pttr)) {
            multiProperty.get(pttr).add(value);
        } else {
            multiProperty.put(pttr, new ArrayList<String>());
            multiProperty.get(pttr).add(value);
        }

    }


    private static ArrayList<Integer> getDateFromString(String value) {
        ArrayList<Integer> listDate = new ArrayList<>();

        String[] vs = value.split(" ");
        for (int i = 0; i < vs.length; i++) {
            if (vs[i].contains("[[") || vs[i].contains("]]")) {
                String temp = vs[i].replace("[[", "").replace("]]", "");
                if (isNumeric(temp)) {
                    listDate.add(Integer.parseInt(temp));
                }
            }
        }

        return listDate;
    }

    public static boolean isNumeric(String str) {
        for (char c : str.toCharArray()) {
            if (!Character.isDigit(c)) return false;
        }
        return true;
    }

    public static WebViewListener webViewListener = new WebViewListener();

    private static void updateUI(String a) {
        urlWeb.setText("vi.dbpedia.org/page/" + WikiBox.title);
        webEngine.loadContent(a);
        webEngine.setJavaScriptEnabled(true);
        webEngine.locationProperty().addListener(webViewListener);
        progressIndicator.setVisible(false);
    }

    private static void showMessage(String a) {
        JFXDialogLayout content = new JFXDialogLayout();
        content.setHeading(new Text("Sorry, but not have mapping"));
        content.setBody(new Text("Please, enter other Wikipedia article"));

        JFXButton okButton = new JFXButton("OK");


        content.setActions(okButton);
        final JFXDialog jfxDialog = new JFXDialog(contentPane, content, JFXDialog.DialogTransition.CENTER);
        jfxDialog.show();

        okButton.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                jfxDialog.close();
            }
        });
        webEngine.loadContent("");

        progressIndicator.setVisible(false);
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


    public HBox getHeader() {
        return header;
    }

    static boolean isLoadTerm = false;

    private static class WebViewListener implements ChangeListener<String> {

        @Override
        public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
            try {

                final String link = URLDecoder.decode(newValue, "UTF-8");
                System.out.println("Link: " + link);


//                if (isLoadTerm) {
//                    isLoadTerm = false;
//                }

//                if (link.contains(WikiBox.title) && link.contains("_")) {
//                    System.out.println("Load term");
//                    loadTermPeriod(link);
//                }else{
                    System.out.println("changed");
                    urlWeb.setText(link);

//                    System.out.println(oldValue + newValue);
                    if (!isUndo) {
                        if (!oldValue.equals("")) {
                            linksUndo.add(URLDecoder.decode(oldValue, "UTF-8"));
                        } else {
                            linksUndo.add("");
                        }
                    } else {
                        if (!oldValue.equals("")) {
                            linksRedo.add(URLDecoder.decode(oldValue, "UTF-8"));
                        } else {
                            linksRedo.add("");
                        }
                        isUndo = false;
                    }
//                }

                System.out.println("Redo: " + linksRedo.toString());
                System.out.println("Undo: " + linksUndo.toString());

            } catch (Exception e) {
                e.getMessage();
            }

        }
    }

    private static void loadTermPeriod(String link) {

        progressIndicator.setVisible(true);
        //
        String[] as = link.split("_");
        int n = Integer.parseInt(as[as.length - 1]);
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(head);
        stringBuilder.append("<title>Thông tin về:" + WikiBox.title + "</title>");
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
                "\t    <a class=\"navbar-brand\" href=\"http://wiki.dbpedia.org/about\" title=\"About DBpedia\" style=\"color: #2c5078\">\n" +
                "\t    <img class=\"img-responsive\" src=\"" + new File("dbpediapage/statics/images/dbpedia_logo_land_120.png").toURI().toString() + "\" alt=\"About DBpedia\" style=\"display: inline-block;  margin-top: -12px\"/>\n" +
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
                "\t    <a href=\"#\">" + link + "</a>\n" +
                "\t</h1>");

        stringBuilder.append("<div class=\"page-resource-uri\">\n" +
                "\t    An Entity " +
                "\t    from Named Graph : <a href=\"http://vi.dbpedia.org\">http://vi.dbpedia.org</a>,\n" +
                "\t    within Data Space : <a href=\"http://vi.dbpedia.org\">vi.dbpedia.org</a>\n" +
                "\t</div>\n" +
                "    </div>\n" +
                "<!-- page-header -->\n" +
                "<!-- row -->\n" +
                "    <div class=\"row\">\n" +
                "\t<div class=\"col-xs-12\">\n" +
                "\t    <!-- proptable -->\n" +
                "\t    <table class=\"description table table-striped\"> <tbody>\n" +
                "<tr> <th class=\"col-xs-3\">Property</th>\n" +
                "\t\t<th class=\"col-xs-9\">Value</th> </tr>");
        if (n == 1) {

        } else {

        }
        stringBuilder.append(foot);
//        WebEngine webEngine1 = webView.getEngine();
//        webEngine1.locationProperty().removeListener(webViewListener);
        webEngine.loadContent(stringBuilder.toString());
//        webEngine1.locationProperty().addListener(webViewListener);
//        webEngine.locationProperty().addListener(webViewListener);
        isLoadTerm = true;

//        urlWeb.setText(link);
        //
        progressIndicator.setVisible(false);
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

    private void setData() {
        list.add("Bảng phân loại");
        list.add("Thông tin khu dân cư");
        list.add("Thông tin hành tinh");
        list.add("Thông tin đơn vị hành chính Việt Nam");
        list.add("Thông tin nhân vật hoàng gia");
        list.add("Thông tin tiểu sử bóng đá");
        list.add("Thông tin nhạc sĩ");
        list.add("Thông tin đĩa đơn");
        list.add("Thông tin phim");
        list.add("Thông tin viên chức");
        list.add("Viên chức");
        list.add("Thông tin chiến tranh");
        list.add("Thông tin truyền hình");
        list.add("Infobox ship career");
        list.add("Thông tin nhà khoa học");
        list.add("Thông tin sân bay");
        list.add("Thông tin album nhạc");
        list.add("Tóm tắt công ty");
        list.add("Tiểu sử quân nhân");
        list.add("Thông tin nhà văn");
        list.add("Thông tin máy bay");
        list.add("Thông tin trò chơi điện tử");
        list.add("Thông tin diễn viên");
        list.add("Thông tin trường học");
        list.add("Thông tin sách");
        list.add("Thông tin hãng hàng không");
        list.add("Thông tin đơn vị quân sự");
        list.add("Thông tin phần mềm");
        list.add("Thông tin sông");
        list.add("Thông tin hóa chất");
        list.add("Tóm tắt câu lạc bộ bóng đá");
        list.add("Thông tin tổ chức");
        list.add("Tóm tắt về ngôn ngữ");
        list.add("Thông tin quốc gia");
        list.add("Thông tin bài hát");
        list.add("Thông tin dân tộc");
        list.add("Thông tin hoa hậu");
        list.add("Thông tin nghệ sĩ");
        list.add("Thông tin hồ");
        list.add("Thông tin đảng phái chính trị");
        list.add("Thông tin trang web");
        list.add("Thông tin núi");
        list.add("Thông tin nhà ga");
        list.add("Trường học");
    }


}
