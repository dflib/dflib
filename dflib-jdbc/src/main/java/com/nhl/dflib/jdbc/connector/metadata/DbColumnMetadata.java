package com.nhl.dflib.jdbc.connector.metadata;

/**
 * @since 0.6
 */
public class DbColumnMetadata {

    private String name;
    private int type;

    public DbColumnMetadata(String name, int type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }
}
