package edu.uob.commands;

import edu.uob.*;
import edu.uob.expression.Expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UpdateCommand implements Command {


    private final DatabaseContext databaseContext;
    private final String tableName;
    private final List<NameValuePair> nameValueList;
    private final Expression whereClause;
    private Table table;

    public UpdateCommand(DatabaseContext databaseContext, String tableName, List<NameValuePair> nameValueList,
                         Expression condition) {
        this.databaseContext = databaseContext;
        this.tableName = tableName;
        this.nameValueList = nameValueList;
        this.table = setTable();
        this.whereClause = condition;
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

        table.getRows().values().forEach(row -> {
            if ((boolean)whereClause.evaluate(table, row)) {
                nameValueList.forEach(element -> {
                    String attributeName = element.getAttributeName();
                    String value = element.getValue();
                    int colIndex = Util.getIndexOf(columns ,attributeName);
                    row.modifyElement(colIndex, value);
                });
            }
        });
        table.writeToFile();
        return List.of();
    }
}
