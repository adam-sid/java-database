package edu.uob;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class Parser {

    private final DatabaseContext databaseContext;

    public Parser(DatabaseContext databaseContext) {
        this.databaseContext = databaseContext;
    }

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
            default:
                throw new RuntimeException("Unexpected token: " + nextToken);
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

    private void parseSemiColon(ArrayList<String> tokenArr, AtomicInteger tokenIndex) {
        String nextToken = tokenArr.get(tokenIndex.get());
        if (!nextToken.equals(";")) {
            throw new RuntimeException("Semi-colon expected at position " + tokenIndex.get());
        }
        tokenIndex.incrementAndGet();
    }
}
