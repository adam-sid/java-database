package edu.uob;

import java.util.List;

public class Row {

    private final List<String> rowData;

    public Row(List<String> rowData) {
        this.rowData = rowData;
   }

   public List<String> getRowData() {
        return rowData;
   }

   public void modifyElement(Integer index, String value) {
        rowData.set(index, value);
   }
}
