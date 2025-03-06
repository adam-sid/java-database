package edu.uob;

import java.io.File;

public class DatabaseContext {

    private final String databasesHome;

    private String databaseName;

    public DatabaseContext(String databasesHome) {
        this.databasesHome = databasesHome;
    }

    public String getDatabasesHome() {
        return databasesHome;
    }

    public void setDatabaseName(String databaseName) {
        this.databaseName = databaseName;
    }

    public String getDatabaseName() {
        return databaseName;
    }

    public String getFullPath() {
        return databasesHome + File.separator + databaseName;
    }
}
