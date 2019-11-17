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
                System.out.println(treeAgeName);
                System.out.println(treeNameAge);
            }
            br.close();

            while (true){
                System.out.println("Insertion (1) ; Recherche (2) ; Suppression (3) ; Affichier arbre age->nom (4) ; Affichier arbre nom->age (5) ; Quitter (6)");
                String choice = sc.next();
                switch (choice){
                    case "1":
                        System.out.println("Insert : donnez une clé");
                        String keyInsert = sc.next();
                        System.out.println("Insert : donnez une valeur");
                        String valInsert = sc.next();
                        //Insert
                        System.out.println("Insert : key="+keyInsert+" val="+valInsert);
                        if (keyInsert.matches("-?\\d+")) { //case key=age
                            treeAgeName.insert(Integer.parseInt(keyInsert),valInsert);
                            System.out.println(treeAgeName);
                        }else{ //case key=name
                            treeNameAge.insert(keyInsert,Integer.parseInt(valInsert));
                            System.out.println(treeNameAge);
                        }
                        //Display consol
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
                            System.out.println(treeAgeName);
                        }else{ //case key=name
                            val = treeNameAge.delete(keyDel);
                            if (val==null){
                                System.out.println("Valeur introuvable");
                                break;
                            }
                            System.out.println(treeNameAge);
                        }
                        break;
                    case "4":
                        System.out.println(treeAgeName);
                        break;
                    case "5":
                        System.out.println(treeNameAge);
                        break;
                    case "6":
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


}
