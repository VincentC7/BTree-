package arbre;

import java.util.ArrayList;

public class BTree<K extends Comparable,V> {

    public static int NODE_SIZE;
    private Node<K,V> root;


    public BTree(int nodeSize){
        NODE_SIZE = nodeSize;
        root = new Node<>(Type.starter);
    }

    public Node<K,V> findNode(K research, Node<K,V> node){
        if (node.getType() == Type.leaf || node.getType() == Type.starter){
            return node;
        }else{
            return findNode(research, node.searchNextNode(research));
        }
    }

    //Insert
    public void insert(K key, V val){
        Node<K,V> n = findNode(key,root);
        n.insert(key,val);
    }

    //Delete

    //Replace

    // ==========================================      ToString     ================================================= //

    @Override
    public String toString() {
        return buildStringTree(getRoot(),"\t");
    }

    public String buildStringTree(Node n, String lvl){
        StringBuilder res = new StringBuilder(n.toString()+"\n");
        ArrayList<Node> sons = n.getNodes();
        for (Node son : sons){
            String sonDisplay = buildStringTree(son, lvl+"\t");
            res.append(lvl).append(sonDisplay);
        }
        return res.toString();
    }

    // ========================================== Getters & Setters ================================================= //

    public Node<K, V> getRoot() {
        return root;
    }

    public void setRoot(Node<K, V> root) {
        this.root = root;
    }
}
