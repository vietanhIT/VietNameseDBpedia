import java.io.PrintWriter;

/**
 * Created by vieta on 24/11/2016.
 */
public class WriteFile {
    PrintWriter printWriter;

    public WriteFile(){
        try{
            printWriter = new PrintWriter(Constants.RESULT_FILE_NAME,"UTF-8");
        }catch (Exception e){
            e.getMessage();
        }
    }

    public WriteFile(String name){
        try{
            printWriter = new PrintWriter(name,"UTF-8");
        }catch (Exception e){
            e.getMessage();
        }
    }

    public void write1Line(String mes){
        printWriter.println(mes);
    }

    public void close(){
        printWriter.close();
    }

    public static void main(String args[]){
        try{
            WriteFile writeFile = new WriteFile();
            for(int i=0; i<100; i++){
                writeFile.write1Line("hello");
            }

            writeFile.close();
        }catch (Exception e){
            e.getMessage();
        }
    }
}
