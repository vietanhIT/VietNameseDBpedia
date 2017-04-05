import edu.mit.jwi.Dictionary;
import edu.mit.jwi.IDictionary;
import edu.mit.jwi.item.*;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by vieta on 19/3/2017.
 */
public class TestWordNet {
    public static final String PATH = "WNHOME" + File.separator + "dict";

    public static ArrayList<String> getSynsets(String w) throws IOException {
        // construct the URL to the Wordnet dictionary directory
        ArrayList<String> listSynset = new ArrayList<>();
        URL url = new URL("file", null, PATH);
        // construct the dictionary object and open it
        IDictionary dict = new Dictionary(url);
        dict.open();

        // look up first sense of the word "dog "
        IIndexWord idxWord = dict.getIndexWord(w, POS.NOUN);
        IWordID wordID = idxWord.getWordIDs().get(0);
        IWord word = dict.getWord(wordID);
        ISynset synset = word.getSynset();

        for(IWord a: synset.getWords()){
            listSynset.add(a.getLemma());
            System.out.println(a.getLemma());
        }
        return listSynset;

    }

    public static void main (String[] args){
        TestWordNet testWordNet = new TestWordNet();
        try{
            testWordNet.getSynsets("position");
        }catch (Exception e){
            System.out.println(e.getMessage());
        }

    }

}
