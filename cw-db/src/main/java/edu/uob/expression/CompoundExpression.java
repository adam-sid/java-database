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

        Comparable<Object> first = (Comparable<Object>) firstValue;
        Comparable<Object> second = (Comparable<Object>) secondValue;

        switch (operator) {
            case "==":
                return first.equals(second);
            case "!=":
                return !first.equals(second);
            case ">":
                return first.compareTo(second) > 0;
            case "<":
                return first.compareTo(second) < 0;
            case ">=":
                return first.compareTo(second) >= 0;
            case "<=":
                return first.compareTo(second) <= 0;
            case "LIKE":
            default:
                throw new RuntimeException("Unknown comparator '" + operator + "'");
        }
    }
}
