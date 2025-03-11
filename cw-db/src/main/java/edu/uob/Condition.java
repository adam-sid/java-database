package edu.uob;

public class Condition {

    private final String firstCondition;

    private final String secondCondition;

    private final String boolOperator;

    private final String comparator;

    private final String attributeName;

    private final String value;

    public Condition(String firstCondition, String secondCondition, String boolOperator, String comparator, String attributeName, String value) {
        this.firstCondition = firstCondition;
        this.secondCondition = secondCondition;
        this.boolOperator = boolOperator;
        this.comparator = comparator;
        this.attributeName = attributeName;
        this.value = value;
    }
}
