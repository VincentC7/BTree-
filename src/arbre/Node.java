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
        parent=null;
        next=null;
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
        next=null;
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

    public V findValue(K key){
        if (!keys.contains(key)) return null;
        return values.get(keys.indexOf(key));
    }


    public V delete(K key){
        if (!keys.contains(key)) return null;
        V val = values.get(keys.indexOf(key));
        values.remove(val);
        keys.remove(key);
        if (!hasGoodFillRate(0)){
            redistribute();
        }
        return val;
    }

    private void redistribute(){
        int index = parent.nodes.indexOf(this);
        if (index != 0 && parent.nodes.get(index-1).hasGoodFillRate(1)){ //Check left
            System.err.println("Je peux prendre a gauche");
            Node<K,V> left = parent.nodes.get(index-1);
            K key = left.keys.get(left.values.size()-1);
            V val = left.values.get(left.values.size()-1);
            insert(key,val);
            left.keys.remove(key);
            left.values.remove(val);
            parent.keys.set(index-1,key);
        }else if (next!=null && next.hasGoodFillRate(1)){ //Check right
            System.err.println("Je peux prendre a droite");
        }else {
            fusion();
        }
    }

    private void fusion(){
        System.err.println("FUSION");
    }


    public void addKey(K key){
        keys.add(key);
        Collections.sort(keys);
    }

    public void addNode(Node<K, V> node){
        nodes.add(node);
    }

    public void addValue(V val, int index){
        values.add(index,val);
    }

    public Double getFillRate(){
        return (double) (BTree.NODE_SIZE / (keys.size()));
    }

    public boolean hasGoodFillRate(int delta){
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

}
