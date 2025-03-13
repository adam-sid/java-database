package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Table;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class InsertCommand implements Command {


    private final DatabaseContext databaseContext;
    private final String databaseName;
    private final String tableName;
    private final ArrayList<String> valueList;
    private final Table table;

    public InsertCommand(DatabaseContext databaseContext, String databaseName,
                         String tableName, ArrayList<String> valueList) throws IOException {
        this.databaseContext = databaseContext;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.valueList = valueList;
        this.table = new Table(databaseContext, databaseName, tableName);
    }

    public Table getTable() {
        return table;
    }

    @Override
    public List<String> execute() throws IOException {
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseName + File.separator + tableName + ".tab");
        int rowId = table.getMaxId();
        if (table.getColumns() == null || table.getColumns().isEmpty()) {
            throw new RuntimeException("Add attributes to table '" + tableName + "' before adding data");
        }
        table.addRow(valueList);
        try(java.io.FileWriter writer = new java.io.FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            table.writeRowToFile(bufferedWriter, table.getRow(rowId));
        }
        return List.of();
    }
}
