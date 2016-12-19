import org.apache.log4j.Logger;
import org.fbk.cit.hlt.thewikimachine.util.AirpediaOntology;
import org.fbk.cit.hlt.thewikimachine.util.DBpediaOntologyNode;

import java.util.ArrayList;

/**
 * Created by vieta on 22/11/2016.
 */
public class SearchAllParentClass {
    AirpediaOntology airpediaOntology;

    public SearchAllParentClass(){
        airpediaOntology = new AirpediaOntology(Constants.ONTOLOGY_2016);
    }

    public ArrayList<String> search(String className){
        ArrayList<String> listClass = new ArrayList<>();
        ArrayList<DBpediaOntologyNode> arrayList = airpediaOntology.getHistoryFromName(className);
        try{
            for(int i=0;i<arrayList.size();i++){
                //System.out.print("----------------"+ className);
                //System.out.println(arrayList.get(i).className);
                listClass.add(arrayList.get(i).className);
            }
        }catch (Exception e){
            e.getMessage();
        }
        return listClass;
    }

    public static void main(String [] args){
        Logger logger = Logger.getLogger(SearchAllParentClass.class.getName());
        String owlFile = "dbpedia_2016-04.owl";
        AirpediaOntology airpediaOntology = new AirpediaOntology(owlFile);
        ArrayList<DBpediaOntologyNode> arrayList = airpediaOntology.getHistoryFromName("Airline");
        try{
            for(int i=0;i<arrayList.size();i++){
                logger.debug(arrayList.get(i).className);
            }
        }catch (Exception e){
            e.getMessage();
        }
    }
}
