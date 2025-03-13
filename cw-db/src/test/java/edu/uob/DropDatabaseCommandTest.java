package edu.uob;

import edu.uob.commands.DropDatabaseCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

public class DropDatabaseCommandTest {

    @Test
    public void dropTableCommand() {
        String databaseName = "dropDatabaseCommandTest";
        DatabaseContext databaseContext = new DatabaseContext("..");
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName);
        assertEquals(databaseName, command.getDatabaseName());
    }

    @Test
    public void testExecute() throws IOException {
        String databaseName = "dropDatabaseCommandTest";
        File database = new File(".." + File.separator + "testDatabases" +
                File.separator + databaseName);
        database.mkdir();
        assertTrue(database.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName);
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName);
        command.execute();
        assertFalse(database.exists());
    }

    @Test
    public void testExecuteDoesNotExist() throws IOException {
        String databaseName = "DoesNotExist";
        File database = new File(".." + File.separator + "testDatabases" + File.separator + databaseName);
        database.mkdir();
        assertTrue(database.exists());
        database.delete();
        assertFalse(database.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName);
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName);
        assertThrows(RuntimeException.class, command::execute);
    }
}
