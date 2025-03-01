package edu.uob;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final String tableName;

    private final List<String> columns;

    private final Map<Integer, Row> rows;

    public Table (String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columns = columnNames;
        //create empty map of rows
        this.rows = new HashMap<>();
    }

    public Table (String databaseHome, String database, String tableName) throws IOException {
        //call other constructor
        this(tableName, readColumnNames(setFileName(databaseHome, database, tableName)));
        this.readRows(setFileName(databaseHome, database, tableName));
      }

    private static String setFileName(String databaseHome, String database, String tableName) {
        return databaseHome + File.separator + database + File.separator + tableName + ".tab";
    }

    private static List<String> readColumnNames(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader)) {

            return Stream.of(bufferedReader.readLine().split("\t"))
                    .map(x -> x.toLowerCase(Locale.getDefault()))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }
    }

    private void readRows(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            //throw away column attributes
            bufferedReader.readLine();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                List<String> rowData = Arrays.asList(line.split("\t"));
                Integer rowID = rowIDFromData(rowData);
                Row row = new Row(rowData);
                rows.put(rowID, row);
            }
        }
    }

    private static Integer rowIDFromData(List<String> rowData) throws NumberFormatException {
        return Integer.parseInt(rowData.get(0));
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Map<Integer, Row> getRows() {
        return rows;
    }

    public Row getSpecificRow(Integer rowID) {
        return rows.get(rowID);
    }

    public String rowToString(Row row) {
        List<String> rowData = row.getRowData();
        return String.join("\t", rowData);
    }

    public void writeToFile(String fileName) throws IOException {
        int lineCount = 1;
        File file = new File(fileName);
        try(java.io.FileWriter writer = new java.io.FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            //first write column attributes to first line
            String line = String.join("\t", columns);
            bufferedWriter.write(line);
            while(lineCount <= rows.size()) {
                line = rowToString(getSpecificRow(lineCount++));
                bufferedWriter.write(line);
            }
        }
    }
}
