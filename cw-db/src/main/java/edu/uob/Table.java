package edu.uob;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final String tableName;

    private final List<String> columns;

    private final Map<Integer, Row> rows;
    //TODO: I get warnings about how I access this fileName variable in TableTest
    private static String fileName;

    public Table (String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columns = columnNames;
        //create empty map of rows
        this.rows = new TreeMap<>();
    }

    public Table (String databaseHome, String database, String tableName) throws IOException {
        //call other constructor
        this(tableName, readColumnNames(setFileName(databaseHome, database, tableName)));
        fileName = setFileName(databaseHome, database, tableName);
        this.readRows(fileName);
      }

    private static String setFileName(String databaseHome, String database, String tableName) {
        return databaseHome + File.separator + database + File.separator + tableName + ".tab";
    }

    public static String getFileName() {
        return fileName;
    }

    private static List<String> readColumnNames(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader)) {

            return Stream.of(bufferedReader.readLine().split("\t"))
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
                Row row = new Row(rowData);
                rows.put(row.getId(), row);
            }
        }
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

    public Row getRow(int rowID) {
        return rows.get(rowID);
    }

    public String rowToString(Row row) {
        List<String> rowData = row.getRowData();
        return String.join("\t", rowData);
    }

    public void writeToFile(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileWriter writer = new java.io.FileWriter(file);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            //first write column attributes to first line
            String headerLine = String.join("\t", columns);
            bufferedWriter.write(headerLine);
            bufferedWriter.newLine();
            rows.values().forEach(row -> {
                try {
                    String rowLine = rowToString(row);
                    bufferedWriter.write(rowLine);
                    bufferedWriter.newLine();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }

    public void modifyTableData(int rowID, Integer columnIndex, String value) {
        Row row = getRow(rowID);
        row.modifyElement(columnIndex, value);
    }
}
