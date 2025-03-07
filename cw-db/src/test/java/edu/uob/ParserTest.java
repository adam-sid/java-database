package edu.uob;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ParserTest {
    @Test
    public void parsingCreateDatabase() {
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup("CREATE DATABASE markbook;"));
        assertTrue(command instanceof CreateDatabaseCommand);
    }
    @Test
    public void createDatabaseWithInvalidName() {
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        //TODO verify content of exception
        assertThrows(RuntimeException.class, () -> parser.parse(BasicTokeniser.setup("CREATE DATABASE m@rkbook;")));
    }

    @Test
    public void parseUseDatabase() {
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup("USE markbook;"));
        assertTrue(command instanceof UseDatabaseCommand);
    }

    @Test
    public void parseUseNoSemiColon() {
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        assertThrows(RuntimeException.class, () -> parser.parse(BasicTokeniser.setup("USE markbook")));
    }

    @Test
    public void parseCreateTable() {
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup("CREATE TABLE newTable;"));
        assertTrue(command instanceof CreateTableCommand);
    }
}
