package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;
import edu.uob.Util;

import java.util.List;

public class AttributeExpression implements Expression {

    private String attributeName;

    private int colIndex;

    public AttributeExpression(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public int getColIndex() {
        return colIndex;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    @Override
    public String evaluate(Table table, Row row) {
        List<String> columns = table.getColumns();
        setColIndex(Util.getIndexOf(columns, attributeName));
        return row.getElement(colIndex);
    }


}
