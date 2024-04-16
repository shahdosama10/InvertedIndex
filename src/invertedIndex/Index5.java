/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import static java.lang.Math.abs;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.Writer;
import java.lang.reflect.Array;
import java.io.IOException;


import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 *
 * @author ehab
 */
public class Index5 {

    //--------------------------------------------
    int N = 0; // number of documents
    public Map<Integer, SourceRecord> sources;  // store the doc_id and the file name.

    public HashMap<String, DictEntry> index; // THe inverted index

    public String path = "tmp11\\"; // path of the documents
    //--------------------------------------------

    /**
     * constructor for index5
     * initializes the index and sources
     *
     */
    public Index5() {
        sources = new HashMap<Integer, SourceRecord>();
        index = new HashMap<String, DictEntry>();
    }


    /**
     * setter for number of documents
     * @param n number of documents
     */
    public void setN(int n) {
        N = n;
    }


    //---------------------------------------------

    /**
     * function to print posting list
     * @param p the haed of the posting list
     */
    public void printPostingList(Posting p , boolean isPositional) {
        // Iterator<Integer> it2 = hset.iterator();
        System.out.print("[");
        while (p != null) {
            /// -4- **** complete here ****
            // fix get rid of the last comma
            // if the next element is not null
            // print the comma
            if(p.next != null){
                System.out.print("" + p.docId);
                if(isPositional){
                    printPositions(p);
                }
                System.out.print(",");

            }
            // if the next element is null
            // print the docId without the comma
            else{
                System.out.print("" + p.docId );
                if(isPositional){
                    printPositions(p);
                }
            }

            p = p.next;
        }
        System.out.println("]");
    }

    //---------------------------------------------

