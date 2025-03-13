package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;
import edu.uob.Util;

import java.util.List;

public class AttributeExpression implements Expression{

    private String attributeName;

    public AttributeExpression(String attributeName) {
        this.attributeName = attributeName;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    @Override
    public Object evaluate(Table table, Row row) {
        List<String> columns = table.getColumns();
        int colIndex = Util.getIndexOf(columns, attributeName);
        return row.getElement(colIndex);
    }
}
