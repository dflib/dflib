package com.nhl.dflib.jdbc.connector.metadata;

/**
 * @since 0.6
 */
public class DbColumnMetadata {

    private String name;
    private int type;
    private boolean pk;

    public DbColumnMetadata(String name, int type, boolean pk) {
        this.name = name;
        this.type = type;
        this.pk = pk;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public boolean isPk() {
        return pk;
    }
}
