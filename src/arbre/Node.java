package arbre;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node<K extends Comparable,V> {

    private Type type;
    private ArrayList<K> keys;
    private ArrayList<V> values;
    private ArrayList<Node<K, V>> nodes;
    private Node<K,V> parent;
    private Node<K,V> next;
    private Node<K,V> prev;

    /**
     * Constructeur sans parent
     *
     * @param p_type Type of node
     */
    public Node(Type p_type){
        keys = new ArrayList<>();
        nodes = new ArrayList<>();
        values = new ArrayList<>();
        type = p_type;
    }

    /**
     * Constructeur avec parent
     *
     * @param p_type Type of node
     * @param p_parent Parent of node
     */
    private Node(Type p_type, Node<K, V> p_parent){
        keys = new ArrayList<>();
        nodes = new ArrayList<>();
        values = new ArrayList<>();
        type = p_type;
        parent=p_parent;
    }

    /**
     * Search the next node who contain the key
     *
     * @param key searched key
     * @return node contain the key
     */
    public Node<K,V> searchNextNode(K key){
        int index=0;
        for (K k : keys){
            if (k.compareTo(key)<= 0){
                index++;
            }
        }
        return nodes.get(index);
    }

    /**
     * Insert the couple key, val inside this node
     *
     * @param key to insert
     * @param val to insert
     */
    public void insert(K key,V val){
        addKey(key);
        if (type == Type.leaf || type == Type.starter){
            addValue(val, keys.indexOf(key));
        }
        if (keys.size()>BTree.NODE_SIZE){
            split();
        }
    }

    /**
     * Algorithme in charge of spliting a node when it is full
     */
    private void split() {
        //valeur qui remonte
        K upKey = keys.get(BTree.NODE_SIZE / 2 + BTree.NODE_SIZE%2);

        //Cas ou on agit sur le sommet de l'arbre
        if (parent == null){
            Node<K,V> newSon;
            Node<K,V> newSon_2;

            if (type == Type.root){
                newSon = new Node<>(Type.intermediate,this);
                newSon_2 = new Node<>(Type.intermediate,this);
                for (int i=0;i<nodes.size();i++){
                    Node<K,V> n = nodes.get(i);
                    if ((nodes.size()/2 + BTree.NODE_SIZE%2) > i){
                        newSon.addNode(n);
                        n.setParent(newSon);
                    }else{
                        newSon_2.addNode(n);
                        n.setParent(newSon_2);
                    }
                }
            }else{
                newSon = new Node<>(Type.leaf,this);
                newSon_2 = new Node<>(Type.leaf,this);
                newSon.setNext(newSon_2);
                newSon_2.setPrev(newSon);
            }

            for (int i=0;i<keys.size();i++){
                int mid = keys.size()/2;
                if (mid > i){
                    newSon.addKey(keys.get(i));
                    if (newSon.type == Type.leaf)newSon.addValue(values.get(i),i);
                }else{
                    newSon_2.addKey(keys.get(i));
                    if (newSon.type == Type.leaf) newSon_2.addValue(values.get(i),i-mid);
                }
            }

            //Devient Racine
            setType(Type.root);
            //Clear des listes
            values=new ArrayList<>();nodes=new ArrayList<>();keys=new ArrayList<>();

            nodes.add(newSon);
            nodes.add(newSon_2);

            if (newSon_2.getType() == Type.intermediate) newSon_2.keys.remove(upKey);
            //On fait remonter la clé
            addKey(upKey);
        }else {
            Node<K,V> newNode;
            if (type == Type.leaf){
                newNode = new Node<>(Type.leaf,parent);
                newNode.setNext(next);
                if (next!=null) next.setPrev(newNode);
                newNode.setPrev(this);
                next=newNode;
            }else {
                newNode = new Node<>(Type.intermediate,parent);
                List<Node<K,V>> subNods = new ArrayList<>(nodes.subList(nodes.size()/2 + BTree.NODE_SIZE%2,nodes.size()));
                for (Node<K,V> n : subNods){
                    newNode.addNode(n);
                    nodes.remove(n);
                    n.setParent(newNode);
                }
            }

            List<K> subKeys = new ArrayList<>(keys.subList(keys.size()/2,keys.size()));
            List<V> subValues = new ArrayList<>(values.subList(values.size()/2,values.size()));
            for (int i=0; i<subKeys.size();i++){
                K key = subKeys.get(i);
                newNode.addKey(key);
                keys.remove(key);
                if (newNode.type == Type.leaf){
                    V val = subValues.get(i);
                    newNode.addValue(val,i);
                    values.remove(val);
                }
            }

            parent.nodes.add(parent.nodes.indexOf(this)+1,newNode);
            if (newNode.getType() == Type.intermediate) newNode.keys.remove(upKey);
            parent.insert(upKey,null);
        }
    }

    /**
     *
     * @param key
     * @return
     */
    public V findValue(K key){
        if (!keys.contains(key)) return null;
        return values.get(keys.indexOf(key));
    }

    /**
     *
     * @param key
     * @return
     */
    public V delete(K key){
        if (!keys.contains(key)) return null;
        V val = values.get(keys.indexOf(key));
        values.remove(val);
        keys.remove(key);
        if (!hasGoodFillRate(0)){
            redistributeLeaves();
        }
        return val;
    }

    /**
     *
     */
    private void redistributeLeaves(){
        if (prev!=null && prev.hasGoodFillRate(1)){ //Check left
            K key = prev.keys.get(prev.values.size()-1);
            V val = prev.values.get(prev.values.size()-1);
            prev.values.remove(val);
            prev.keys.remove(key);

            insert(key,val);
            if (prev.parent == parent) {
                parent.keys.set(parent.nodes.indexOf(this)-1, keys.get(0));
            }else if (prev.parent.parent != null && parent.parent != null) {
                checkUpperNode(prev,0,key);
            }
        }else if (next!=null && next.hasGoodFillRate(1)){ //Check right
            K key = next.keys.get(0);
            V val = next.values.get(0);
            prev.values.remove(val);
            next.keys.remove(key);

            insert(key,val);
            K upKey = next.keys.get(0);
            if (next.parent == parent) {
                parent.keys.set(parent.nodes.indexOf(this), next.keys.get(0));
            }else if (next.parent.parent != null && parent.parent != null) {
                checkUpperNode(next,1,upKey);
            }
        }else {
            fusion();
        }
    }

    /**
     * Methode qui regarde l'intégrité des clés dans les niveaux intermédiaires/racines
     */
    private void checkUpperNode(Node<K, V> brother, int delta, K upKey) {
        Node<K, V> parentNext = brother.parent.parent;
        Node<K, V> parentThis = parent.parent;
        //Find common node relative to two nodes
        while (parentNext != parentThis) {
            parentNext = parentNext.parent;
            parentThis = parentThis.parent;
        }
        int compt = 0;
        for (K k : parentNext.keys) {
            if (upKey.compareTo(k) <= 0) {
                break;
            }
            compt++;
        }
        parentNext.keys.set(Math.max(compt - delta,0), upKey);
    }

    /**
     *
     */
    private void fusion(){
        if (type==Type.leaf){
            Node<K,V> mergeNode = prev;
            if (prev==null){
                mergeNode=next;
                next.prev=null;
            }else{
                prev.next=next;
                if (next!=null){
                    next.prev=prev;
                    if (prev.parent != parent)checkUpperNode(prev,1,next.keys.get(0));
                }
            }
            for(int i=0;i<keys.size();i++){
                mergeNode.addKey(keys.get(i));
                mergeNode.addValue(values.get(i));
            }
            parent.keys.remove(Math.max(parent.nodes.indexOf(this)-1,0));
            parent.nodes.remove(this);

            if (!parent.hasGoodFillRate(0))parent.redistributeInte();

        }
    }

    private void redistributeInte(){
        if (type!=Type.root){
            int index = parent.nodes.indexOf(this);
            if (index!=0 && (parent.nodes.get(index-1).hasGoodFillRate(1))){

            }else if (index!=parent.nodes.size()-1 && (parent.nodes.get(index+1).hasGoodFillRate(1))){

            }
        }
    }


    /**
     *
     * @param key
     */
    private void addKey(K key){
        keys.add(key);
        Collections.sort(keys);
    }

    /**
     *
     * @param node
     */
    private void addNode(Node<K, V> node){
        nodes.add(node);
    }

    /**
     *
     * @param val
     * @param index
     */
    public void addValue(V val, int index){
        values.add(index,val);
    }

    /**
     *
     * @param value
     */
    private void addValue(V value){
        values.add(value);
    }


    public Double getFillRate(){
        return (double) (BTree.NODE_SIZE / (keys.size()));
    }

    private boolean hasGoodFillRate(int delta){
        DecimalFormat df = new DecimalFormat("#");
        if (type==Type.leaf){
            df.setRoundingMode(RoundingMode.UP);
            return (keys.size()-delta>=Integer.parseInt(df.format((BTree.NODE_SIZE+1)/2)));
        }else {
            df.setRoundingMode(RoundingMode.DOWN);
            return (keys.size()-delta>=Integer.parseInt(df.format((BTree.NODE_SIZE+1)/2)));
        }
    }

    // ==========================================      ToString     ================================================= //

    @Override
    public String toString() {
        StringBuilder bloc = new StringBuilder("[");
        for (int i=0; i<keys.size();i++){
            K key = keys.get(i);
            V value;
            if (!values.isEmpty()){
                value = values.get(i);
                bloc.append(key).append(" : ").append(value).append(",");
            }else{
                bloc.append(key).append(",");
            }
        }
        if (!keys.isEmpty()){
            bloc.replace(bloc.length()-1,bloc.length(),"");
        }
        bloc.append("]");
        return bloc.toString();
    }

    // ========================================== Getters & Setters ================================================= //

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public ArrayList<Node<K, V>> getNodes(){
        return nodes;
    }

    public Node<K, V> getParent() {
        return parent;
    }

    public void setParent(Node<K, V> parent) {
        this.parent = parent;
    }

    public void setNext(Node<K,V> n){
        next=n;
    }

    public Node<K,V> getNext(){
        return next;
    }

    public void setPrev(Node<K, V> prev) {
        this.prev = prev;
    }

    public Node<K, V> getPrev() {
        return prev;
    }
}
