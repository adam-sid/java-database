package edu.uob;

import edu.uob.commands.*;
import edu.uob.expression.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {

    private final DatabaseContext databaseContext;
    //TODO get clarity on reserved words
    //TODO how to handle NULLs
    //TODO make increment/get functions
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
        "==" , ">" , "<" , ">=" , "<=" , "!=" , "LIKE"
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
        String nextToken = peekNextToken(tokenArr, tokenIndex).toUpperCase();
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
            case "UPDATE":
                tokenIndex.incrementAndGet();
                return parseUpdate(tokenArr, tokenIndex);
            case "DELETE":
                tokenIndex.incrementAndGet();
                return parseDelete(tokenArr, tokenIndex);
            default:
                throw new RuntimeException("Unexpected token: " + nextToken);
        }
    }

    private Command parseDelete(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "FROM");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        parseString(tokenArr, tokenIndex, "WHERE");
        Expression condition = parseExpression(tokenArr, tokenIndex);
        return new DeleteCommand(databaseContext, tableName, condition);
    }

    private Command parseUpdate(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String tableName = parsePlainText(tokenArr, tokenIndex);
        parseString(tokenArr, tokenIndex, "SET");
        List<NameValuePair> nameValList = parseNameAttributeList(tokenArr, tokenIndex);
        parseString(tokenArr, tokenIndex, "WHERE");
        Expression condition = parseExpression(tokenArr, tokenIndex);
        return new UpdateCommand(databaseContext, tableName, nameValList, condition);
    }

    private List<NameValuePair> parseNameAttributeList(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        List<NameValuePair> nameValList = new ArrayList<>();
        do {
            String name = getNextToken(tokenArr, tokenIndex);
            parseString(tokenArr, tokenIndex, "=");
            String value = parseValue(tokenArr, tokenIndex);
            NameValuePair nameValPair = new NameValuePair(name, value);
            nameValList.add(nameValPair);
        } while (parseStringIfExists(tokenArr, tokenIndex, ","));
        return nameValList;
    }


    private Command parseAlter(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "TABLE");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        String alterType = parseAlterType(tokenArr, tokenIndex);
        String attribute = parsePlainText(tokenArr, tokenIndex);
        return new AlterCommand(databaseContext, tableName, alterType, attribute);
    }

    private String parseAlterType(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String alterType = peekNextToken(tokenArr, tokenIndex).toUpperCase();
        if ("ADD".equals(alterType) || "DROP".equals(alterType)) {
            tokenIndex.incrementAndGet();
            return alterType;
        } else {
            throw new RuntimeException("Unexpected alteration: " + alterType);
        }
    }

    private Command parseSelect(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        ArrayList<String> attributeList = null;
        boolean isWild = peekNextToken(tokenArr, tokenIndex).equals("*");
        if (!isWild) {
            attributeList = parseList(tokenArr, tokenIndex, false);
            if (attributeList.isEmpty()) {
                throw new RuntimeException("Expected list of attributes but received " + peekNextToken(tokenArr, tokenIndex));
            }
        } else {
            tokenIndex.incrementAndGet();
        }
        parseString(tokenArr, tokenIndex, "FROM");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        Expression condition = null;
        if (peekNextToken(tokenArr, tokenIndex).equalsIgnoreCase("WHERE")) {
            parseString(tokenArr, tokenIndex, "WHERE");
            condition = parseExpression(tokenArr, tokenIndex);
        }
        return new SelectCommand(databaseContext, tableName, isWild, attributeList, condition );
    }

    Expression parseExpression(List<String> tokenArr, AtomicInteger tokenIndex){
        return parseExpression(tokenArr, tokenIndex, true);
    }

    Expression parseExpression(List<String> tokenArr, AtomicInteger tokenIndex, boolean isFirstExpression) {
        String nextToken = getNextToken(tokenArr, tokenIndex);
        Expression firstExpression = null;
        if(BOOL_LITERAL.contains(nextToken)) {
            firstExpression = new LiteralExpression(Boolean.parseBoolean(nextToken));
        } else if(nextToken.equals("(")) {
            Expression e = parseExpression(tokenArr, tokenIndex);
            nextToken = getNextToken(tokenArr, tokenIndex);
            if(!nextToken.equals(")")) {
                throw new RuntimeException("Missing closing bracket at end of expression:" + nextToken);
            }
            firstExpression = e;
        } else if (isFirstExpression && isAttribute(nextToken)) {
            firstExpression = new AttributeExpression(nextToken); //This is definitely an attribute
        } else if (!isFirstExpression && !isAttribute(nextToken)) { //This is definitely a value
            if(nextToken.startsWith("'") && nextToken.endsWith("'")) {
                nextToken = nextToken.substring(1, nextToken.length() - 1);
            }
            return new LiteralExpression(nextToken);
        }
        //process second expression (if there is one)
        if (isFirstExpression) {
            nextToken = peekNextToken(tokenArr, tokenIndex);
            if (COMPARATOR.contains(nextToken.toUpperCase())) {
                tokenIndex.getAndIncrement();
                Expression secondExpression = parseExpression(tokenArr, tokenIndex, false);
                return new CompoundExpression(nextToken.toUpperCase(), firstExpression, secondExpression);
            } else if (BOOL_OPERATOR.contains(nextToken.toUpperCase())) {
                tokenIndex.getAndIncrement();
                // Recursively parse another full expression
                Expression secondExpression = parseExpression(tokenArr, tokenIndex, true);
                return new CompoundExpression(nextToken.toUpperCase(), firstExpression, secondExpression);
            }
        }

        if (firstExpression != null) {
            return firstExpression;
        }
        throw new RuntimeException("Unexpected token '" + nextToken + "' in expression");
    }

    private boolean isAttribute(String nextToken) {
        if (nextToken.chars().allMatch(Character::isDigit)) {
            return false;
        }
        if (BOOL_LITERAL.contains(nextToken.toUpperCase()) || nextToken.equalsIgnoreCase("null")) {
            return false;
        }
        for (int i = 0 ; i != nextToken.length() ; i++) {
            char c = nextToken.charAt(i);
            boolean valid = Character.isAlphabetic(c) || Character.isDigit(c);
            if (!valid) {
                return false;
            }
        }
        return true;
    }

    //TODO don't need databasename in the insert command as can be derived from database context
    private Command parseInsert(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "INTO");
        String tableName = parsePlainText(tokenArr, tokenIndex);
        parseString(tokenArr, tokenIndex, "VALUES");
        parseString(tokenArr, tokenIndex, "(");
        ArrayList<String> valueList = parseList(tokenArr, tokenIndex, true);
        parseString(tokenArr, tokenIndex, ")");
        if (valueList.isEmpty()) {
            throw new RuntimeException("No values found to insert");
        }
        try {
            return new InsertCommand(databaseContext, databaseContext.getDatabaseName(), tableName, valueList);
        } catch (IOException e) {
            throw new RuntimeException("Cannot INSERT into table: " + tableName);
        }
    }

    private Command parseDrop(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = peekNextToken(tokenArr, tokenIndex).toUpperCase();
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
        String nextToken = peekNextToken(tokenArr, tokenIndex).toUpperCase();
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

    private static String peekNextToken(List<String> tokenArr, AtomicInteger tokenIndex) {
        try {
            return tokenArr.get(tokenIndex.get());
        } catch (IndexOutOfBoundsException aob) {
            throw new RuntimeException("Unexpected end of line");
        }
    }

    private Command parseCreateTable(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String tableName = parsePlainText(tokenArr, tokenIndex);
        ArrayList<String> attributeList = null;
        if (peekNextToken(tokenArr, tokenIndex).equals("(")) {
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
        while ((tokenIndex.get() < tokenArr.size()) && peekNextToken(tokenArr, tokenIndex).equals(",")) {
            tokenIndex.incrementAndGet();
            listString = isValue ? parseValue(tokenArr, tokenIndex) : parsePlainText(tokenArr, tokenIndex);
            attributeList.add(listString);
        }
        return attributeList;
    }

    public static String parseValue(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String value = getNextToken(tokenArr, tokenIndex);
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
            return value; //if string literal
        }
        else {  //must be a number if all above false
            parseIntOrFloat(value);
            return value;
        }
    }

    private static String getNextToken(List<String> tokenArr, AtomicInteger tokenIndex) {
        try {
            return tokenArr.get(tokenIndex.getAndIncrement());
        } catch (IndexOutOfBoundsException aob) {
            throw new RuntimeException("Unexpected end of line");
        }
    }

    private static void parseIntOrFloat(String number) {
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
    private static void parseStringLiteral(String value) {
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

    private static void parseCharLiteral(char c) {
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
        String actualStr = peekNextToken(tokenArr, tokenIndex).toUpperCase();
        tokenIndex.incrementAndGet();
        if (!actualStr.equals(expectedStr)) {
            throw new RuntimeException("Expected '" + expectedStr + "' but got '" + actualStr + "'");
        }
    }

    private boolean parseStringIfExists(ArrayList<String> tokenArr, AtomicInteger tokenIndex, String expectedStr) {
        String actualStr = peekNextToken(tokenArr, tokenIndex).toUpperCase();
        if (actualStr.equals(expectedStr)) {
            tokenIndex.incrementAndGet();
            return true;
        }
        return false;
    }

    private Command parseUse(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String databaseName = parsePlainText(tokenArr, tokenIndex);
        return new UseDatabaseCommand(databaseContext, databaseName);
    }

    private Command parseCreateDatabase(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String databaseName = parsePlainText(tokenArr, tokenIndex);
        return new CreateDatabaseCommand(databaseContext, databaseName);
    }

    public static String parsePlainText(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String plainText = getNextToken(tokenArr, tokenIndex);
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
        String nextToken = peekNextToken(tokenArr, tokenIndex);
        if (!nextToken.equals(";")) {
            throw new RuntimeException("Semi-colon expected at position " + tokenIndex.get());
        }
        tokenIndex.incrementAndGet();
    }
}
