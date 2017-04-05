import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;

import java.io.File;
import java.util.*;

/**
 * Created by vieta on 13/3/2017.
 */
public class MergeResult {

    public static void main(String[] args){
        MergeResult mergeResult = new MergeResult();
        mergeResult.readAllFileExcelResult();
    }

    private final static String MAIN_FOLDER = "template_statistic" + File.separator + "infobox-Thong_tin_don_vi_hanh_chinh-result";
    private void readAllFileExcelResult(){
        try{
            File mainFolder = new File(MAIN_FOLDER);
            File[] listFileName = mainFolder.listFiles();
            HashMap<String, ArrayList<Double>> hashMap = new HashMap<>();
            HashMap<String, Double> averageHashMap = new HashMap<>();
            WorkbookSettings ws = new WorkbookSettings();
            ws.setEncoding("Cp1252");

            for(File a: listFileName){
                if(a.getName().contains("xls")){
                    Workbook w = Workbook.getWorkbook(a,ws);
                    Sheet sheet = w.getSheet(0);
                    for (int i = 0; i < sheet.getRows(); i++) {
                        String attr = sheet.getCell(0,i).getContents().toLowerCase();
                        String pttr = sheet.getCell(1,i).getContents();
                        Double count = Double.valueOf(sheet.getCell(2,i).getContents());
                        String name = attr + ";" + pttr;
                        Set<String> keys = hashMap.keySet();
                        if(keys.contains(name)){
                            hashMap.get(name).add(count);
                        }else{
                            ArrayList<Double> listCount = new ArrayList<>();
                            listCount.add(count);
                            hashMap.put(name, listCount);
                        }
                    }
                }
            }

            Set<String> keys = hashMap.keySet();
            for(String b: keys){
                ArrayList<Double> listCount = hashMap.get(b);
                Double sum = 0.0;
                for(Double c: listCount){
                    sum += c;
                }
                averageHashMap.put(b, sum); //sum/listCount.size()
            }

            HashMap<String, HashMap<String, Double>> mapping = new HashMap<>();
            for(String b: keys){
                String[] list = b.split(";");
                Double similarity = averageHashMap.get(b);

                if(!mapping.containsKey(list[0])){
                    HashMap<String, Double> h3 = new HashMap<>();
                    h3.put(list[1],similarity);
                    mapping.put(list[0], h3);
                }else{
                    mapping.get(list[0]).put(list[1],similarity);
                }
            }

            HashMap<String, HashMap<String, Double>> resultMapping = new HashMap<>();
            Set<String> k3s = mapping.keySet();
            for (String c: k3s){
                resultMapping.put(c, new HashMap<String, Double>());
                HashMap<String, Double> h1 = mapping.get(c);
                Collection<Double> doubles = h1.values();
                Double max = Collections.max(doubles);
                Set<String> k2s = h1.keySet();
                for(String a: k2s){
                    if(h1.get(a).equals(max)){
                        resultMapping.get(c).put(a, max);
                    }
                }

            }

            System.out.println(hashMap.size()+"/m");
            Set<String> keys1 = resultMapping.keySet();
            try {
                WritableWorkbook workbook = Workbook.createWorkbook(new File("template_statistic" + File.separator + "merge_result" + File.separator + "merge1.xls"));
                WritableSheet sheet = workbook.createSheet("Sheet 1", 0);
                int index =0;
                for (String k : keys1) {
                    HashMap<String, Double> h1 = resultMapping.get(k);
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
        }catch (Exception e){
            System.out.println(e.getMessage());
        }
    }
}
