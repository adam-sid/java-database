package edu.uob;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final String tableName;

    private List<String> columns;

    private final Map<Integer, Row> rows;

    private final DatabaseContext databaseContext;

    private final String databaseName;

    private int maxId = 1;

    public Table (DatabaseContext databaseContext, String databaseName,
                  String tableName, List<String> columnNames) {
        this.databaseContext = databaseContext;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.columns = columnNames;
        //create empty map of rows
        this.rows = new TreeMap<>();
    }

    public Table (DatabaseContext databaseContext, String databaseName, String tableName) throws IOException {
        //call other constructor
        this(databaseContext, databaseName, tableName,
                readColumnNames(setFileName(databaseContext, databaseName, tableName)));
        String fileName = setFileName(databaseContext, databaseName, tableName);
        this.readRows(fileName);
    }

    private static String setFileName(DatabaseContext databaseContext, String databaseName, String tableName) {
        return databaseContext.getDatabasesHome() + File.separator + databaseName +
                File.separator + tableName + ".tab";
    }

    private static List<String> readColumnNames(String fileName) throws IOException {
        File file = new File(fileName);
        try(FileReader reader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader)) {
            String firstLine = bufferedReader.readLine();
            if(firstLine == null) {
                return null;
            }
            return Stream.of(firstLine.split("\t"))
                    .map(String::trim)
                    .collect(Collectors.toList());
        } catch (IOException e) {
            throw new IOException("Table cannot be found: " + fileName);
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
                List<String> rowData = new ArrayList<>(Arrays.asList(line.split("\t")));
                int rowId = Integer.parseInt(rowData.get(0));
                setMaxId(rowId);
                Row row = new Row(rowId, rowData);
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

    public void writeToFile() throws IOException {
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                    databaseName + File.separator + tableName + ".tab");
        try(java.io.FileWriter writer = new java.io.FileWriter(file, !file.exists());
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            if (columns != null) {
                //first write column attributes to first line
                String headerLine = String.join("\t", columns);
                bufferedWriter.write(headerLine);
                bufferedWriter.newLine();
                rows.values().forEach(row -> {
                    try {
                        writeRowToFile(bufferedWriter, row);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
            }
        }
    }

    public void writeRowToFile(BufferedWriter bufferedWriter, Row row) throws IOException {
        String rowLine = rowToString(row);
        bufferedWriter.write(rowLine);
        bufferedWriter.newLine();
    }

    public void modifyRowData(int rowID, Integer columnIndex, String value) {
        Row row = getRow(rowID);
        row.modifyElement(columnIndex, value);
    }

    public void addRow(ArrayList<String> valueList) {
        valueList.add(0, String.valueOf(maxId));
        if (valueList.size() == getColumns().size()) {
            Row newRow = new Row(maxId, valueList);
            rows.put(maxId, newRow);
            maxId++;
        } else {
            throw new RuntimeException("Provided " + (valueList.size() - 1) + " elements of row data but there are " +
                    (getColumns().size() - 1) + " columns in table.");
        }
    }

    public void setMaxId(int rowId){
        if (rowId == maxId) {
            maxId++;
        } else if (rowId > maxId) {
            maxId = rowId + 1;
        }
    }

    public int getMaxId() {
        return maxId;
    }

    public void deleteColumn(int colIndex) throws IOException {
        if(colIndex == 0) {
            throw new RuntimeException("id is a protected column");
        }
        columns.remove(colIndex);
        rows.values().forEach(row -> row.deleteElement(colIndex));
        writeToFile();
    }

    public void addColumn(String attributeName) throws IOException {
        if (columns == null || columns.isEmpty()){
            this.columns = new ArrayList<>();
            columns.add("id");
        }
        columns.add(attributeName);
        rows.values().forEach(row -> row.addElement("NULL"));
        writeToFile();
    }
}
