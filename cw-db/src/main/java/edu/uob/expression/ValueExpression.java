package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;
import edu.uob.Util;

import java.util.List;

public class ValueExpression implements Expression {

    private String value;
    private Integer colIndex;

    public ValueExpression(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public void setColIndex(int colIndex) {
        this.colIndex = colIndex;
    }

    @Override
    public Object evaluate(Table table, Row row) {
        return row.getElement(colIndex);
    }
}
