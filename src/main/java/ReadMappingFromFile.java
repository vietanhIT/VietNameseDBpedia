import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

/**
 * Created by vieta on 5/3/2017.
 */
public class ReadMappingFromFile {
    public static void main(String[] args){
        ReadMappingFromFile readMappingFromFile = new ReadMappingFromFile();
        readMappingFromFile.readFileMapping();
    }

    public static HashMap<String, HashMap<String, String>> readFileMapping(){
        boolean isPart = false;
        HashMap<String, HashMap<String, String>> hashMap = new HashMap<>();
        try{
            FileReader fileReader = new FileReader("Mapping_en.xml");
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String line;
            String title="";
            ArrayList<String> titles = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null){
                if(!line.contains("PropertyMapping")&&!line.contains("GeocoordinatesMapping")){
                    if(line.contains("<title>")){
                        isPart = false;
                        int start = line.indexOf("<title>");
                        int end = line.indexOf("</title>");

                        title  = line.substring(start+7, end);
                        title = title.replace("Mapping en:","").replace(" ","_").toLowerCase();
                        titles.add(title);
                        //System.out.println(title);

                        hashMap.put(title,new HashMap<String, String>());

                    }
                }
                if(line.contains("PropertyMapping")&&line.contains("<title>")){
                    if(line.contains("PropertyMapping")){
                        //System.out.println(line);
                        String templateProperty = "";
                        String ontologyProperty = "";
                        String a[] = line.split(Pattern.quote("|"));
                        for(String b: a){
                            if(b.contains("templateProperty")){
                                if(b.contains("}}")){
                                    b = b.split(Pattern.quote("}}"))[0];
                                }
                                templateProperty = b.replace("templateProperty = ","").trim();
                            }
                            if(b.contains("ontologyProperty")){
                                if(b.contains("}}")){
                                    b = b.split(Pattern.quote("}}"))[0];
                                }
                                ontologyProperty = b.replace("ontologyProperty = ","").trim();
                                //ontologyProperty = b.replace("ontologyProperty = ","").replace("}}","").trim();
                            }
                        }
                        if(!templateProperty.equals("")&&!ontologyProperty.equals("")){
                            //System.out.println(templateProperty + " --- " + ontologyProperty);
                            hashMap.get(title).put(templateProperty,ontologyProperty);
                        }
                    }
                    if(line.contains("<title>")){
                        isPart = false;
                        int start = line.indexOf("<title>");
                        int end = line.indexOf("</title>");

                        title = line.substring(start+7, end);
                        title = title.replace("Mapping en:","").replace(" ","_").toLowerCase();
                        titles.add(title);
                        //System.out.println(title);
                        hashMap.put(title,new HashMap<String, String>());

                    }
                }

                if(line.contains("GeocoordinatesMapping")&&line.contains("<title>")){
                    if(line.contains("GeocoordinatesMapping")){
                        //System.out.println(line);
                        String templateProperty = "";
                        String ontologyProperty = "";
                        String c[] = line.split(Pattern.quote("}}"));
                        String a[] = c[0].split(Pattern.quote("|"));
                        for(String b: a){
                            String d[] = b.split(Pattern.quote("="));
                            if(d.length==2){
                                ontologyProperty = d[0].trim();
                                templateProperty = d[1].trim();
                                if(!templateProperty.equals("")&&!ontologyProperty.equals("")){
                                    //System.out.println(templateProperty + " --- " + ontologyProperty);
                                    hashMap.get(title).put(templateProperty,ontologyProperty);
                                }
                            }
                        }
                    }
                    if(line.contains("<title>")){
                        isPart = false;
                        int start = line.indexOf("<title>");
                        int end = line.indexOf("</title>");

                        title = line.substring(start+7, end);
                        title = title.replace("Mapping en:","").replace(" ","_").toLowerCase();
                        titles.add(title);
                        //System.out.println(title);
                        hashMap.put(title,new HashMap<String, String>());

                    }
                }

                if(!isPart&&(line.contains("| mappings =")||line.contains("| defaultMappings ="))){
                    isPart = true;
                    continue;
                }
                if(isPart){
                    if(line.contains("PropertyMapping")){
                        //System.out.println(line);
                        String templateProperty = "";
                        String ontologyProperty = "";
                        String a[] = line.split(Pattern.quote("|"));
                        for(String b: a){
                            if(b.contains("templateProperty = ")){
                                templateProperty = b.replace("templateProperty = ","").replace("}}","").trim();
                            }
                            if(b.contains("ontologyProperty")){
                                ontologyProperty = b.replace("ontologyProperty = ","").replace("}}","").trim();
                            }
                        }
                        if(!templateProperty.equals("")&&!ontologyProperty.equals("")){
                            //System.out.println(templateProperty + " --- " + ontologyProperty);
                            hashMap.get(title).put(templateProperty,ontologyProperty);
                        }
                    }

                    if(line.contains("GeocoordinatesMapping")){ //GeocoordinatesMapping
                        //System.out.println(line);
                        String templateProperty = "";
                        String ontologyProperty = "";
                        String c[] = line.split(Pattern.quote("}}"));
                        String a[] = c[0].split(Pattern.quote("|"));
                        for(String b: a){
                            String d[] = b.split(Pattern.quote("="));
                            if(d.length==2){
                                ontologyProperty = d[0].trim();
                                templateProperty = d[1].trim();
                                if(!templateProperty.equals("")&&!ontologyProperty.equals("")){
                                    //System.out.println(templateProperty + " --- " + ontologyProperty);
                                    hashMap.get(title).put(templateProperty,ontologyProperty);
                                }
                            }
                        }

                    }
                }

            }

//            System.out.print(titles.size()+"end");
            Set<String> keys = hashMap.keySet();
            for(String a: keys){
                if(titles.contains(a)){
                    titles.remove(a);
                }
            }
//            System.out.print(titles.toString()+"end");
            HashMap<String, String> h123 = hashMap.get("infobox_settlement");
            try{
                bufferedReader.close();
                fileReader.close();
            }catch (Exception e){
                e.getMessage();
            }
        }catch (Exception e){
            e.getMessage();
        }


        return hashMap;
    }
}
