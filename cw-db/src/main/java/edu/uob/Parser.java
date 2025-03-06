package edu.uob;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {

    private final DatabaseContext databaseContext;

    public Parser(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

    //Position in token array is stored as an AtomicInteger type so it can persist across method calls
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
            default:
                throw new RuntimeException("Unexpected token: " + nextToken);
        }

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
                throw new RuntimeException("Unexpected token: " + nextToken);
        }
    }

    private Command parseCreateTable(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String rawTableName = parsePlainText(tokenArr, tokenIndex);
        String tableName = rawTableName.trim();
        ArrayList<String> attributeList = parseAttributeList(tokenArr, tokenIndex);
        return new CreateTableCommand(databaseContext, databaseContext.getDatabaseName(), tableName, attributeList);
    }

    private ArrayList<String> parseAttributeList(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        if (!parseChar(tokenArr, tokenIndex, "(")) {
            return null;
        }
        tokenIndex.incrementAndGet();
        ArrayList<String> attributeList = new ArrayList<>();
        while (tokenIndex.get() < tokenArr.size() && !tokenArr.get(tokenIndex.get()).equals(")")) {
            String newAttribute = parsePlainText(tokenArr, tokenIndex);
            attributeList.add(newAttribute);
            tokenIndex.incrementAndGet();
            if (!parseChar(tokenArr, tokenIndex, ",") || !parseChar(tokenArr, tokenIndex, ")")) {
                throw new RuntimeException("Expected ')' or ',' but got " + tokenIndex.get());
            }
        }
        if (parseChar(tokenArr, tokenIndex, ")")) {
            return attributeList;
        } else
            throw new RuntimeException("Expected ')' but got " + tokenIndex.get());
    }
    //note this function DOES NOT increment tokenIndex - must manually increment after method call
    private boolean parseChar(ArrayList<String> tokenArr, AtomicInteger tokenIndex, String expectedChar) {
        String actualChar = tokenArr.get(tokenIndex.get()).trim();
        return actualChar.equals(expectedChar);
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
