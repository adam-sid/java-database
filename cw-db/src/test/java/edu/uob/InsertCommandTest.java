package edu.uob;

import edu.uob.commands.CreateTableCommand;
import edu.uob.commands.DropTableCommand;
import edu.uob.commands.InsertCommand;
import edu.uob.commands.UseDatabaseCommand;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InsertCommandTest {
//    @Disabled
    @Test
    public void testInsertCommand() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("test");
        String tableName = "insertedtable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator + databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        if (file.exists()) {
            file.delete();
        }
        assertFalse(file.exists());
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("attribute1");
        attributes.add("attribute2");
        attributes.add("attribute3");
        CreateTableCommand createCommand = new CreateTableCommand(databaseContext, databaseContext.getDatabaseName(), tableName, attributes);
        createCommand.execute();
        Table table = createCommand.getTable();
        assertTrue(table.getRows().isEmpty());
        assertTrue(file.exists());
        ArrayList<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");
        InsertCommand command = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, values);
        command.execute();
        table = command.getTable();
        int row = 1, column = 1;

        assertTrue(table != null);
        assertTrue(table.getRows().size() == 1);
        Row firstRow = table.getRow(row);
        assertTrue(firstRow != null);

        assertEquals("value1", firstRow.getElement(column));
        DropTableCommand deleteCommand = new DropTableCommand(databaseContext, tableName);
        deleteCommand.execute();
        assertFalse(file.exists());
    }

    @Test
    public void tooMuchData() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("test");
        String tableName = "inserttest";
        ArrayList<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");
        values.add("value4");
        InsertCommand command = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, values);
        assertThrows(RuntimeException.class, command::execute);
    }

    @Test
    public void tooLittleData() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("test");
        String tableName = "inserttest";
        ArrayList<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        InsertCommand command = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, values);
        assertThrows(RuntimeException.class, command::execute);
    }

    @Test
    public void insertWithNoAttributes() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("test");
        String tableName = "emptytable";
        ArrayList<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");
        InsertCommand command = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, values);
        assertThrows(RuntimeException.class, command::execute);
    }

}
