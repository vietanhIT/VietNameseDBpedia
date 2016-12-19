import jxl.Sheet;
import jxl.Workbook;
import jxl.write.Formula;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.Number;

import java.io.File;

/**
 * Created by vieta on 1/12/2016.
 */
public class WriteFileExcel {

    public static void main(String args[]){
        try{
            WritableWorkbook workbook = Workbook.createWorkbook(new File("result.xls"));
            WritableSheet sheet = workbook.createSheet("Sheet 1", 0);

            sheet.addCell(new Label(0, 0, "Add a String to cell")); // add a String to cell A1
            sheet.addCell(new Number(1, 0, 100)); // add number 100 to cell A2

            workbook.write();
            workbook.close();
        }catch (Exception e){
            e.getMessage();
        }


    }

}
