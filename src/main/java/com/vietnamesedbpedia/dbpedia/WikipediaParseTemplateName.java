package com.vietnamesedbpedia.dbpedia;

import org.fbk.cit.hlt.thewikimachine.xmldump.util.WikiTemplate;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by vieta on 29/11/2016.
 */
public class WikipediaParseTemplateName {
    static int start;
    static int end;

    public static String nameTemplate(String content){
        String name="NULL";
        start = -1;
        end = 0;
        int max = content.length();

        for(int i=0; i<max-1;i++){
            char c1, c2;
//            int j = i;
//
//            while ((Character.toString(content.charAt(j)).trim()).length() == 0) {
//                j++;
//                i++;
//                if (j > max - 1) {
//                    break;
//                }
//            }
//            if (j > max - 1) {
//                break;
//            }
            c1 = content.charAt(i);
//            j++;
//            if (j > max - 1) {
//                break;
//            }
//            while ((Character.toString(content.charAt(j)).trim()).length() == 0) {
//                j++;
//                i++;
//                if (j > max - 1) {
//                    break;
//                }
//            }
            if (i> max - 1) {
                break;
            }
            c2 = content.charAt(i+1);

            //System.out.println(i + "---" + "c1: "+ c1 +"---" +"c2: " + c2);

            if(c1 == '{' && c2=='{'){
                start = i;
            }
            if(c1 == '}'){
                start = -1;
            }
            if(c1 == '|'){
                end = i;
                break;
            }
        }
        if(end>start&&start>=0){
            try{
                name  = content.substring(start+2, end);
            }catch (Exception e){

            }
        }
        return name;
    }

    public static String infoboxName="";

    public static ArrayList<WikiTemplate> parse(String content, boolean stopOnText) {

        // Get templates before text
        ArrayList<WikiTemplate> listOfTemplates = new ArrayList<WikiTemplate>();
        ArrayList<Integer> start = new ArrayList<Integer>();
        ArrayList<Integer> count = new ArrayList<Integer>();
        HashMap<Integer, ArrayList<WikiTemplate>> templatesOnLevel = new HashMap<Integer, ArrayList<WikiTemplate>>();

        templatesOnLevel.put(0, new ArrayList<WikiTemplate>());

        int max = content.length();
        for (int i = 0; i < max - 1; i++) {
            char c1, c2;
            int j = i;

            while ((Character.toString(content.charAt(j)).trim()).length() == 0) {
                j++;
                i++;
                if (j > max - 1) {
                    break;
                }
            }
            if (j > max - 1) {
                break;
            }
            c1 = content.charAt(j);
            j++;
            if (j > max - 1) {
                break;
            }
            while ((Character.toString(content.charAt(j)).trim()).length() == 0) {
                j++;
                i++;
                if (j > max - 1) {
                    break;
                }
            }
            if (j > max - 1) {
                break;
            }
            c2 = content.charAt(j);

            if (c1 == '{' && c2 == '{') {
                start.add(i);
                int s = count.size();
                if (s > 0) {
                    count.set(s - 1, count.get(s - 1) + 1);
                }
                count.add(0);
                templatesOnLevel.put(s + 1, new ArrayList<WikiTemplate>());
                i++;
            }
            if (c1 == '}' && c2 == '}') {
                if (start.size() > 0) {
                    int myStart = start.get(start.size() - 1);
                    int myEnd = i + 1;
                    int includedTemplates = count.get(count.size() - 1);
                    String myContent = content.substring(myStart + 2, myEnd - 1).trim();

                    WikiTemplate t = new WikiTemplate(myStart, myEnd, myContent, includedTemplates);
                    t.setChildren(templatesOnLevel.get(count.size()));

                    if (count.size() == 1) {
                        t.isRoot = true;
                    }
                    listOfTemplates.add(t);
                    templatesOnLevel.get(count.size() - 1).add(t);

/*
					System.out.println(start);
                    System.out.println(countPageCounter);
                    System.out.println(myStart);
                    System.out.println(myEnd);
                    System.out.println(myContent);
                    System.out.println(templatesOnLevel);
                    System.out.println(t);
                    System.exit(0);
*/

                    start.remove(start.size() - 1);
                    count.remove(count.size() - 1);
                }
                i++;
            }

            String myChar = Character.toString(c1);
            if (stopOnText && myChar.trim().length() > 0 && start.size() == 0 && c1 != '}') {
                break;
            }
            // System.out.print(c);
        }

        for (WikiTemplate c : listOfTemplates) {
            String templateContent = c.getContent();
            int level = 0;
            int fromChar = 0;
            ArrayList<String> parts = new ArrayList<String>();

            for (int i = 0; i < templateContent.length() - 1; i++) {
                char c1 = templateContent.charAt(i);
                char c2 = templateContent.charAt(i + 1);
                if (c1 == '{' && c2 == '{') {
                    level++;
                    i++;
                }
                if (c1 == '[' && c2 == '[') {
                    level++;
                    i++;
                }
                if (c1 == '}' && c2 == '}') {
                    level--;
                    i++;
                }
                if (c1 == ']' && c2 == ']') {
                    level--;
                    i++;
                }
                if (c1 == '|' && level == 0) {
                    if (i > fromChar) {
                        parts.add(templateContent.substring(fromChar, i).trim());
                        infoboxName = templateContent.substring(fromChar, i).trim();
                    }
                    fromChar = i + 1;
                }
            }
//            if (templateContent.substring(fromChar).trim().length() > 0) {
//                parts.add(templateContent.substring(fromChar));
//            }
            c.setParts(parts);
        }

        return listOfTemplates;
    }

