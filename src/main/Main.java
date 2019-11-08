package main;

import arbre.BTree;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class Main {

    public static void main(String[] args) {
        try {
            Scanner sc = new Scanner(System.in);
            //System.out.println("Taille des blocs : ");
            BTree<String , Integer> tree = new BTree<>(3);

            BufferedReader br = new BufferedReader(new FileReader("src/main/arbre.txt"));
            String line;
            while (((line = br.readLine()) != null)){
                String[] splitedLine = line.split("[|]",2);
                String key = splitedLine[0];
                Integer value = Integer.parseInt(splitedLine[1]);
                tree.insert(key,value);
                System.out.println("Insertion : clé="+key+" valeur"+value);
                System.out.println(tree);
                System.out.println("===============================================================================================================");
            }
            br.close();

            /*
            while (true){
                System.out.println("Insertion : Veuillez indiquer une valeur entiere (Pour quitter tapez q)");
                String val = sc.nextLine();
                while (!val.matches("-?\\d+")){
                    if (val.equals("q") || val.equals("Q")) return;
                    System.out.println("Erreur : Veuillez indiquer une valeur entiere (Pour quitter tapez q)");
                    val = sc.nextLine();
                }
                System.out.println(tree);
                System.out.println("========================================================================================================================\n");
            }
            */
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
