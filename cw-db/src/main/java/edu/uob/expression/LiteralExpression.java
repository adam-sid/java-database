package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;

public class LiteralExpression implements Expression {

    private final Object value;

    public LiteralExpression(Object value) {
        this.value = value;
    }

    @Override
    public Object evaluate(Table table, Row row) {
        return value;
    }
}
