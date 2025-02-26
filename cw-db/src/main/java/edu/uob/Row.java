package edu.uob;

import java.util.ArrayList;
import java.util.HashMap;

public class Row {

    public ArrayList createRow(int rowID) {
        ArrayList<String> newRow = new ArrayList<>();
        newRow.add(Integer.toString(rowID));
        return newRow;
    }

    public ArrayList populateRow(ArrayList<String> row, String data) {
        row.add(data);
        return row;
    }
}
