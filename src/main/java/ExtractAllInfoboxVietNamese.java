import jxl.*;
import org.apache.lucene.analysis.WhitespaceAnalyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.util.Version;

import java.io.File;
import java.io.IOException;

/**
 * Created by vieta on 18/12/2016.
 */
public class ExtractAllInfoboxVietNamese {
    private IndexWriter indexWriter;

    public ExtractAllInfoboxVietNamese(){

    }

    public ExtractAllInfoboxVietNamese(String outFile){
        try{
            indexWriter = new IndexWriter(FSDirectory.open(new File(outFile)), new WhitespaceAnalyzer());
        }catch (Exception e){
            e.getMessage();
        }
    }

    private void getInfoboxName(){
        File inputWorkbook = new File("infobox_vietnamese.xls");
        Workbook w;
        try{
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");
            w = Workbook.getWorkbook(inputWorkbook,ws);
            // Get the first sheet
            Sheet sheet = w.getSheet(0);
            // Loop over first 10 column and lines

                for (int i = 0; i < sheet.getRows(); i++) {
                    String infoboxName = "";
                    String frequency;
                    Document document = new Document();
                    for (int j=0; j<2;j++) {
                        Cell cell = sheet.getCell(j, i);
                        CellType type = cell.getType();
                        if (j == 0) {
                            frequency = cell.getContents();
                            document.add(new Field("frequency", frequency, Field.Store.YES, Field.Index.NOT_ANALYZED));
                        } else {
                            infoboxName = cell.getContents();
                            infoboxName = infoboxName.trim().replace(" ","_");
                            if((int)infoboxName.charAt(infoboxName.length()-1)>96){
                                infoboxName = infoboxName.substring(0,infoboxName.length()-1);
                            }
                            infoboxName = infoboxName.toLowerCase();
                            System.out.println(infoboxName);
                            document.add(new Field("infobox", infoboxName, Field.Store.YES, Field.Index.NOT_ANALYZED));
                        }
                    }
                    indexWriter.addDocument(document);
                }
        }catch (Exception e){
            e.getMessage();
        }

        try{
            indexWriter.optimize();
            indexWriter.close();
        }catch (Exception e){
            e.getMessage();
        }

    }

    private IndexSearcher indexSearcher;
    private QueryParser queryParser;
    private Query query;

    private void initSearch(String indexDirectoryPath){
        try{
            Directory indexDirectory =
                    FSDirectory.open(new File(indexDirectoryPath));
            indexSearcher = new IndexSearcher(indexDirectory);
            queryParser = new QueryParser(Version.LUCENE_20,
                    "infobox",
                    new StandardAnalyzer(Version.LUCENE_20));
        }catch (Exception e){
            e.getMessage();
        }
    }

    public TopDocs search(Query query) throws IOException, ParseException {
        return indexSearcher.search(query, 10);
    }

    public Document getDocument(ScoreDoc scoreDoc)
            throws CorruptIndexException, IOException{
        return indexSearcher.doc(scoreDoc.doc);
    }

    public boolean searchVerifyInfobox(String queryname){
        initSearch("infobox_vietnam");

        try{

            Term term = new Term("infobox", queryname);
            //PrefixQuery prefixQuery = new PrefixQuery(term);
            //create the term query object
            Query query = new TermQuery(term);
            TopDocs hits = search(query);
            if(hits.scoreDocs.length>0){
                return true;
            }
        }catch (Exception e){
            e.getMessage();
        }
        return false;
    }


    public static void main(String[] args){
        ExtractAllInfoboxVietNamese extractAllInfoboxVietNamese = new ExtractAllInfoboxVietNamese("infobox_vietnam");
//        extractAllInfoboxVietNamese.initSearch("infobox_vietnam");
        extractAllInfoboxVietNamese.getInfoboxName();

//        try{
//            String queryname = "Bảng_phân_loại";
//            Term term = new Term("infobox", queryname);
//            //PrefixQuery prefixQuery = new PrefixQuery(term);
//            //create the term query object
//            Query query = new TermQuery(term);
//            TopDocs hits = extractAllInfoboxVietNamese.search(query);
//            for(ScoreDoc scoreDoc : hits.scoreDocs) {
//                Document doc = extractAllInfoboxVietNamese.getDocument(scoreDoc);
//                String p = doc.get("infobox");
//                //if(p.equals(queryname)){
//                System.out.println(doc.get("infobox"));
//                // }
//            }
//        }catch (Exception e){
//            e.getMessage();
//        }
    }

}
