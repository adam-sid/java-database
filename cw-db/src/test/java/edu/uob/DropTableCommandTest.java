package edu.uob;

import edu.uob.DatabaseContext;
import edu.uob.commands.DropTableCommand;
import edu.uob.commands.UseDatabaseCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DropTableCommandTest {

    @Test
    public void dropTableCommand() {
        String tableName = "dropTableCommandTest";
        DatabaseContext databaseContext = new DatabaseContext("..");
        DropTableCommand command = new DropTableCommand(databaseContext, tableName);
        assertEquals(tableName, command.getTableName());
    }

    @Test
    public void testExecute() throws IOException {
        String tableName = "dropTableCommandTest";
        File table = new File(".." + File.separator + "testDatabases" + File.separator +
                "Test", tableName + ".tab");
        table.createNewFile();
        assertTrue(table.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        DropTableCommand command = new DropTableCommand(databaseContext, tableName);
        command.execute();
        assertFalse(table.exists());
    }

    @Test
    public void testExecuteDoesNotExist() throws IOException {
        String tableName = "DoesNotExist";
        File table = new File(".." + File.separator + "testDatabases" + File.separator +
                "Test", tableName + ".tab");
        table.createNewFile();
        assertTrue(table.exists());
        table.delete();
        assertFalse(table.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        DropTableCommand command = new DropTableCommand(databaseContext, tableName);
        assertThrows(RuntimeException.class, command::execute);
    }
}
