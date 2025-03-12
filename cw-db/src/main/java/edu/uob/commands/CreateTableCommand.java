package edu.uob.commands;

import edu.uob.DatabaseContext;
import edu.uob.Table;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CreateTableCommand implements Command {

    private final DatabaseContext databaseContext;

    private final String databaseName;

    private final String tableName;

    private final ArrayList<String> attributes;

    public Table table = null;

    public CreateTableCommand(DatabaseContext databaseContext, String databaseName, String tableName,
                              ArrayList<String> attributes) {
        this.databaseContext = databaseContext;
        this.databaseName = databaseName;
        this.tableName = tableName;
        this.attributes = setAttributes(attributes);
    }

    public ArrayList<String> setAttributes(ArrayList<String> attributes) {
        if (attributes == null) {
            return null;
        } else {
            attributes.add(0, "id");
            Set<String> seen = new HashSet<>();
            for (String a : attributes) {
                if (!seen.add(a.toLowerCase())) {
                    throw new RuntimeException("Multiple instances of '" + a +
                            "'. Attributes must be unique");
                }
            }
            return attributes;
        }
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getTableName() {
        return tableName;
    }

    public Table getTable() {
        return table;
    }

    public void deleteTable() {
        File tableHomeFolder = new File(databaseContext.getDatabasesHome() + File.separator + databaseName);
        File file = new File(tableHomeFolder, tableName + ".tab");
        if (!file.exists()) {
            throw new RuntimeException("Table " + tableName + " does not exist");
        }
        try {
            file.delete();
        } catch (RuntimeException e) {
            throw new RuntimeException("Could not delete table " + tableName) ;
        }
    }
    //TODO trying to create a table with no Database set should result in error
    @Override
    public List<String> execute(){
        File tableHomeFolder = new File(databaseContext.getDatabasesHome() + File.separator + databaseName);
        File file = new File(tableHomeFolder, tableName + ".tab");
        if (file.exists()) {
            throw new RuntimeException("Table " + tableName + " already exists");
        }
        try {
            Table table = new Table(databaseContext, databaseName, tableName, attributes);
            this.table = table;
            table.writeToFile();
        } catch (IOException e) {
            throw new RuntimeException("Could not create table " + tableName);
        }
        return List.of();
    }
}
