package edu.uob;

import java.util.HashMap;

public class Table {

    static int tableCount = 1;

    public void createTable(String tableName) {
        HashMap<String, Integer> newTable = new HashMap<>();
        newTable.put(tableName, tableCount);
        tableCount++;
    }

    public void makeRows(int numRows) {

        for (int i = 0; i < numRows; i++) {
            Row
        }
    }
}
