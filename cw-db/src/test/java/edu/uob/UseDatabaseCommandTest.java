package edu.uob;

import edu.uob.commands.UseDatabaseCommand;
import org.junit.jupiter.api.Test;

import java.io.File;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;


public class UseDatabaseCommandTest {

    @Test
    public void useDatabaseCommand() {
        String databaseName = "UseDatabaseCommandTest";
        DatabaseContext databaseContext = new DatabaseContext("..");
        UseDatabaseCommand command = new UseDatabaseCommand(databaseContext, databaseName.toLowerCase());
        databaseContext.setDatabaseName(databaseName);
        assertEquals(databaseName.toLowerCase(), command.getDatabaseName());
    }

    @Test
    public void testExecute() {
        String databaseName = "UseDatabaseCommandTest";
        File databaseFolder = new File(".." + File.separator + "testDatabases" + File.separator + databaseName.toLowerCase());
        databaseFolder.mkdirs();
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName);
        UseDatabaseCommand command = new UseDatabaseCommand(databaseContext, databaseName);
        command.execute();
        assertEquals("usedatabasecommandtest", databaseContext.getDatabaseName());
        databaseFolder.delete();
    }

    @Test
    public void testExecuteDoesNotExist() {
        String databaseName = "doesnotexist";
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName(databaseName);
        UseDatabaseCommand command = new UseDatabaseCommand(databaseContext, databaseName);
        assertThrows(RuntimeException.class, command::execute);
    }
}
