package com.nhl.dflib.jdbc.connector.metadata;

import com.nhl.dflib.jdbc.connector.metadata.flavors.MySQLFlavor;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

public class DbMetadataTest {

    @Test
    public void testParseTableName_CatalogNoSchema() throws SQLException {

        DatabaseMetaData jdbcMetaData = mock(DatabaseMetaData.class);
        DbMetadata md = new DbMetadata(mock(DataSource.class), MySQLFlavor.create(jdbcMetaData));

        assertEquals(TableFQName.forCatalogAndName("cat", "tab"), md.parseTableName("cat.tab"));
        assertEquals(TableFQName.forName("tab"), md.parseTableName("tab"));
        assertEquals(TableFQName.forName("cat.schema.tab"), md.parseTableName("cat.schema.tab"));
    }
}
