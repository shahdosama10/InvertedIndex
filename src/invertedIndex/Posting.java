/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package invertedIndex;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ehab
 */

public class Posting {

    public Posting next = null;
    int docId;

    List<Integer> positions; // to store the positions of the word in this document
    int dtf = 1;

    Posting(int id, int t) {
        docId = id;
        dtf=t;
        positions = new ArrayList<Integer>();
    }

    Posting(int id) {
        docId = id;
        positions = new ArrayList<Integer>();
    }
}