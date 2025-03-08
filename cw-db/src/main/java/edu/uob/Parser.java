package edu.uob;

import edu.uob.commands.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {

    private final DatabaseContext databaseContext;

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
        String nextToken = tokenArr.get(tokenIndex.get());
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
            default:
                throw new RuntimeException("Unexpected token: " + nextToken);
        }
    }

    private Command parseInsert(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        parseString(tokenArr, tokenIndex, "INTO", null);
        String rawTableName = parsePlainText(tokenArr, tokenIndex);
        String tableName = rawTableName.trim();
        parseString(tokenArr, tokenIndex, "VALUES", null);
        ArrayList<String> valueList = parseList(tokenArr, tokenIndex);
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
        String nextToken = tokenArr.get(tokenIndex.get());
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
        String rawTableName = parsePlainText(tokenArr, tokenIndex);
        String tableName = rawTableName.trim();
        return new DropTableCommand(databaseContext, tableName);
    }

    private Command parseDropDatabase(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String rawDatabaseName = parsePlainText(tokenArr, tokenIndex);
        String databaseName = rawDatabaseName.trim();
        return new DropDatabaseCommand(databaseContext, databaseName);
    }

    private Command parseCreate(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = tokenArr.get(tokenIndex.get());
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
        String rawTableName = parsePlainText(tokenArr, tokenIndex);
        String tableName = rawTableName.trim();
        ArrayList<String> attributeList = parseList(tokenArr, tokenIndex);
        return new CreateTableCommand(databaseContext, databaseContext.getDatabaseName(), tableName, attributeList);
    }
    //parses attribute or value tokens into an ArrayList and returns this
    private ArrayList<String> parseList(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        if (!tokenArr.get(tokenIndex.get()).equals("(")) {
            return null;
        }
        tokenIndex.incrementAndGet();
        ArrayList<String> attributeList = new ArrayList<>();
        while (tokenIndex.get() < tokenArr.size() && !tokenArr.get(tokenIndex.get()).equals(")")) {
            String newAttribute = parsePlainText(tokenArr, tokenIndex);
            attributeList.add(newAttribute);
            parseString(tokenArr, tokenIndex, ",", ")");
        }
        parseString(tokenArr, tokenIndex, ")", null);
        return attributeList;
    }

    //checks if a token matches an expected string(s)
    //IMPORTANT: if expectedStrB is found then it will walk back tokenIndex
    private void parseString(ArrayList<String> tokenArr, AtomicInteger tokenIndex,
                             String expectedStrA, String expectedStrB) {
        String actualStr = tokenArr.get(tokenIndex.get()).trim();
        tokenIndex.incrementAndGet();
        // If only expectedStrA is provided
        if (expectedStrB == null) {
            if (!actualStr.equals(expectedStrA)) {
                throw new RuntimeException("Expected '" + expectedStrA + "' but got '" + actualStr + "'");
            }
        // If both expectedStrA and expectedStrB are provided
        } else if (actualStr.equals(expectedStrB)) {
            tokenIndex.decrementAndGet(); //decrement counter if expectedStrB found, needed for Attribute List logic.
        } else if (!actualStr.equals(expectedStrA)) {
            throw new RuntimeException("Expected '" + expectedStrA + "' or '" + expectedStrB + "' but got '" +
                    actualStr + "'");
        }

    }

    private Command parseUse(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String rawDatabaseName = parsePlainText(tokenArr, tokenIndex);
        String databaseName = rawDatabaseName.trim();
        return new UseDatabaseCommand(databaseContext, databaseName);
    }

    private Command parseCreateDatabase(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String rawDatabaseName = parsePlainText(tokenArr, tokenIndex);
        String databaseName = rawDatabaseName.trim();
        return new CreateDatabaseCommand(databaseContext, databaseName);
    }

    private static String parsePlainText(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String plainText = tokenArr.get(tokenIndex.getAndIncrement());
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
