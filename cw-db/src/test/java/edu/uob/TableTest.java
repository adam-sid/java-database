package edu.uob;

import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileNotFoundException;
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
    //TODO modify this before submission - no pre-existing files!
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
    }
}
