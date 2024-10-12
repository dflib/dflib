package org.dflib.jdbc.connector.metadata;

public class DbColumnMetadata {

    private String name;
    private int type;
    private boolean pk;
    private boolean nullable;

    public DbColumnMetadata(String name, int type, boolean pk, boolean nullable) {
        this.name = name;
        this.type = type;
        this.pk = pk;
        this.nullable = nullable;
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


    public boolean isNullable() {
        return nullable;
    }
}
