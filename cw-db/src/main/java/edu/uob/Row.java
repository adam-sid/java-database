package edu.uob;

import java.util.List;

public class Row {
    private final int rowId;
    private List<String> rowData;

    public Row(int rowId, List<String> rowData) {
        this.rowId = rowId;
        this.rowData = rowData;
    }

    public int getId() {
        return rowId;
    }

    public List<String> getRowData() {
        return rowData;
    }

    public void modifyElement(Integer index, String value) {
        rowData.set(index, value);
    }

    public String getElement(int columnIndex) {
        return rowData.get(columnIndex);
    }

    public void addElement(String value) {
        rowData.add(value);
    }

    public void deleteElement(int colIndex) {
        rowData.remove(colIndex);
    }
}