    private void printPositions(Posting p){
        System.out.print("{ ");
        for(int i = 0; i <p.positions.size(); i++){
            if(i == p.positions.size()-1){
                System.out.print(p.positions.get(i));

            } else{
                System.out.print(p.positions.get(i)+ ",");
            }
        }
        System.out.print(" }");
    }
    /**
     * function to print the index
     */
    public void printDictionary(boolean isPositional) {
        Iterator it = index.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();
            DictEntry dd = (DictEntry) pair.getValue(); // get the DictEntry from the index
            System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "]       =--> "); // print the string and the number of documents
            printPostingList(dd.pList , isPositional); // print posting list
        }
        System.out.println("------------------------------------------------------");
        System.out.println("*** Number of terms = " + index.size()); // print the number of words in the index
    }


    //-----------------------------------------------

    /**
     * Inverted index
     *
     * function to build the inverted index
     * @param files from the disk
     */

    public void buildIndex(String[] files , boolean isBiWord) {  // from disk not from the internet
        int fid = 0; // Initialize document ID counter
        for (String fileName : files) { // Iterate through each file in the array of file names
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) { // Open file for reading
                if (!sources.containsKey(fileName)) { // Check if the file is not already in the sources map
                    // If not, create a new SourceRecord and add it to the sources map
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln; // Variable to store each line read from the file
                int flen = 0; // Initialize the length of the file
                // Read each line from the file until end of file is reached
                while ((ln = file.readLine()) != null) {
                    // Call indexOneLine method to process each line and update index
                    flen += indexOneLine(ln, fid, isBiWord);
                }
                // Update the length of the file in the corresponding SourceRecord
                sources.get(fid).length = flen;
    
            } catch (IOException e) { // Catch IOException if file not found or other I/O error occurs
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++; // Increment document ID counter for the next file
        }
        //   printDictionary(); // to print the dictionary after building the index
    }

    /**
     * helper method to build the inverted index
     * @param ln line from the doc
     * @param fid doc id
     * @return the number of words in the line
     */
    public int indexOneLine(String ln, int fid, boolean isBiWord) {
        int flen = 0; // number of words in the line

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase(); // convert the word to lowercase
            // skip the words that constantly repeated
            if(!isBiWord){
                if (stopWord(word)) {
                    continue;
                }
                word = stemWord(word);
            }
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_freq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }


// =================================================================================================================================

    /**
     * build the bi word index
     * @param files from the disk
     */
    public void buildBIIndex(String[] files) {  // from disk not from the internet
        int fid = 0; // Initialize document ID counter
        for (String fileName : files) { // Iterate through each file in the array of file names
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) { // Open file for reading
                if (!sources.containsKey(fileName)) { // Check if the file is not already in the sources map
                    // If not, create a new SourceRecord and add it to the sources map
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln; // Variable to store each line read from the file
                int flen = 0; // Initialize the length of the file
                // Read each line from the file until end of file is reached
                while ((ln = file.readLine()) != null) {
                    // Call indexOneLine method to process each line and update index
                    // handle case of the last word in the previous line and the first word in the current line
                    flen += indexBiOneLine(ln, fid);


                }
                // Update the length of the file in the corresponding SourceRecord
                sources.get(fid).length = flen;

            } catch (IOException e) { // Catch IOException if file not found or other I/O error occurs
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++; // Increment document ID counter for the next file
        }
        //   printDictionary(); // to print the dictionary after building the index
    }

    /**
     * build the bi word index
     * @param ln
     * @param fid
     * @return
     */
    public int indexBiOneLine(String ln, int fid) {
        int flen = 0; // number of words in the line

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;

        for (int i=0; i<flen; i++) {
            String word = words[i].toLowerCase(); // convert the word to lowercase

            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
            } else {
                index.get(word).last.dtf += 1;
            }
            //set the term_freq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

        }
        return flen;
    }

// =============================================================================================================================

    public void buildPositionalIndex(String[] files) {  // from disk not from the internet
        int fid = 0; // Initialize document ID counter
        for (String fileName : files) { // Iterate through each file in the array of file names
            try (BufferedReader file = new BufferedReader(new FileReader(fileName))) { // Open file for reading
                if (!sources.containsKey(fileName)) { // Check if the file is not already in the sources map
                    // If not, create a new SourceRecord and add it to the sources map
                    sources.put(fid, new SourceRecord(fid, fileName, fileName, "notext"));
                }
                String ln; // Variable to store each line read from the file
                int flen = 0; // Initialize the length of the file
                // Read each line from the file until end of file is reached
                while ((ln = file.readLine()) != null) {
                    // Call indexOneLine method to process each line and update index
                    flen += positionalIndexOneLine(ln, fid, flen);
                }
                // Update the length of the file in the corresponding SourceRecord
                sources.get(fid).length = flen;

            } catch (IOException e) { // Catch IOException if file not found or other I/O error occurs
                System.out.println("File " + fileName + " not found. Skip it");
            }
            fid++; // Increment document ID counter for the next file
        }
        //   printDictionary(); // to print the dictionary after building the index
    }

    public int positionalIndexOneLine(String ln, int fid, int position) {
        int flen = 0; // number of words in the line

        String[] words = ln.split("\\W+");
        //   String[] words = ln.replaceAll("(?:[^a-zA-Z0-9 -]|(?<=\\w)-(?!\\S))", " ").toLowerCase().split("\\s+");
        flen += words.length;
        for (String word : words) {
            word = word.toLowerCase(); // convert the word to lowercase
            // skip the words that constantly repeated
//            if (stopWord(word)) {
//                continue;
//            }
//            word = stemWord(word);
            // check to see if the word is not in the dictionary
            // if not add it
            if (!index.containsKey(word)) {
                index.put(word, new DictEntry());
            }
            // add document id to the posting list
            if (!index.get(word).postingListContains(fid)) {
                index.get(word).doc_freq += 1; //set doc freq to the number of doc that contain the term
                if (index.get(word).pList == null) {
                    index.get(word).pList = new Posting(fid);
                    index.get(word).last = index.get(word).pList;
                } else {
                    index.get(word).last.next = new Posting(fid);
                    index.get(word).last = index.get(word).last.next;
                }
                index.get(word).last.positions.add(position);

            } else {
                index.get(word).last.dtf += 1;
                index.get(word).last.positions.add(position);
            }
            //set the term_freq in the collection
            index.get(word).term_freq += 1;
            if (word.equalsIgnoreCase("lattice")) {

                System.out.println("  <<" + index.get(word).getPosting(1) + ">> " + ln);
            }

            position++;

        }
        return flen;
    }



    //----------------------------------------------------------------------------



//----------------------------------------------------------------------------

    /**
     * function to skip the words that constantly repeated
     * @param word the word to test it
     * @return true if the word is repeated and false otherwise
     */
    boolean stopWord(String word) {
        // if the word is one of the following skip it
        if (word.equals("the") || word.equals("to") || word.equals("be") || word.equals("for") || word.equals("from") || word.equals("in")
                || word.equals("a") || word.equals("into") || word.equals("by") || word.equals("or") || word.equals("and") || word.equals("that")) {
            return true;
        }
        // if the length of the word is less than 2 skip it
        if (word.length() < 2) {
            return true;
        }
        return false;

    }
//----------------------------------------------------------------------------  

    String stemWord(String word) { //skip for now
        return word;
//        Stemmer s = new Stemmer();
//        s.addString(word);
//        s.stem();
//        return s.toString();
    }

    //----------------------------------------------------------------------------

    /**
     * function to get the intersection of two posting lists
     * @param pL1 the head of the first posting list
     * @param pL2 the head of the second posting list
     * @return the intersection of the two posting lists as Posting
     */
    Posting intersect(Posting pL1, Posting pL2) {
///****  -1-   complete after each comment ****
//   INTERSECT ( p1 , p2 )
//          1  answer ←      {}
        Posting answer = null;
        Posting last = null;
//      2 while p1  != NIL and p2  != NIL
        while (pL1 != null && pL2 != null) {
//          3 do if docID ( p 1 ) = docID ( p2 )
            if (pL1.docId == pL2.docId) {
//          4   then ADD ( answer, docID ( p1 ))
                if (answer == null) {
                    answer = new Posting(pL1.docId);
                    last = answer;
                } else {
                    last.next = new Posting(pL1.docId);
                    last = last.next;
                }
//          5       p1 ← next ( p1 )
                pL1 = pL1.next;
//          6       p2 ← next ( p2 )
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) {
//          7   else if docID ( p1 ) < docID ( p2 )
//          8        then p1 ← next ( p1 )
                pL1 = pL1.next;
            } else {
//          9        else p2 ← next ( p2 )
                pL2 = pL2.next;
            }
        }
        return answer;
    }

    /**
     * intersect of the positional index
     * @param pL1
     * @param pL2
     * @return
     */
    Posting PositionalIntersect(Posting pL1, Posting pL2, int k) {
        Posting answer = null;
        Posting last = null;
        while (pL1 != null && pL2 != null) {
            if (pL1.docId == pL2.docId) {
                List<Integer> pp1 = pL1.positions;
                List<Integer> pp2 = pL2.positions;
                int i = 0;
                int j = 0;
                List<Integer> positions = new ArrayList<>();
                while (i < pp1.size() && j < pp2.size()) {
                    if (pp1.get(i) + k == pp2.get(j)) {
                        System.out.println("Term 1 positions: " + pp1.get(i) + " Term 2 positions: " + pp2.get(j));
                        positions.add(pp2.get(j));
                        i++;
                        j++;
                    } else if (pp1.get(i) < pp2.get(j)) {
                        i++;
                    } else {
                        j++;
                    }
                }
                if(!positions.isEmpty()) {
                    if (answer == null) {
                        answer = new Posting(pL1.docId);
                        last = answer;
                    } else {
                        last.next = new Posting(pL1.docId);
                        last = last.next;
                    }
                    last.positions = positions;
                } 
                pL1 = pL1.next;
                pL2 = pL2.next;
            } else if (pL1.docId < pL2.docId) {
                pL1 = pL1.next;
            } else {
                pL2 = pL2.next;
            }
        }
        return answer;
    }


// =================================================================================================================

    /**
     * function that take the phrase and return the documents that contain the words in the phrase
     * @param phrase the phrase to search for documents
     * @return the documents that contain the words in the phrase or not found if all the words are not found in the index
     *         or not matching if there is no intersection between the words in the phrase
     *
     */
    public String find_24_01(String phrase, boolean isPositional) { // any mumber of terms non-optimized search
        String result = "";


        String[] words = phrase.split("\\W+");
        int len = words.length;
        boolean found = false;

        //fix this if word is not in the hash table will crash...
        for(String word: words){
            if(index.containsKey(word.toLowerCase())){
                found = true;
            }
        }
        //check if the whole phrase is not in the index return not found
        if(!found){
            return "Not found";
        }

        Posting posting = null;
        int i = 0;
        while (i < len) {
            //skip the word if it is not found
            if (!index.containsKey(words[i].toLowerCase())) {
                i++;
                continue;
            }

            // if the posting is null that mean this is the first word from the phrase that appears in the index
            if(posting == null)
                posting = index.get(words[i].toLowerCase()).pList;
                // else get the intersection between them
            else
            {
                if(isPositional){
                    posting = PositionalIntersect(posting, index.get(words[i].toLowerCase()).pList, 1);
                }else{
                    posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
                }
                if(posting == null)
                    return "No matching";
            }
            i++;
        }
        // if posting is not null print the posting list with doc id , title and the length of the document
        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }


    /**
     * find for the bi word index
     * @param phrase
     * @return
     */

    public String find_24_01_BiWord(String phrase) { // any mumber of terms non-optimized search
        String result = "";


        String[] words = phrase.split("\\W+");
        int len = words.length;
        boolean found = false;
        
        //fix this if word is not in the hash table will crash...
        for(String word: words){
            if(index.containsKey(word.toLowerCase())){
                found = true;
            }
        }
        //check if the whole phrase is not in the index return not found
        if(!found){
            return "Not found";
        }

        Posting posting = null;
        int i = 0;
        while (i < len) {
            //skip the word if it is not found
            if (!index.containsKey(words[i].toLowerCase())) {
                i++;
                continue;
            }

            // if the posting is null that mean this is the first word from the phrase that appears in the index
            if(posting == null)
                posting = index.get(words[i].toLowerCase()).pList;
            // else get the intersection between them
            else
            {
                posting = intersect(posting, index.get(words[i].toLowerCase()).pList);
                if(posting == null)
                    return "No matching";
            }
            i++;
        }
        // if posting is not null print the posting list with doc id , title and the length of the document
        while (posting != null) {
            //System.out.println("\t" + sources.get(num));
            result += "\t" + posting.docId + " - " + sources.get(posting.docId).title + " - " + sources.get(posting.docId).length + "\n";
            posting = posting.next;
        }
        return result;
    }


    
    
    //---------------------------------

    /**
     * function to sort list of words using bubble sort
     * @param words
     * @return the sorted list
     */
    String[] sort(String[] words) {  //bubble sort
        boolean sorted = false;
        String sTmp;
        //-------------------------------------------------------
        while (!sorted) {
            sorted = true;
            for (int i = 0; i < words.length - 1; i++) {
                int compare = words[i].compareTo(words[i + 1]);
                if (compare > 0) {
                    sTmp = words[i];
                    words[i] = words[i + 1];
                    words[i + 1] = sTmp;
                    sorted = false;
                }
            }
        }
        return words;
    }

     //---------------------------------

    /**
     * function to take the name of the index and create the file of the index from index hash map
     * @param storageName the name of the index
     */
    public void store(String storageName) {
        try {
            String pathToStorage = path+"rl\\"+storageName;
            Writer wr = new FileWriter(pathToStorage);
            for (Map.Entry<Integer, SourceRecord> entry : sources.entrySet()) {
                System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue().URL + ", Value = " + entry.getValue().title + ", Value = " + entry.getValue().text);
                wr.write(entry.getKey().toString() + ",");
                wr.write(entry.getValue().URL.toString() + ",");
                wr.write(entry.getValue().title.replace(',', '~') + ",");
                wr.write(entry.getValue().length + ","); //String formattedDouble = String.format("%.2f", fee );
                wr.write(String.format("%4.4f", entry.getValue().norm) + ",");
                wr.write(entry.getValue().text.toString().replace(',', '~') + "\n");
            }
            wr.write("section2" + "\n");

            Iterator it = index.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                DictEntry dd = (DictEntry) pair.getValue();
                //  System.out.print("** [" + pair.getKey() + "," + dd.doc_freq + "] <" + dd.term_freq + "> =--> ");
                wr.write(pair.getKey().toString() + "," + dd.doc_freq + "," + dd.term_freq + ";");
                Posting p = dd.pList;
                while (p != null) {
                    //    System.out.print( p.docId + "," + p.dtf + ":");
                    wr.write(p.docId + "," + p.dtf + ":");
                    p = p.next;
                }
                wr.write("\n");
            }
            wr.write("end" + "\n");
            wr.close();
            System.out.println("=============EBD STORE=============");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//=========================================

    /**
     * function to check if the storage name is exist or not
     * @param storageName the name of the storage
     * @return true if the storage name is exist or false otherwise
     */
    public boolean storageFileExists(String storageName){
        java.io.File f = new java.io.File(path+"rl\\"+storageName);
        if (f.exists() && !f.isDirectory())
            return true;
        return false;
            
    }
//----------------------------------------------------

    /**
     * function to create the file for the storage
     * @param storageName the name of the storage
     */
    public void createStore(String storageName) {
        try {
            String pathToStorage = path+storageName;
            Writer wr = new FileWriter(pathToStorage);
            wr.write("end" + "\n");
            wr.close();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
//----------------------------------------------------      
     //load index from hard disk into memory
    public HashMap<String, DictEntry> load(String storageName) {
        try {
            String pathToStorage = path+"rl\\"+storageName;
            sources = new HashMap<Integer, SourceRecord>();
            index = new HashMap<String, DictEntry>();
            BufferedReader file = new BufferedReader(new FileReader(pathToStorage));
            String ln = "";
            int flen = 0;
            while ((ln = file.readLine()) != null) {
                if (ln.equalsIgnoreCase("section2")) {
                    break;
                }
                String[] ss = ln.split(",");
                int fid = Integer.parseInt(ss[0]);
                try {
                    System.out.println("**>>" + fid + " " + ss[1] + " " + ss[2].replace('~', ',') + " " + ss[3] + " [" + ss[4] + "]   " + ss[5].replace('~', ','));

                    SourceRecord sr = new SourceRecord(fid, ss[1], ss[2].replace('~', ','), Integer.parseInt(ss[3]), Double.parseDouble(ss[4]), ss[5].replace('~', ','));
                    //   System.out.println("**>>"+fid+" "+ ss[1]+" "+ ss[2]+" "+ ss[3]+" ["+ Double.parseDouble(ss[4])+ "]  \n"+ ss[5]);
                    sources.put(fid, sr);
                } catch (Exception e) {

                    System.out.println(fid + "  ERROR  " + e.getMessage());
                    e.printStackTrace();
                }
            }
            while ((ln = file.readLine()) != null) {
                //     System.out.println(ln);
                if (ln.equalsIgnoreCase("end")) {
                    break;
                }
                String[] ss1 = ln.split(";");
                String[] ss1a = ss1[0].split(",");
                String[] ss1b = ss1[1].split(":");
                index.put(ss1a[0], new DictEntry(Integer.parseInt(ss1a[1]), Integer.parseInt(ss1a[2])));
                String[] ss1bx;   //posting
                for (int i = 0; i < ss1b.length; i++) {
                    ss1bx = ss1b[i].split(",");
                    if (index.get(ss1a[0]).pList == null) {
                        index.get(ss1a[0]).pList = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).pList;
                    } else {
                        index.get(ss1a[0]).last.next = new Posting(Integer.parseInt(ss1bx[0]), Integer.parseInt(ss1bx[1]));
                        index.get(ss1a[0]).last = index.get(ss1a[0]).last.next;
                    }
                }
            }
            System.out.println("============= END LOAD =============");
            //    printDictionary();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return index;
    }
}

//=====================================================================
