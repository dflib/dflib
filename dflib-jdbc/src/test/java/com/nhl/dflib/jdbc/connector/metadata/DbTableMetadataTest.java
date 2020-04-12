package com.nhl.dflib.jdbc.connector.metadata;

import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

public class DbTableMetadataTest {

    @Test
    public void testGetColumn() {
        DbColumnMetadata c1 = new DbColumnMetadata("c1", Types.INTEGER, false, false);
        DbColumnMetadata c2 = new DbColumnMetadata("c2", Types.VARCHAR, false, false);

        DbTableMetadata md = new DbTableMetadata(TableFQName.forName("x"), new DbColumnMetadata[]{c1, c2});
        assertSame(c1, md.getColumn("c1"));
        assertSame(c2, md.getColumn("c2"));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testGetColumn_Unknown() {
        DbColumnMetadata c1 = new DbColumnMetadata("c1", Types.INTEGER, false, false);

        DbTableMetadata md = new DbTableMetadata(TableFQName.forName("x"), new DbColumnMetadata[]{c1});
        md.getColumn("no_such_column");
    }
}
