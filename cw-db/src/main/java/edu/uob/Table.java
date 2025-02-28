package edu.uob;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Table {
    private final String tableName;

    private final List<String> columns;

    private final Map<String, Row> rows;

    public Table (String tableName, List<String> columnNames) {
        this.tableName = tableName;
        this.columns = columnNames;
        //create empty map of rows
        this.rows = new HashMap<>();
    }

    public Table (String databaseHome, String database, String tableName) throws IOException {
        //call other constructor
        this(tableName, readColumnNames(createFileName(databaseHome, database, tableName)));

      }

    private static String createFileName(String databaseHome, String database, String tableName) {
        return databaseHome + File.separator + database + File.separator + tableName + ".tab";
    }

    private static List<String> readColumnNames(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);) {

            return Stream.of(bufferedReader.readLine().split("\t"))
                    .map(x -> x.toLowerCase(Locale.getDefault()))
                    .map(String::trim)
                    .collect(Collectors.toList());
        }

    }

    private void readRows(String fileName) throws IOException {
        File file = new File(fileName);
        try(java.io.FileReader reader = new java.io.FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(reader);) {
            //throw away column attributes
            bufferedReader.readLine();
            String line;
            while((line = bufferedReader.readLine()) != null) {
                List<String> rowData = Arrays.asList(line.split("\t"));

                //TODO: add constructor to Row class
                //TODO: add above to row Map using the id

                Row row = new Row(rowData);


            }

        }
    }

    public String getTableName() {
        return tableName;
    }

    public List<String> getColumns() {
        return columns;
    }

    public Map<String, Row> getRows() {
        return rows;
    }
}
