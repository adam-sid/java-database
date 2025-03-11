package edu.uob.commands;

import edu.uob.Condition;
import edu.uob.DatabaseContext;
import edu.uob.Row;
import edu.uob.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Map;

public class SelectCommand implements Command {

    private final DatabaseContext databaseContext;

    private final String tableName;

    private final Boolean isWild;

    private final Condition condition;

    private ArrayList<String> attributes;

    private final Table table;

    public SelectCommand(DatabaseContext databaseContext, String tableName, Boolean isWild,
                         ArrayList<String> attributes, Condition condition) {
        this.databaseContext = databaseContext;
        this.tableName = tableName;
        this.isWild = isWild;
        this.attributes = attributes;
        this.condition = condition;
        this.table = setTable();
    }

    private Table setTable() {
        try {
            return new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        } catch(IOException e){
            throw new RuntimeException("Could not find table " + tableName);
        }
    }

    @Override
    public List<String> execute() throws IOException {
        List<String> result = new ArrayList<>();
        List<String> columns = table.getColumns();
        Map<Integer, Row> rows = table.getRows();
        List<Integer> queryColumns = new ArrayList<>(); //list of relevant column indexes
        if(isWild){
            attributes = (ArrayList<String>) columns;
        }
        for (String attribute : attributes) {
            int colIndex = columns.indexOf(attribute);
            if (colIndex == -1) {
                throw new RuntimeException("Attribute " + attribute + " not found in table " + tableName);
            }
            queryColumns.add(colIndex);
        }
        result.add(  //add column data in order of appearance in query
            queryColumns.stream()
                .map(columns::get)
                .collect(Collectors.joining("\t"))
        );
        for (Row row : rows.values()){ //add row data
            List<String> rowData = row.getRowData();
            result.add(
                queryColumns.stream()
                    .map(rowData::get)
                    .collect(Collectors.joining("\t"))
            );
        }
        return result;
    }
}
