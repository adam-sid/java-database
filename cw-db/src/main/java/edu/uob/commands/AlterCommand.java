package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Row;
import edu.uob.Table;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class AlterCommand implements Command {

    private final DatabaseContext databaseContext;
    private final String tableName;
    private final String alterType;
    private final String attribute;
    private final Table table;

    public AlterCommand(DatabaseContext databaseContext, String tableName, String alterType, String attribute) {
        this.databaseContext = databaseContext;
        this.tableName = tableName;
        this.alterType = alterType;
        this.attribute = attribute;
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
        List<String> columns = table.getColumns();
        Map<Integer, Row> rows = table.getRows();
        if("ADD".equals(alterType)) {
            table.addColumn();
        } else {
            int colIndex = columns.indexOf(attribute);
            if (colIndex == -1) {
                throw new RuntimeException("Attribute " + attribute + " not found in table " + tableName);
            }
            table.deleteColumn(colIndex);
        }

        return List.of();
    }
}
