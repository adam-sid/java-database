package edu.uob;

import edu.uob.commands.AlterCommand;
import edu.uob.commands.InsertCommand;
import edu.uob.commands.SelectCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class AlterCommandTest {

    @Test
    public void InsertSetup() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "alterTest";
        Table beforeTable = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        int rowsAmount = beforeTable.getRows().size();
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("big");
        attributes.add("small");
        attributes.add("medium");
        InsertCommand insertCommand = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, attributes);
        insertCommand.execute();
        Table afterTable = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        assertTrue(rowsAmount + 1 == afterTable.getRows().size());
    }
    @Test
    public void AddDropTest() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "alterTest";
        AlterCommand command = new AlterCommand(databaseContext, tableName, "ADD", "newColumn");
        assertDoesNotThrow(command :: execute);
        SelectCommand selectCommand = new SelectCommand(databaseContext, tableName, true, null, null);
        List<String> response = selectCommand.execute();
        assertTrue(response.get(0).contains("newColumn"));
        assertTrue(response.get(1).contains("NULL"));
        AlterCommand dropCommand = new AlterCommand(databaseContext, tableName, "DROP", "newColumn");
        assertDoesNotThrow(dropCommand :: execute);
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        Row firstRow = table.getRow(1);
        assertFalse(firstRow.getRowData().contains("NULL"));
    }

    //TODO check you cannot drop ID from table!
}
