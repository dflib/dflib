package org.dflib.jdbc.connector.metadata;

public class DbColumnMetadata {

    private final String name;
    private final int type;
    private final boolean pk;
    private final boolean nullable;

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
