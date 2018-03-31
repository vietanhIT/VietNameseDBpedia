package com.vietnamesedbpedia.program;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.EventHandler;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplate;
import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplateParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.InputStream;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

/**
 * Created by vieta on 5/4/2017.
 */
public class InfoboxBox {
    private VBox contentBox;
    private StackPane stackPane = new StackPane();
    private static ProgressIndicator progressIndicator = new ProgressIndicator();
    private static ArrayList<String> list = new ArrayList<>();

    private TableView<AttributeColumn> table = new TableView<>();
    private static final ObservableList<AttributeColumn> data =
            FXCollections.observableArrayList(
            );
    public static HashMap<String, String> infoboxAtrribute = new HashMap<>();
    public static boolean isLoad = false;


    public InfoboxBox(VBox contentBox) {
        this.contentBox = contentBox;
    }

    public InfoboxBox() {

    }

    public void createInfoboxBox() {
        VBox.setVgrow(stackPane, Priority.ALWAYS);
        VBox.setVgrow(table, Priority.ALWAYS);
//        HBox.setHgrow(table, Priority.ALWAYS);
        table.prefWidthProperty().bind(contentBox.widthProperty());
        TableColumn attribute = new TableColumn("Attribute");
        attribute.prefWidthProperty().bind(table.widthProperty().multiply(0.25));
        attribute.setMinWidth(100);
        attribute.setCellValueFactory(
                new PropertyValueFactory<AttributeColumn, String>("attributeName"));

        TableColumn value = new TableColumn("Value");
        value.setMinWidth(100);
        value.prefWidthProperty().bind(table.widthProperty().multiply(0.75));
        value.setCellValueFactory(
                new PropertyValueFactory<AttributeColumn, String>("attributeValue"));

        table.setItems(data);
        table.getColumns().addAll(attribute, value);
        table.getSelectionModel().setCellSelectionEnabled(true);
        table.setRowFactory(new Callback<TableView<AttributeColumn>, TableRow<AttributeColumn>>() {
            @Override
            public TableRow<AttributeColumn> call(TableView<AttributeColumn> param) {
                TableRow<AttributeColumn> row = new TableRow<>();
                row.setOnMouseEntered(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        System.out.println("hover");
                    }
                });
                return row;
            }
        });

        progressIndicator.setMaxWidth(32);
        progressIndicator.setMaxHeight(32);
        progressIndicator.setStyle(" -fx-progress-color: #BDBDBD;");
        progressIndicator.setVisible(false);
        stackPane.getChildren().add(table);
        stackPane.getChildren().add(progressIndicator);
        setData();
    }

    public TableView getTableView() {
        return table;
    }

    public StackPane getInfoboxStackPane() {
        return stackPane;
    }

    public static class AttributeColumn {
        private final SimpleStringProperty attributeName;
        private final SimpleStringProperty attributeValue;

        private AttributeColumn(String name, String value) {
            this.attributeName = new SimpleStringProperty(name);
            this.attributeValue = new SimpleStringProperty(value);
        }

        public String getAttributeName() {
            return attributeName.get();
        }

        public void setAttributeName(String fName) {
            attributeName.set(fName);
        }

        public String getAttributeValue() {
            return attributeValue.get();
        }

        public void setAttributeValue(String fName) {
            attributeValue.set(fName);
        }

    }

    public class ExtractInfoboxFromWiki implements Runnable {
        private StackPane stackPane;

        public ExtractInfoboxFromWiki(StackPane stackPane) {
            this.stackPane = stackPane;
        }

        @Override
        public void run() {
            contentBox.getChildren().setAll(stackPane);
            progressIndicator.setVisible(true);
            try{
                Thread.sleep(2000);
            }catch (Exception e){
                e.getMessage();
            }

            Platform.runLater(new Runnable() {
                @Override
                public void run() {
                    try {
                        if (!isLoad) {
                            data.removeAll(data);
                            String link = "https://vi.wikipedia.org/w/index.php?title=" + URLEncoder.encode(WikiBox.title) + "&action=edit";
                            URL url = new URL(link);
                            InputStream is = url.openStream();
                            System.out.println(url.getPath());
                            Document document = Jsoup.parse(is, "UTF-8", "");
                            Elements textArea = document.select("textarea");

                            ArrayList<WikiTemplate> listOfTemplates = WikiTemplateParser.parse(textArea.text(), false);

                            for (WikiTemplate wikiTemplate : listOfTemplates) {
                                // System.out.println(wikiTemplate.getFirstPart());
                                if (list.contains(wikiTemplate.getFirstPart())) {
                                    HashMap<String, String> parts = wikiTemplate.getHashMapOfParts();
                                    Set<String> keys = parts.keySet();
                                    for (String k : keys) {
                                        if (!parts.get(k).equals("")) {
//                                System.out.println(k+"----"+parts.get(k));
                                            data.add(new AttributeColumn(k, parts.get(k)));
                                            infoboxAtrribute.put(k, parts.get(k));
                                        }
                                    }
                                    DbpediaBox.infoboxName = wikiTemplate.getFirstPart();
                                    break;
                                }

                            }
                            //System.out.println(textArea.text());
                            isLoad = true;
                        }
                        closeProgress();
                    } catch (Exception e) {
                        e.getMessage();
                    }
                }
            });

        }
    }

    public void load() {

    }


    private static void closeProgress() {
        progressIndicator.setVisible(false);
    }

    private void setData() {
        list.add("Bảng phân loại");
        list.add("Thông tinh khu dân cư");
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

    private ArrayList<String> getData() {
        return list;
    }

}
