package com.nhl.dflib.jdbc.connector.metadata;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DbMetadataTest {

    @Test
    public void testToCatalogSchemaName_CatalogNoSchema() {

        DbMetadata md = new DbMetadata(mock(DataSource.class), DbFlavor.MYSQL, mock(DatabaseMetaData.class));
        assertArrayEquals(new String[]{"cat", null, "tab"}, md.toCatalogSchemaName("cat.tab"));
        assertArrayEquals(new String[]{null, null, "tab"}, md.toCatalogSchemaName("tab"));
        assertArrayEquals(new String[]{null, null, "cat.schema.tab"}, md.toCatalogSchemaName("cat.schema.tab"));
    }
}
