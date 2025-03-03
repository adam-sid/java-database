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
        List<String> columnNames = List.of("id", "name", "age", "email");
        assertEquals(columnNames, table.getColumns());
    }

    @Test
    public void tableLoadsRowsFromFile() throws IOException {
        Table table = new Table( ".." + File.separator + "databases","Test", "tableWithRows");
        assertEquals("tableWithRows", table.getTableName());
        assertEquals(3, table.getRows().size());
        List<String> secondRow = table.getSpecificRow(2).getRowData();
        assertEquals("Harry", secondRow.get(1));
    }

    @Test
    public void tableWritesToFile() throws IOException {
        String value = "Tim";
        int row = 3, column = 1;
        Table table1 = new Table( ".." + File.separator + "databases","Test", "tableWithRows");
        table1.modifyTableData(row, column, value);
        table1.writeToFile(table1.getFileName());

        Table table2 = new Table( ".." + File.separator + "databases","Test", "tableWithRows");
        List<String> secondRow = table2.getSpecificRow(row).getRowData();
        assertEquals(value, secondRow.get(column));
    }
}
