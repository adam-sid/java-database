package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;
import edu.uob.Util;

import java.util.Set;

public class CompoundExpression implements Expression {

    private static final Set<String> BOOL_OPERATOR = Set.of(
            "AND" , "OR"
    );

    private Expression firstExpression;
    private Expression secondExpression;
    private String operator;

    public CompoundExpression(String operator, Expression firstExpression, Expression secondExpression) {
        this.operator = operator;
        this.firstExpression = firstExpression;
        this.secondExpression = secondExpression;
    }

    @Override
    public Object evaluate(Table table, Row row) {
        Object firstValue = firstExpression.evaluate(table, row);
        Object secondValue = secondExpression.evaluate(table, row);

        Object first = firstValue;
        Object second = secondValue;

        switch (operator) {
            case "==":
                return first.equals(second);
            case "!=":
                return !first.equals(second);
            case ">":
                return ((Comparable<Object>)first).compareTo(second) > 0;
            case "<":
                return ((Comparable<Object>)first).compareTo(second) < 0;
            case ">=":
                return ((Comparable<Object>)first).compareTo(second) >= 0;
            case "<=":
                return ((Comparable<Object>)first).compareTo(second) <= 0;
            case "LIKE":
                boolean x = ((String)first).indexOf((String)second) != -1;
                return x;
            default:
                throw new RuntimeException("Unknown comparator '" + operator + "'");
        }
    }
}
