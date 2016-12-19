import java.util.ArrayList;

/**
 * Created by vieta on 23/11/2016.
 */
public class Node {

    private Node parent;
    private String name;
    private ArrayList<Node> listChild;
    private int frequency;
    private int depth;
    private int priority;
    private int maxPriority;

    public Node(){
        //parent = new Node();
        listChild = new ArrayList<>();
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Node getParent() {
        return parent;
    }

    public void setParent(Node parent) {
        this.parent = parent;
    }

    public boolean isParent(){
        if(parent!=null) return true;
        return false;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getMaxPriority() {
        return maxPriority;
    }


    public void setMaxPriority(int max) {
        if(max>maxPriority) this.maxPriority = max;
    }

    public boolean isGreater(int max){
        if(max>priority) return true;
        return false;
    }

    public ArrayList<Node> getListChild() {
        return listChild;
    }

    public void setListChild(ArrayList<Node> listChild) {
        this.listChild = listChild;
    }

    public boolean isChild(){
        if(listChild.size()>0) return true;
        return false;
    }
}
