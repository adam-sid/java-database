package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.*;

public class CreateDatabaseCommandTest {
    @Test
    public void constructCommand() {
        String databaseName = "CreateDatabaseCommandTest";
        CreateDatabaseCommand command = new CreateDatabaseCommand(null, databaseName);
        assertEquals(databaseName, command.getDatabaseName());
    }

    @Test
    public void testExecuteAndDeleteCommand() {
        String databaseName = "CreateDatabaseCommandTest";
        File databaseFolder = new File(".." + File.separator + "testDatabases" + File.separator + databaseName);
        databaseFolder.delete();
        assertFalse(databaseFolder.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        CreateDatabaseCommand command = new CreateDatabaseCommand(databaseContext, databaseName);
        command.execute();
        assertTrue(databaseFolder.exists());
        assertTrue(databaseFolder.isDirectory());
        command.deleteDatabase();
        assertFalse(databaseFolder.exists());
        assertFalse(databaseFolder.isDirectory());
    }
}
