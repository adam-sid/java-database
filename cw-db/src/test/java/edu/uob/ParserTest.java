package edu.uob;

import edu.uob.commands.Command;
import edu.uob.commands.CreateDatabaseCommand;
import edu.uob.commands.CreateTableCommand;
import edu.uob.commands.UseDatabaseCommand;
import edu.uob.expression.*;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

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

    @Test
    public void parseBooleanExpression() {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        Parser parser = new Parser(databaseContext);
        String expressionStr = "TRUE;";
        Expression expression = parser.parseExpression(BasicTokeniser.setup(expressionStr), new AtomicInteger(0));
        assertTrue((boolean)expression.evaluate(null, null));
    }

    @Test
    public void parseExpressionInBrackets() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        Parser parser = new Parser(databaseContext);
        String tableName = "selectTable";
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        Row row = new Row(5, List.of("5", "stringValue2", "42", "hello2"));
        String expressionStr = "(weirdness == 42);";
        Expression expression = parser.parseExpression(BasicTokeniser.setup(expressionStr), new AtomicInteger(0));
        assertEquals(true, expression.evaluate(table, row));
    }

    @Test
    public void parseVariable() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selectTable";
        Parser parser = new Parser(databaseContext);
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        String expressionStr = "weirdness;";
        AttributeExpression expression = (AttributeExpression) parser.parseExpression(BasicTokeniser.setup(expressionStr),
                new AtomicInteger(0));
        assertEquals("weirdness", expression.getAttributeName());
        assertEquals("C", expression.evaluate(table, new Row(0, List.of("A", "B", "C"))));
    }
    //TODO make name==123; work same as name == 123;
    @Test
    public void parseIntegerExpression() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        Parser parser = new Parser(databaseContext);
        String tableName = "selectTable";
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        Row row = new Row(5, List.of("5", "stringValue2", "42", "hello2"));
        String expressionStr = "weirdness == 42;";
        Expression expression = parser.parseExpression(BasicTokeniser.setup(expressionStr), new AtomicInteger(0));
        assertEquals(true, expression.evaluate(table, row));
    }

    @Test
    public void parseVariableWhereVariableIsId() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selectTable";
        Parser parser = new Parser(databaseContext);
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        String expressionStr = "id;";
        AttributeExpression expression = (AttributeExpression) parser.parseExpression(BasicTokeniser.setup(expressionStr),
                new AtomicInteger(0));
        assertEquals("id", expression.getAttributeName());
        assertEquals("A", expression.evaluate(table, new Row(0, List.of("A", "B", "C"))));
    }


    @Test
    public void parseNotEqualsExpression() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        Parser parser = new Parser(databaseContext);
        String tableName = "selectTable";
        Table table = new Table(databaseContext, databaseContext.getDatabaseName(), tableName);
        Row row = new Row(5, List.of("5", "stringValue2", "42", "hello2"));
        String expressionStr = "weirdness != 42;";
        Expression expression = parser.parseExpression(BasicTokeniser.setup(expressionStr), new AtomicInteger(0));
        assertFalse((boolean)expression.evaluate(table, row));
    }
}
