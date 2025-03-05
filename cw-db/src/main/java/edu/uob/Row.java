package edu.uob;

import java.util.List;

public class Row {

    private final List<String> rowData;
    private final int id;

    public Row(List<String> rowData) {
        this.rowData = rowData;
        this.id = Integer.parseInt(rowData.get(0));
    }

    public int getId() {
        return id;
    }

    public List<String> getRowData() {
        return rowData;
    }

    public void modifyElement(Integer index, String value) {
        rowData.set(index, value);
    }
}
