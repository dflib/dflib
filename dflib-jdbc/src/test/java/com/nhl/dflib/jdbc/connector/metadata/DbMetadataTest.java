package com.nhl.dflib.jdbc.connector.metadata;

import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DbMetadataTest {

    @Test
    public void testParseTableName_CatalogNoSchema() {

        DbMetadata md = new DbMetadata(mock(DataSource.class), DbFlavor.MYSQL, mock(DatabaseMetaData.class));

        assertEquals(TableFQName.forCatalogAndName("cat", "tab"), md.parseTableName("cat.tab"));
        assertEquals(TableFQName.forName("tab"), md.parseTableName("tab"));
        assertEquals(TableFQName.forName("cat.schema.tab"), md.parseTableName("cat.schema.tab"));
    }
}
