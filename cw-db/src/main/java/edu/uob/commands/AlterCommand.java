package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Table;
import edu.uob.Util;

import java.io.IOException;
import java.util.List;


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
        if("ADD".equals(alterType)) {
            if (!columns.contains(attribute)) {
                table.addColumn(attribute);
            } else {
                throw new RuntimeException("Multiple instances of '" + attribute +
                        "'. Attributes must be unique");
            }
        } else {
            int colIndex = Util.getIndexOf(columns, attribute);
            table.deleteColumn(colIndex);
        }
        return List.of();
    }

}
