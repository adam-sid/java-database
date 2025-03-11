package edu.uob;

import edu.uob.commands.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {

    private final DatabaseContext databaseContext;
    //TODO get clarity on reserved words
    private static final Set<String> SQL_KEYWORDS = Set.of(
        "USE", "CREATE", "DROP", "ALTER", "INSERT", "SELECT", "UPDATE", "DELETE", "JOIN",
        "DATABASE", "TABLE", "INTO", "VALUES", "FROM", "WHERE", "SET", "AND", "OR", "ON",
        "ADD", "LIKE", "NULL"
    );

    private static final Set<String> BOOL_LITERAL = Set.of(
        "TRUE", "FALSE"
    );

    private static final Set<String> SYMBOL_LITERAL = Set.of(
        "!" , "#" , "$" , "%" , "&" , "(" , ")" , "*" , "+" , "," , "-" , "." , "/" ,
        ":" , ";" , ">" , "=" , "<" , "?" , "@" , "[" , "\\" , "]" , "^" , "_" , "`",
        "{" , "}" , "~"
    );

    private static final Set<String> BOOL_OPERATOR = Set.of(
        "AND" , "OR"
    );

    private static final Set<String> COMPARATOR = Set.of(
        "==" , ">" , "<" , ">=" , "<=" , "!=" , " LIKE "
    );

    public Parser(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    //Position in token array is stored as an AtomicInteger type so it persists across method calls
    public Command parse(ArrayList<String> tokenArr) {
        AtomicInteger tokenIndex = new AtomicInteger(0);
        Command command = parseCommand(tokenArr, tokenIndex);
        parseSemiColon(tokenArr, tokenIndex);
        return command;
    }

    private Command parseCommand(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = tokenArr.get(tokenIndex.get()).toUpperCase();
        switch (nextToken) {
            case "CREATE":
                tokenIndex.incrementAndGet();
                return parseCreate(tokenArr, tokenIndex);
            case "USE":
                tokenIndex.incrementAndGet();
                return parseUse(tokenArr, tokenIndex);
            case "DROP":
                tokenIndex.incrementAndGet();
                return parseDrop(tokenArr, tokenIndex);
            case "INSERT":
                tokenIndex.incrementAndGet();
                return parseInsert(tokenArr, tokenIndex);
            case "SELECT":
                tokenIndex.incrementAndGet();
                return parseSelect(tokenArr, tokenIndex);
            case "ALTER":
                tokenIndex.incrementAndGet();
                return parseAlter(tokenArr, tokenIndex);
            default:
                throw new RuntimeException("Unexpected token: " + nextToken);
        }
    }

    private Command parseAlter(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "TABLE");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        String alterType = parseAlterType(tokenArr, tokenIndex);
        String attribute = parsePlainText(tokenArr, tokenIndex);
        return new AlterCommand(databaseContext, tableName, alterType, attribute);
    }

    private String parseAlterType(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String alterType = tokenArr.get(tokenIndex.get()).toUpperCase();
        if ("ALTER".equals(alterType) || "DROP".equals(alterType)) {
            tokenIndex.incrementAndGet();
            return alterType;
        } else {
            throw new RuntimeException("Unexpected alteration: " + alterType);
        }
    }

    private Command parseSelect(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        ArrayList<String> attributeList = null;
        boolean isWild = tokenArr.get(tokenIndex.get()).equals("*");
        if (!isWild) {
            attributeList = parseList(tokenArr, tokenIndex, false);
            if (attributeList == null) {
                throw new RuntimeException("Expected list of attributes but received " + tokenArr.get(tokenIndex.get()));
            }
        } else {
            tokenIndex.incrementAndGet();
        }
        parseString(tokenArr, tokenIndex, "FROM");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        Condition condition = null;
        if (tokenArr.get(tokenIndex.get()).equalsIgnoreCase("WHERE")) {
            condition = parseCondition(tokenArr, tokenIndex);
        }
        return new SelectCommand(databaseContext, tableName, isWild, attributeList, condition);
    }

    private Condition parseCondition(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        return null;
    }

    //TODO don't need databasename in the insert command as can be derived from database context
    private Command parseInsert(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "INTO");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        parseString(tokenArr, tokenIndex, "VALUES");
        parseString(tokenArr, tokenIndex, "(");
        ArrayList<String> valueList = parseList(tokenArr, tokenIndex, true);
        parseString(tokenArr, tokenIndex, ")");
        if (valueList == null || valueList.isEmpty()) {
            throw new RuntimeException("No values found to insert");
        }
        try {
            return new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, valueList);
        } catch (IOException e) {
            throw new RuntimeException("Cannot INSERT into table: " + tableName);
        }
    }

    private Command parseDrop(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = tokenArr.get(tokenIndex.get()).toUpperCase();
        switch (nextToken) {
            case "DATABASE":
                tokenIndex.incrementAndGet();
                return parseDropDatabase(tokenArr, tokenIndex);
            case "TABLE":
                tokenIndex.incrementAndGet();
                return parseDropTable(tokenArr, tokenIndex);
            default:
                throw new RuntimeException("Unexpected token: " + nextToken + ". Did you mean TABLE or DATABASE?");
        }
    }

    private Command parseDropTable(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String tableName = parsePlainText(tokenArr, tokenIndex);
        return new DropTableCommand(databaseContext, tableName);
    }

    private Command parseDropDatabase(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String databaseName = parsePlainText(tokenArr, tokenIndex);
        return new DropDatabaseCommand(databaseContext, databaseName);
    }

    private Command parseCreate(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = tokenArr.get(tokenIndex.get()).toUpperCase();
        switch (nextToken) {
            case "DATABASE":
                tokenIndex.incrementAndGet();
                return parseCreateDatabase(tokenArr, tokenIndex);
            case "TABLE":
                tokenIndex.incrementAndGet();
                return parseCreateTable(tokenArr, tokenIndex);
            default:
                throw new RuntimeException("Unexpected token: " + nextToken + ". Did you mean TABLE or DATABASE?");
        }
    }

    private Command parseCreateTable(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String tableName = parsePlainText(tokenArr, tokenIndex);
        ArrayList<String> attributeList = null;
        if (tokenArr.get(tokenIndex.get()).equals("(")) {
            parseString(tokenArr, tokenIndex, "(");
            attributeList = parseList(tokenArr, tokenIndex, false);
            parseString(tokenArr, tokenIndex, ")");
        }
        return new CreateTableCommand(databaseContext, databaseContext.getDatabaseName(), tableName, attributeList);
    }
    //parses attribute or value tokens into an ArrayList and returns this
    private ArrayList<String> parseList(ArrayList<String> tokenArr, AtomicInteger tokenIndex, Boolean isValue) {
        ArrayList<String> attributeList = new ArrayList<>();
        String listString = isValue ? parseValue(tokenArr, tokenIndex) : parsePlainText(tokenArr, tokenIndex);
        attributeList.add(listString);
        while ((tokenIndex.get() < tokenArr.size()) && tokenArr.get(tokenIndex.get()).equals(",")) {
            tokenIndex.incrementAndGet();
            listString = isValue ? parseValue(tokenArr, tokenIndex) : parsePlainText(tokenArr, tokenIndex);
            attributeList.add(listString);
        }
        return attributeList;
    }

    private String parseValue(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String value = tokenArr.get(tokenIndex.getAndIncrement());
        if (BOOL_LITERAL.contains(value.toUpperCase())) {
            return value; //if boolean literal
        }
        if (value.equalsIgnoreCase("NULL")) {
            return value; //if NULL
        }
        else if (value.startsWith("'")) {
            if (!value.endsWith("'")) {
                throw new RuntimeException("String " + value + " must end with \"'\"");
            }
            value = value.substring(1, value.length() - 1);
            parseStringLiteral(value);
            value = "'" + value + "'";
            return value; //if string literal
        }
        else {  //must be a number if all above false
            parseIntOrFloat(value);
            return value;
        }
    }

    private void parseIntOrFloat(String number) {
        boolean isFloat = number.contains(".");
        int i = 0;
        int dotCount = 0;
        if (number.startsWith("+") || number.startsWith("-")) {
            i++;
        }
        if (number.charAt(i) == '.' || number.endsWith(".")) {
            throw new RuntimeException("Floating number cannot start or end with '.'");
        }
        for (; i != number.length() ; i++) {
            char c = number.charAt(i);
            if (c == '.' && isFloat) {
                dotCount++;
                if (dotCount > 1) {
                    throw new RuntimeException("Floating number cannot have more than one '.' character");
                }
            }
            boolean valid = Character.isDigit(c) || (c == '.' && isFloat);
            if (!valid) {
                throw new RuntimeException("Unexpected character: " + c);
            }
        }
    }

    //String in tokenArr will be used for comparison if an override string isn't provided
    //This DOES NOT increment the tokenIndex
    private void parseStringLiteral(String value) {
        //TODO check if objects equals is effective here?
        //case if string is ""
        if (value.isEmpty()) {
            return;
        }
        for (int i = 0; i < value.length(); i++) {
            char c = value.charAt(i);
            parseCharLiteral(c);
        }
    }

    private void parseCharLiteral(char c) {
        if (SYMBOL_LITERAL.contains(String.valueOf(c)) || Character.isWhitespace(c)) {
            return;
        }
        boolean valid = Character.isAlphabetic(c) || Character.isDigit(c);
        if (!valid) {
            throw new RuntimeException("Unexpected character '" + c + "' in string");
        }
    }

    //checks if a token matches an expected string(s)
    private void parseString(ArrayList<String> tokenArr, AtomicInteger tokenIndex, String expectedStr) {
        String actualStr = tokenArr.get(tokenIndex.get()).toUpperCase();
        tokenIndex.incrementAndGet();
        if (!actualStr.equals(expectedStr)) {
            throw new RuntimeException("Expected '" + expectedStr + "' but got '" + actualStr + "'");
        }
    }

    private Command parseUse(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String databaseName = parsePlainText(tokenArr, tokenIndex);
        return new UseDatabaseCommand(databaseContext, databaseName);
    }

    private Command parseCreateDatabase(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String databaseName = parsePlainText(tokenArr, tokenIndex);
        return new CreateDatabaseCommand(databaseContext, databaseName);
    }

    private static String parsePlainText(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String plainText = tokenArr.get(tokenIndex.getAndIncrement());
        if(SQL_KEYWORDS.contains(plainText.toUpperCase())) {
            throw new RuntimeException("SQL Keyword '" + plainText.toUpperCase() + "' is a reserved word");
        } else if (BOOL_LITERAL.contains(plainText.toUpperCase())) {
            throw new RuntimeException("Boolean values TRUE/FALSE cannot be used for database, table " +
                    "or attribute names");
        }
        for (int i = 0 ; i != plainText.length() ; i++) {
            char c = plainText.charAt(i);
            boolean valid = Character.isAlphabetic(c) || Character.isDigit(c);
            if (!valid) {
                throw new RuntimeException("Unexpected character: " + c);
            }
        }
        return plainText;
    }
    //TODO: can I shorten this function?
    private void parseSemiColon(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        if (tokenIndex.get() >= tokenArr.size()) {
            throw new RuntimeException("Reached end of input but no semi-colon found");
        }
        String nextToken = tokenArr.get(tokenIndex.get());
        if (!nextToken.equals(";")) {
            throw new RuntimeException("Semi-colon expected at position " + tokenIndex.get());
        }
        tokenIndex.incrementAndGet();
    }
}
