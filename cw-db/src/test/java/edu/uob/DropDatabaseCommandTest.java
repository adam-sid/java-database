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
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName.toLowerCase());
        assertEquals(databaseName.toLowerCase(), command.getDatabaseName());
    }

    @Test
    public void testExecute() throws IOException {
        String databaseName = "dropDatabaseCommandTest";
        File database = new File(".." + File.separator + "testDatabases" +
                File.separator + databaseName.toLowerCase());
        database.mkdir();
        assertTrue(database.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName.toLowerCase());
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName.toLowerCase());
        command.execute();
        assertFalse(database.exists());
    }

    @Test
    public void testExecuteDoesNotExist() throws IOException {
        String databaseName = "DoesNotExist";
        File database = new File(".." + File.separator + "testDatabases" + File.separator + databaseName.toLowerCase());
        database.mkdir();
        assertTrue(database.exists());
        database.delete();
        assertFalse(database.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName.toLowerCase());
        DropDatabaseCommand command = new DropDatabaseCommand(databaseContext, databaseName.toLowerCase());
        assertThrows(RuntimeException.class, command::execute);
    }
}
