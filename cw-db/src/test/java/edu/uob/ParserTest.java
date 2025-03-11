package edu.uob;

import edu.uob.commands.Command;
import edu.uob.commands.CreateDatabaseCommand;
import edu.uob.commands.CreateTableCommand;
import edu.uob.commands.UseDatabaseCommand;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.function.Executable;

import java.io.File;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

public class ParserTest {

    private static final Set<String> SQL_KEYWORDS = Set.of(
            "USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN",
            "DATABASE", "TABLE", "INTO", "VALUES", "FROM", "WHERE", "SET", "AND", "OR", "ON",
            "ADD", "LIKE"
    );

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

    @Test
    public void parseInvalidName(){
        DatabaseContext databaseContext = new DatabaseContext("..");
        Parser parser = new Parser(databaseContext);
        for (String keyword : SQL_KEYWORDS) {
            String query1 = "CREATE DATABASE " + keyword + ";";
            assertThrows(RuntimeException.class, () -> parser.parse(BasicTokeniser.setup(query1)));
        }
    }

    @Test
    public void parseSelectNoAttributes() {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        Parser parser = new Parser(databaseContext);
        //parser.parse(BasicTokeniser.setup("SELECT * FROM selectTest;"));
        assertThrows(RuntimeException.class, () -> parser.parse(BasicTokeniser.setup("SELECT FROM selectTest;")));
    }

}
