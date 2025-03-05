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
    public void testExecuteCommand() {
        String databaseName = "CreateDatabaseCommandTest";
        File databaseFolder = new File("databases" + File.separator + databaseName);
        databaseFolder.delete();
        assertFalse(databaseFolder.exists());
        DatabaseContext databaseContext = new DatabaseContext("databases");
        CreateDatabaseCommand command = new CreateDatabaseCommand(databaseContext, databaseName);
        command.execute();
        assertTrue(databaseFolder.exists());
        assertTrue(databaseFolder.isDirectory());
    }
}
