package edu.uob;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BasicTokeniserTest {
    @Test
    public void tokenisation() {
        ArrayList<String> tokens = BasicTokeniser.setup("Hello world (\"from Mars!\")");
        assertEquals(List.of("Hello", "world", "(", "\"from, Mars!\"",")").toString(), tokens.toString());
    }

    @Test
    public void tokeniseExpression() {
        ArrayList<String> tokens = BasicTokeniser.setup("id==4");
        assertEquals(List.of("id", "==", "4").toString(), tokens.toString());
    }

    @Test
    public void tokeniseSingleEquals() {
        ArrayList<String> tokens = BasicTokeniser.setup("column = value");
        assertEquals(List.of("column", "=", "value").toString(), tokens.toString());
    }
}
