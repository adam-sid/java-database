package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Row;
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
    private ArrayList<String> valueList;
    private final Table table;

    public InsertCommand(DatabaseContext databaseContext, String databaseName,
                         String tableName, ArrayList<String> valueList) throws IOException {
        this.databaseContext = databaseContext;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.valueList = valueList;
        this.table = new Table(databaseContext, databaseName, tableName);
    }

    @Override
    public List<String> execute() throws IOException {
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseName + File.separator + tableName + ".tab");
        valueList = table.setRowData(valueList);
        Row nextRow = new Row(table.getMaxId(), valueList);
        try(java.io.FileWriter writer = new java.io.FileWriter(file, true);
            BufferedWriter bufferedWriter = new BufferedWriter(writer)) {
            table.writeRowToFile(bufferedWriter, nextRow);
        }
        return List.of();
    }
}