    public static ArrayList<WikiTemplate> getTemplates(String content) {
        return parse(content, true);
    }

    public static ArrayList<WikiTemplate> getTemplatesFromFile(String filename) {
        return getTemplatesFromFile(filename, true);
    }

    public static ArrayList<WikiTemplate> getTemplatesFromFile(String filename, boolean stopOnText) {
        StringBuilder strbuf = new StringBuilder();

        // Read filePageCounter
        try {
            BufferedReader in = new BufferedReader(new FileReader(filename));
            String line;
            while ((line = in.readLine()) != null) {
                line = line.trim();
                strbuf.append(line).append("\n");
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return parse(strbuf.toString(), stopOnText);
    }

    public static void main(String args[]){
        String content = "'''VIQR''' (viết tắt của [[tiếng Anh]] '''Vietnamese Quoted-Readable'''), còn gọi là '''Vietnet''' là một quy ước để viết chữ [[tiếng Việt]] dùng bảng mã [[ASCII]] 7 bit. Vì tính tiện lợi của nó, quy ước này được sử dụng phổ biến trên [[Internet]], nhất là khi bảng mã [[Unicode]] chưa được áp dụng rộng rãi. Hiện nay quy ước VIQR vẫn còn được một số người hay nhóm thư sử dụng.\n" +
                "\n" +
                "== Quy tắc ==\n" +
                "\n" +
                "Quy ước VIQR sử dụng ký tự có trên bàn phím để biểu thị [[dấu]]:\n" +
                "\n" +
                "{| class=&quot;wikitable&quot; style=&quot;width: 60%&quot;\n" +
                "|+ style=&quot;font-weight: bold&quot; | Quy ước dấu trong VIQR\n" +
                "! Dấu !! Ký hiệu !! Ví dụ\n" +
                "|-\n" +
                "| trăng\t|| &lt;code&gt;(&lt;/code&gt;\t|| a(→ &lt;big&gt;ă&lt;/big&gt;\n" +
                "|-\n" +
                "| mũ\t|| &lt;code&gt;^&lt;/code&gt;\t|| a^ → &lt;big&gt;â&lt;/big&gt;\n" +
                "|-\n" +
                "| râu\t|| &lt;code&gt;+ hoặc *&lt;/code&gt;\t|| o+ →  &lt;big&gt;ơ&lt;/big&gt;\n" +
                "|-\n" +
                "| huyền\t|| &lt;code&gt;`&lt;/code&gt;\t|| a` → &lt;big&gt;à&lt;/big&gt;\n" +
                "|-\n" +
                "| sắc\t|| &lt;code&gt;'&lt;/code&gt;\t|| a' → &lt;big&gt;á&lt;/big&gt;\n" +
                "|-\n" +
                "| hỏi\t|| &lt;code&gt;?&lt;/code&gt;\t|| a? → &lt;big&gt;ả&lt;/big&gt;\n" +
                "|-\n" +
                "| ngã\t|| &lt;code&gt;~&lt;/code&gt;\t|| a~ → &lt;big&gt;ã&lt;/big&gt;\n" +
                "|-\n" +
                "| nặng\t|| &lt;code&gt;.&lt;/code&gt;\t|| a. → &lt;big&gt;ạ&lt;/big&gt;\n" +
                "|-\n" +
                "| đ\t|| &lt;code&gt;dd&lt;/code&gt;\t|| \n" +
                "|- \n" +
                "|}\n" +
                "\n" +
                "Một ví dụ của VIQR: ''Việt Nam đất nước mến yêu'' =&gt; &lt;code&gt;''Vie^.t Nam dda^'t nu*o*'c me^'n ye^u''&lt;/code&gt;\n" +
                "\n" +
                "Quy ước VIQR dùng &lt;code&gt;DD&lt;/code&gt; cho chữ ''Đ'', và &lt;code&gt;dd&lt;/code&gt; cho ''đ''. Dấu cách &lt;code&gt;\\&lt;/code&gt; được dùng trước dấu chấm câu (&lt;code&gt;.&lt;/code&gt;) (&lt;code&gt;?&lt;/code&gt;) nếu dấu chấm câu này đặt ngay sau nguyên âm và trong từ có nguồn gốc nước ngoài.\n" +
                "\n" +
                "Ví dụ:\n" +
                "\n" +
                ": &lt;code&gt;O^ng te^n gi`\\? To^i te^n la` Ted\\dy Thu.y\\.&lt;/code&gt; \n" +
                ": Ông tên gì?  Tôi tên là Teddy Thụy.\n" +
                "\n" +
                "Một biến thể của quy ước VIQR là VIQR*. Trong đó, dấu &lt;code&gt;*&lt;/code&gt; được dùng thay cho dấu &lt;code&gt;+&lt;/code&gt; để bỏ dấu móc.\n" +
                "\n" +
                "== Lịch sử ==\n" +
                "Quy ước VIQR đã được dùng tại [[miền Nam]] trước 1975 trong việc lưu giữ các tài liệu của quân đội. Năm 1992, được chuẩn hóa bởi [http://www.vnet.org/vietstd/ Nhóm Viet-Std] (Vietnamese-Standard Working Group - Nhóm Nghiên cứu Tiêu chuẩn Tiếng Việt) thuộc TriChlor group tại [[California]].\n" +
                "\n" +
                "Lối viết này hiện nay cũng được dùng thường xuyên trên [[mạng]], khi ''chat'', vì tiện lợi, không cần dùng [[phần mềm]] nào cả và có thể dùng mọi lúc mọi nơi.\n" +
                "\n" +
                "==Xem thêm==\n" +
                "*[[Mnemonic Encoding Specification for Vietnamese]]\n" +
                "*[[VISCII]]\n" +
                "*[[VNI]]\n" +
                "\n" +
                "==Tham khảo==\n" +
                "{{tham khảo}}\n" +
                "{{tham khảo}}\n" +
                "{{tham khảo}}\n" +
                "{{tham khảo}}\n" +
                "==Liên kết ngoài==\n" +
                "*[http://vietpali.sf.net/binh/KieuGoDauChuVietNhanhNhat.htm Kiểu gõ dấu chữ Việt nhanh nhất]\n" +
                "*[http://vietunicode.sourceforge.net/charset/viqr.html The VIQR Convention]\n" +
                "*RFC 1456&amp;nbsp;– Conventions for Encoding the Vietnamese com.vietnamesedbpedia.dbpedia.Language (VISCII và VIQR)\n" +
                "\n" +
                "[[Thể loại:Mã kí tự]]\n" +
                "[[Thể loại:Kiểu gõ chữ Việt]]";
        nameTemplate(content);
        System.out.println(start+"---"+end);
//        if(end>start){
//            String name  = content.substring(start+2, end);
//            System.out.println(name);
//        }
        for(int i=0; i<57; i++){
            System.out.println(i+"---"+nameTemplate(content));
        }
    }
}
