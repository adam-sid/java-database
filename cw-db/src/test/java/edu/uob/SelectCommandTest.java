package edu.uob;

import edu.uob.commands.Command;
import edu.uob.commands.SelectCommand;
import edu.uob.expression.AttributeExpression;
import edu.uob.expression.CompoundExpression;
import edu.uob.expression.Expression;
import edu.uob.expression.LiteralExpression;
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
        String tableName = "selecttable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator + databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        SelectCommand command = new SelectCommand(databaseContext, tableName, true, null, null);
        assertDoesNotThrow(command::execute);
    }

    @Test
    public void testSelectSingleAttribute() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selecttable";
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
        String tableName = "selecttable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        Expression firstExpression = new AttributeExpression("id");
        Expression secondExpression = new LiteralExpression("4");
        Expression expression = new CompoundExpression("==", firstExpression, secondExpression);

        SelectCommand command = new SelectCommand(databaseContext, tableName, true, null, expression);
        List<String> response = command.execute();
        assertTrue(response.size() == 2);
        assertTrue(response.get(0).contains("description"));
        assertTrue(response.get(1).startsWith("4"));
    }

    @Test
    public void testSelectWithWhereAndAttributes() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "selecttable";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        Expression firstExpression = new AttributeExpression("id");
        Expression secondExpression = new LiteralExpression("4");
        Expression expression = new CompoundExpression("==", firstExpression, secondExpression);

        SelectCommand command = new SelectCommand(databaseContext, tableName, true, null, expression);
        List<String> response = command.execute();
        assertTrue(response.size() == 2);
        assertTrue(response.get(0).contains("description"));
        assertTrue(response.get(1).startsWith("4"));
    }

    @Test
    public void selectWithStringCompare() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "prawnstest";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT  size, tastiness FROM prawnsTest WHERE id == 1;";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 2);
        assertEquals("size\ttastiness", response.get(0));
        assertEquals("NULL\tvery", response.get(1));
    }

    @Test
    public void selectWithLike() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "prawnstest";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT  size, tastiness FROM prawnsTest WHERE speed like 'st';";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertTrue(response.size() == 2);
        assertEquals("size\ttastiness", response.get(0));
        assertEquals("NULL\tvery", response.get(1));
    }

    @Test
    public void selectWithFloatCompareAnd() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "sourdough";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT * FROM sourdough WHERE (price<10) AND (price>3);";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertEquals(3, response.size());
        assertEquals("id\tprice\tcrustiness\tisTasty", response.get(0));
        assertEquals("1\t4.50\tvery\tTRUE", response.get(1));
        assertEquals("2\t4.80\tquite\tFALSE", response.get(2));
    }

    @Test
    public void selectWithFloatCompareOr() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "sourdough";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT * FROM sourdough WHERE (price<10) OR (price>3);";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertEquals(5, response.size());
        assertEquals("id\tprice\tcrustiness\tisTasty", response.get(0));
        assertEquals("1\t4.50\tvery\tTRUE", response.get(1));
        assertEquals("2\t4.80\tquite\tFALSE", response.get(2));
        assertEquals("3\t2.50\tnot very\tFALSE", response.get(3));
        assertEquals("4\t10\tExtremely\tTRUE", response.get(4));
    }

    @Test
    public void selectWithStringCompareLessThan() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "sourdough";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT * FROM sourdough WHERE (crustiness<'very');";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertEquals(4, response.size());
    }

    @Test
    public void selectWithStringIntCompare() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "sourdough";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT * FROM sourdough WHERE (crustiness<12);";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertEquals(1, response.size());
    }

    @Test
    public void selectWithBadFloat() throws IOException {
        DatabaseContext databaseContext = new DatabaseContext(".." + File.separator + "testDatabases");
        databaseContext.setDatabaseName("Test");
        String tableName = "sourdough";
        File file = new File(databaseContext.getDatabasesHome() + File.separator +
                databaseContext.getDatabaseName() + File.separator + tableName + ".tab");
        assertTrue(file.exists());
        String query = "SELECT * FROM sourdough WHERE (price<12.2.2);";
        Parser parser = new Parser(databaseContext);
        Command command = parser.parse(BasicTokeniser.setup(query));
        List<String> response = command.execute();
        assertEquals(2, response.size());
        assertEquals("4\t10\tExtremely\tTRUE", response.get(1));
    }
}
