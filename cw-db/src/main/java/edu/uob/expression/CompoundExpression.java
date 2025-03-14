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

    private Double safeParseNumber(Object value) {
        if (value instanceof Number) {
            return ((Number) value).doubleValue();
        }
        if (value instanceof String) {
            try {
                return Double.parseDouble((String) value);
            } catch (NumberFormatException nfe) {
                return null;
            }
        }
        return null;
    }

    private int compareExpression(Double num1, Double num2, Object firstValue, Object secondValue) {
        if(num1 != null && num2 != null) {
            return Double.compare(num1, num2);
        } else {
            return ((Comparable<Object>) firstValue).compareTo(secondValue);
        }
    }

    @Override
    public Object evaluate(Table table, Row row) {
        Object firstValue = firstExpression.evaluate(table, row);
        Object secondValue = secondExpression.evaluate(table, row);

        Double num1 = safeParseNumber(firstValue);
        Double num2 = safeParseNumber(secondValue);

        switch (operator) {
            case "==":
                return firstValue.equals(secondValue);
            case "!=":
                return !firstValue.equals(secondValue);
            case ">":
                return compareExpression(num1, num2, firstValue, secondValue) > 0;
            case "<":
                return compareExpression(num1, num2, firstValue, secondValue) < 0;
            case ">=":
                return compareExpression(num1, num2, firstValue, secondValue) >= 0;
            case "<=":
                return compareExpression(num1, num2, firstValue, secondValue) <= 0;
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
