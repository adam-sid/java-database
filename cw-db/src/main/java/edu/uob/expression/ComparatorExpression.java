package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;

public class ComparatorExpression implements Expression {

    private Expression firstExpression;
    private Expression secondExpression;
    private String comparator;

    public ComparatorExpression(String comparator, Expression firstExpression, Expression secondExpression) {
        this.firstExpression = firstExpression;
        this.secondExpression = secondExpression;
        this.comparator = comparator;
    }

    @Override
    public Object evaluate(Table table, Row row) {
        Object firstValue = firstExpression.evaluate(table, row);
        Object secondValue = secondExpression.evaluate(table, row);
        switch (comparator) {
            case "==":
                return firstValue.equals(secondValue);
            case "!=":
                return !firstValue.equals(secondValue);
            default:
                throw new RuntimeException("Unknown comparator '" + comparator + "'");
        }
    }
}
