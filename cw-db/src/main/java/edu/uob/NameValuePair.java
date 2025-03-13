package edu.uob;

public class NameValuePair {

    private final String name;
    private final String value;

    public NameValuePair(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getAttributeName() {
        return name;
    }
}
