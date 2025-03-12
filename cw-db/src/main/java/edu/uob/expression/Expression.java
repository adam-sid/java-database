package edu.uob.expression;

import edu.uob.Row;
import edu.uob.Table;

public interface Expression {

    Object evaluate(Table table, Row row);

}
