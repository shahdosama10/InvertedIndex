/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Scanner;

/**
 *
 * @author ehab
 */
public class Test {

    public static void main(String args[]) throws IOException {
        Index5 index = new Index5();
        //|**  change it to your collection directory
        //|**  in windows "C:\\tmp11\\rl\\collection\\"
        String files = "tmp11\\rl\\collection\\";

        File file = new File(files);
        //|** String[] 	list()
        //|**  Returns an array of strings naming the files and directories in the directory denoted by this abstract pathname.
        String[] fileList = file.list();

        fileList = index.sort(fileList);
        index.N = fileList.length;

        for (int i = 0; i < fileList.length; i++) {
            fileList[i] = files + fileList[i];
        }

        System.out.println("what is the type of the index would you like to use?");
        System.out.println("1- inverted index");
        System.out.println("2- Bi word index");
        System.out.println("3- Positional index");
        System.out.println("Enter number from (1 , 2 , 3)");
        int type;
        Scanner input = new Scanner(System.in);
        type = input.nextInt();
        while (!(type <=3 && type >=1)){
            System.out.println("Please enter a number between 1 and 3");
            type = input.nextInt();
        }

        if(type == 1){
            index.buildIndex(fileList , false);
            index.printDictionary(false);
        } else if(type == 2){
            index.buildIndex(fileList , true);
            index.buildBIIndex(fileList);
            index.printDictionary(false);
        } else if(type == 3){
            index.buildPositionalIndex(fileList);
            index.printDictionary(true);
        }


//
        index.store("index");
//        index.printDictionary();

//        String test3 = "data should plain greatest comif"; // data  should plain greatest comif
//        System.out.println("Boo0lean Model result = \n" + index.find_24_01(test3));

        String phrase = "";

        do {
            System.out.println("Print search phrase: ");
            BufferedReader in = new BufferedReader(new InputStreamReader(System.in));

            phrase = in.readLine();
            if (phrase.isEmpty()) {
                break;
            } else {
                if(type == 1) {
                    System.out.println("Boo0lean Model result = \n" + index.find_24_01(phrase, false));
                } else if (type == 2) {
                    System.out.println("Boo0lean Model result = \n" + index.find_24_01_BiWord(phrase));
                } else if (type == 3) {
                    System.out.println("Boo0lean Model result = \n" + index.find_24_01(phrase, true));
                }
            }

        } while (!phrase.isEmpty());

    }
}
