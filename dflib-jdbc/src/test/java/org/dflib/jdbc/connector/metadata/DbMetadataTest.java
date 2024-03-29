package org.dflib.jdbc.connector.metadata;

import org.dflib.jdbc.connector.metadata.DbMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.dflib.jdbc.connector.metadata.flavors.MySQLFlavor;
import org.junit.jupiter.api.Test;

import javax.sql.DataSource;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

public class DbMetadataTest {

    @Test
    public void parseTableName_CatalogNoSchema() throws SQLException {

        DatabaseMetaData jdbcMetaData = mock(DatabaseMetaData.class);
        DbMetadata md = new DbMetadata(mock(DataSource.class), MySQLFlavor.create(jdbcMetaData));

        assertEquals(TableFQName.forCatalogAndName("cat", "tab"), md.parseTableName("cat.tab"));
        assertEquals(TableFQName.forName("tab"), md.parseTableName("tab"));
        assertEquals(TableFQName.forName("cat.schema.tab"), md.parseTableName("cat.schema.tab"));
    }
}
