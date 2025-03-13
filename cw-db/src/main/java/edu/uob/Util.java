package edu.uob;

import java.util.List;

public class Util {
    public static int getIndexOf(List<String> columns, String attribute) {
        int colIndex = columns.stream()
            .map(String::toLowerCase)
            .toList()
            .indexOf(attribute.toLowerCase());
        if (colIndex == -1) {
            throw new RuntimeException("Attribute '" + attribute +
                    "' not found in table");
        }
        return colIndex;
    }
}
