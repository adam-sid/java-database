package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class TableTest {
    @Test
    public void tableCreation() {
        String tableName = "new table";
        List<String> columnNames = List.of("id", "name", "email");
        Table table = new Table(tableName, columnNames);
        assertEquals(tableName, table.getTableName());
        assertEquals(columnNames, table.getColumns());
    }
    //TODO modify/remove these tests before submission - no pre-existing files!
    @Test
    public void tableDerivesColumnsFromFile() throws IOException {
        Table table = new Table( ".." + File.separator + "databases","Test", "tableWithNoRows");
        assertEquals("tableWithNoRows", table.getTableName());
        List<String> columnNames = List.of("id", "Name", "Age", "Email");
        assertEquals(columnNames, table.getColumns());
    }

    @Test
    public void tableLoadsRowsFromFile() throws IOException {
        Table table = new Table( ".." + File.separator + "databases","Test", "tableWithRows");
        assertEquals("tableWithRows", table.getTableName());
        assertEquals(3, table.getRows().size());
        List<String> secondRow = table.getRow(2).getRowData();
        assertEquals("Harry", secondRow.get(1));
    }

    @Test
    public void tableWritesToFile() throws IOException {
        String value = ExampleDBTests.generateRandomName();
        int row = 3, column = 1;
        //read the table
        Table table1 = new Table( ".." + File.separator + "databases","Test", "tableToBeWritten");
        //modify data
        table1.modifyTableData(row, column, value);
        //writing table to same file
        table1.writeToFile(table1.getFileName());

        //read and test file
        Table table2 = new Table( ".." + File.separator + "databases","Test", "tableToBeWritten");
        List<String> secondRow = table2.getRow(row).getRowData();
        assertEquals(value, secondRow.get(column));
    }
}
