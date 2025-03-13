package edu.uob;

import edu.uob.commands.Command;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class UpdateCommandTest {

    @Test
    public void updateCommand() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "updateTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String randomValue = UUID.randomUUID().toString();
        String query = "UPDATE updateTable SET juiciness = '" + randomValue + "' WHERE id == 3;";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 0);

        String selectQuery = "SELECT juiciness FROM updateTable WHERE id == 3;";
        Command selectCommand = parser.parse(BasicTokeniser.setup(selectQuery));
        List<String> selectResponse = selectCommand.execute();
        assertTrue(selectResponse.size() == 2);
        assertEquals(randomValue, selectResponse.get(1));
    }

    @Test
    public void updateCommandMultipleNameValuePair() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "updateTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String randomValue1 = UUID.randomUUID().toString();
        String randomValue2 = UUID.randomUUID().toString();
        String query = "UPDATE updateTable SET juiciness = '" + randomValue1 + "', Weight = '" + randomValue2 +
                "' WHERE id == 3;";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 0);

        String selectQuery = "SELECT juiciness, weight FROM updateTable WHERE id == 3;";
        Command selectCommand = parser.parse(BasicTokeniser.setup(selectQuery));
        List<String> selectResponse = selectCommand.execute();
        assertTrue(selectResponse.size() == 2);
        assertEquals(randomValue1 + "\t" + randomValue2, selectResponse.get(1));
    }
}
