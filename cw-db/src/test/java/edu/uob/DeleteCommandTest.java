package edu.uob;

import edu.uob.commands.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteCommandTest {

    @Test
    public void deleteCommand() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "deletetable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());

        String query = "DELETE FROM deleteTable WHERE age == 'small';";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 0);

        String selectQuery = "SELECT * FROM deleteTable;";
        Command selectCommand = parser.parse(BasicTokeniser.setup(selectQuery));
        List<String> selectResponse = selectCommand.execute();
        assertEquals(2, selectResponse.size());
        assertEquals("3\tbig\tlarge\tmedium", selectResponse.get(1));

        String insertQuery = "INSERT INTO deleteTable VALUES ('big', 'small', 'medium');";
        Command insertCommand1 = parser.parse(BasicTokeniser.setup(insertQuery));
        insertCommand1.execute();
        Command insertCommand2 = parser.parse(BasicTokeniser.setup(insertQuery));
        insertCommand2.execute();
    }
}
