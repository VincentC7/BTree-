package main;

import arbre.BTree;
import arbre.Node;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            System.out.println("Taille des blocs : ");
            String size = sc.nextLine();
            while(!size.matches("-?\\d+")){
                System.out.println("Erreur : donnez une valeur entiere svp");
                size = sc.nextLine();
            }
            int sizeINT = Integer.parseInt(size);
            BTree<Integer, String> treeAgeName = new BTree<>(sizeINT);
            BTree<String, Integer> treeNameAge = new BTree<>(sizeINT);

            BufferedReader br = new BufferedReader(new FileReader("src/main/arbre.txt"));

            String line;
            while (((line = br.readLine()) != null)){
                String[] splitedLine = line.split("[|]",2);
                String name = splitedLine[0];
                Integer age = Integer.parseInt(splitedLine[1]);

                //Insert
                treeNameAge.insert(name,age);
                treeAgeName.insert(age,name);

                displayStatus(treeAgeName,treeNameAge,name,age+"","Insert");
            }
            br.close();

            while (true){
                System.out.println("Insertion (1) ; Recherche (2) ; Suppression (3) ; Quitter (4)");
                String choice = sc.next();
                switch (choice){
                    case "1":
                        System.out.println("Comment s'appelle la personne que vous souhaitez inserer dans la base de données ?");
                        String name = sc.next();
                        System.out.println("Quel age a cette personne ?");
                        String age = sc.next();
                        while (!age.matches("-?\\d+")){
                            System.out.println("Erreur : Veuillez indiquer une valeur entiere");
                            age = sc.next();
                        }
                        //Insert
                        treeNameAge.insert(name,Integer.parseInt(age));
                        treeAgeName.insert(Integer.parseInt(age),name);

                        //Display consol
                        displayStatus(treeAgeName,treeNameAge,name,age,"Insert");
                        break;
                    case "2":
                        System.out.println("Entrez la clé que vous recherchez");
                        String key = sc.next();
                        String value;
                        if (key.matches("-?\\d+")){ //case key=age
                            value = treeAgeName.findValue(Integer.parseInt(key));
                        }else{ //case key=name
                            value = treeNameAge.findValue(key)+"";
                        }

                        if (value == null || value.equals("null")){
                            System.out.println("Il n'y a aucune valeur associée à la clé que vous avez insérée ");
                        }else{
                            System.out.println(value);
                        }
                        break;
                    case "3":
                        System.out.println("Entrez la clé que vous souhaitez supprimer");
                        String keyDel = sc.next();
                        Object val;
                        if (keyDel.matches("-?\\d+")){ //case key=age
                            val = treeAgeName.delete(Integer.parseInt(keyDel));
                            if (val==null){
                                System.out.println("Valeur introuvable");
                                break;
                            }
                            treeNameAge.delete(val.toString());
                        }else{ //case key=name
                            val = treeNameAge.delete(keyDel);
                            if (val==null){
                                System.out.println("Valeur introuvable");
                                break;
                            }
                            treeAgeName.delete(Integer.parseInt(val.toString()));
                        }
                        displayStatus(treeAgeName,treeNameAge,keyDel,val.toString(),"Delete");
                        break;
                    case "4":
                        return;
                    default:
                        System.out.println("Error : vous n'avez pas respecté les consignes");
                        break;
                }
                System.out.println();
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void displayStatus(BTree tree1, BTree tree2,String name, String age,String action){
        System.out.println(action+" : name="+name+" age="+age);
        System.out.println("##########Key: age, Val: name########");
        System.out.println(tree1+"\n");
        System.out.println("##########Key: name, Val: age########");
        System.out.println(tree2);
        System.out.println("===============================================================================================================");

    }
}
