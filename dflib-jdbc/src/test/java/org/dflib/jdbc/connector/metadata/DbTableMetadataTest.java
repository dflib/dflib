package org.dflib.jdbc.connector.metadata;

import org.dflib.jdbc.connector.metadata.DbColumnMetadata;
import org.dflib.jdbc.connector.metadata.DbTableMetadata;
import org.dflib.jdbc.connector.metadata.TableFQName;
import org.junit.jupiter.api.Test;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

public class DbTableMetadataTest {

    @Test
    public void getColumn() {
        DbColumnMetadata c1 = new DbColumnMetadata("c1", Types.INTEGER, false, false);
        DbColumnMetadata c2 = new DbColumnMetadata("c2", Types.VARCHAR, false, false);

        DbTableMetadata md = new DbTableMetadata(TableFQName.forName("x"), new DbColumnMetadata[]{c1, c2});
        assertSame(c1, md.getColumn("c1"));
        assertSame(c2, md.getColumn("c2"));
    }

    @Test
    public void getColumn_Unknown() {
        DbColumnMetadata c1 = new DbColumnMetadata("c1", Types.INTEGER, false, false);

        DbTableMetadata md = new DbTableMetadata(TableFQName.forName("x"), new DbColumnMetadata[]{c1});
        assertThrows(IllegalArgumentException.class, () -> md.getColumn("no_such_column"));
    }
}
