package edu.uob;

import edu.uob.commands.CreateTableCommand;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertFalse;

public class CreateTableCommandTest {

    @Test
    public void constructTableCommand() {
        String databaseName = "CreateDatabaseCommandTest";
        String tableName = "CreateTableCommandTest";
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        CreateTableCommand command = new CreateTableCommand(databaseContext, databaseName, tableName, null);
        assertEquals(tableName, command.getTableName());
    }

    @Test
    public void testExecuteAndDeleteCommand() {
        String tableName = "tableTest";
        File table = new File(".." + File.separator + "testDatabases" + File.separator + "Test", tableName + ".tab");
        table.delete();
        assertFalse(table.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");

        CreateTableCommand command = new CreateTableCommand(databaseContext, "Test", tableName, null);
        command.execute();

        assertTrue(table.exists());
        assertTrue(table.isFile());
        command.deleteTable();
        assertFalse(table.exists());
        assertFalse(table.isDirectory());
    }

    @Test
    public void testTableWithAttributes() {
        String tableName = "tableTest";
        ArrayList<String> attributeList = new ArrayList<String>();
        attributeList.add("name");
        attributeList.add("age");

        File table = new File(".." + File.separator + "testDatabases" + File.separator + "Test", tableName + ".tab");
        table.delete();
        assertFalse(table.exists());
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");

        CreateTableCommand command = new CreateTableCommand(databaseContext, "Test", tableName, attributeList);
        command.execute();

        assertTrue(table.exists());

        command.deleteTable();
        assertFalse(table.exists());
    }

    @Test
    public void testIdDerivedFromTable() throws IOException {
        String databaseName = "foodstuffs";
        String tableName = "turkey";
        File database = new File(".." + File.separator + "testDatabases" + File.separator + databaseName);
        File file = new File(".." + File.separator + "testDatabases" + File.separator + databaseName, tableName + ".tab");
        if (file.exists()) {
            file.delete();
        }
        database.mkdir();
        assertTrue(database.exists());

        ArrayList<String> attributeList = new ArrayList<String>();
        attributeList.add("weight");
        attributeList.add("cookingTime");
        attributeList.add("marinadeTime");

        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        CreateTableCommand command = new CreateTableCommand(databaseContext, databaseName, tableName, attributeList);
        command.execute();
        assertTrue(file.exists());
    }
}


