package edu.uob;

import edu.uob.commands.InsertCommand;
import edu.uob.commands.UseDatabaseCommand;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InsertCommandTest {
//    @Disabled
    @Test
    public void testInsertCommand() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "tableToBeInserted";
        ArrayList<String> values = new ArrayList<>();
        values.add("value1");
        values.add("value2");
        values.add("value3");
        InsertCommand command = new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, values);
        command.execute();
    }
}
