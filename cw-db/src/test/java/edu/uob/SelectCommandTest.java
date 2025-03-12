package edu.uob;

import edu.uob.commands.SelectCommand;
import edu.uob.expression.Expression;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SelectCommandTest {

    @Test
    public void testSelectWildcard() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selectTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator + databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        SelectCommand command = new SelectCommand(databaseContext, tableName, true, null, null);
        assertDoesNotThrow(command::execute);
    }

    @Test
    public void testSelectSingleAttribute() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selectTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator + databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        ArrayList<String> attributes = new ArrayList<>();
        attributes.add("description");
        SelectCommand command = new SelectCommand(databaseContext, tableName, false, attributes, null);
        List<String> response = command.execute();
        assertTrue(response.size() > 0);
        assertTrue(response.contains("description"));
        assertFalse(response.contains("id"));
    }

    @Test
    public void testSelectWithWhere() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selectTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        Expression expression = null;
        SelectCommand command = new SelectCommand(databaseContext, tableName, true, null, expression);
        List<String> response = command.execute();
        assertTrue(response.size() > 0);
        assertTrue(response.contains("description"));
        assertFalse(response.contains("id"));
    }
}
