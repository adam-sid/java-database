package edu.uob;

import edu.uob.commands.Command;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class DeleteCommandTest {
    @Disabled
    @Test
    public void deleteCommand() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "deleteTable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "DELETE FROM deleteTable WHERE WHERE id == 3;";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 0);
    }

}
