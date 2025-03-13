package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Table;
import edu.uob.expression.Expression;

import java.io.IOException;
import java.util.List;

public class DeleteCommand implements Command {

    private final DatabaseContext databaseContext;
    private final String tableName;
    private final Table table;
    private final Expression whereClause;


    public DeleteCommand(DatabaseContext databaseContext, String tableName, Expression condition) {
        this.databaseContext = databaseContext;
        this.tableName = tableName;
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
        table.getRows().values().removeIf(row ->
                (boolean) whereClause.evaluate(table, row));
        table.writeToFile();
        return List.of();
    }
}
