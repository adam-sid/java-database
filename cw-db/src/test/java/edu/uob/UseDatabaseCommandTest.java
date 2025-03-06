package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;


public class UseDatabaseCommandTest {

    @Test
    public void useDatabaseCommand() {
        String databaseName = "UseDatabaseCommandTest";
        DatabaseContext databaseContext = new DatabaseContext("..");
        UseDatabaseCommand command = new UseDatabaseCommand(databaseContext, databaseName);
        databaseContext.setDatabaseName(databaseName);
        assertEquals(databaseName, command.getDatabaseName());
    }

    @Test
    public void testExecute() {
        String databaseName = "UseDatabaseCommandTest";
        DatabaseContext databaseContext = new DatabaseContext("..");
        UseDatabaseCommand command = new UseDatabaseCommand(databaseContext, databaseName);
        command.execute();
        assertEquals("UseDatabaseCommandTest", databaseContext.getDatabaseName());
    }

    //TODO add test for when no database exists
}
