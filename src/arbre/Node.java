package arbre;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Node<K extends Comparable,V> {

    private Type type;
    private ArrayList<K> keys;
    private ArrayList<V> values;
    private ArrayList<Node<K, V>> nodes;
    private Node<K,V> parent;

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

            //On fait remonter la clé
            if (newSon_2.getType() == Type.intermediate) newSon_2.keys.remove(upKey);
            addKey(upKey);
        }else {
            Node<K,V> newNode;
            if (type == Type.leaf){
                newNode = new Node<>(Type.leaf,parent);
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


    public void delete(K key){
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

    public boolean hasGoodFillRate(){
        if (type==Type.leaf){
        }
        return true;
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

}
