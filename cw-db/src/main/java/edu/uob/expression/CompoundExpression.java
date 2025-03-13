package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;

public class CompoundExpression implements Expression {

    private final Expression firstExpression;
    private final Expression secondExpression;
    private final String operator;

    public CompoundExpression(String operator, Expression firstExpression, Expression secondExpression) {
        this.operator = operator;
        this.firstExpression = firstExpression;
        this.secondExpression = secondExpression;
    }
    //not going to throw an exception so don't care about these warnings
    @SuppressWarnings("unchecked")
    @Override
    public Object evaluate(Table table, Row row) {
        Object firstValue = firstExpression.evaluate(table, row);
        Object secondValue = secondExpression.evaluate(table, row);

        switch (operator) {
            case "==":
                return firstValue.equals(secondValue);
            case "!=":
                return !firstValue.equals(secondValue);
            case ">":
                return ((Comparable<Object>) firstValue).compareTo(secondValue) > 0;
            case "<":
                return ((Comparable<Object>) firstValue).compareTo(secondValue) < 0;
            case ">=":
                return ((Comparable<Object>) firstValue).compareTo(secondValue) >= 0;
            case "<=":
                return ((Comparable<Object>) firstValue).compareTo(secondValue) <= 0;
            case "LIKE":
                return ((String) firstValue).contains((String) secondValue);
            case "AND":
                return (boolean) firstValue && (boolean) secondValue;
            case "OR":
                return (boolean) firstValue || (boolean) secondValue;
            default:
                throw new RuntimeException("Unknown comparator '" + operator + "'");
        }
    }
}
