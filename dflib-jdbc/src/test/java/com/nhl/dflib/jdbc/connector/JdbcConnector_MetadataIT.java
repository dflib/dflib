package com.nhl.dflib.jdbc.connector;

import com.nhl.dflib.jdbc.connector.metadata.DbColumnMetadata;
import com.nhl.dflib.jdbc.connector.metadata.DbTableMetadata;
import com.nhl.dflib.jdbc.unit.BaseDbTest;
import com.nhl.dflib.jdbc.unit.dbadapter.TestDbAdapter;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.sql.Types;

import static org.junit.jupiter.api.Assertions.*;

public class JdbcConnector_MetadataIT extends BaseDbTest {

    @ParameterizedTest
    @MethodSource(DB_ADAPTERS_METHOD)
    public void testGetMetadata(TestDbAdapter adapter) {

        DbTableMetadata t1 = adapter.createConnector().getMetadata().getTable("t1");
        assertNotNull(t1);

        assertEquals(3, t1.getColumns().length);

        DbColumnMetadata id = t1.getColumn("id");
        assertNotNull(id);
        assertEquals("id", id.getName());
        assertEquals(Types.BIGINT, id.getType());
        assertTrue(id.isPk());
        assertFalse(id.isNullable());

        DbColumnMetadata name = t1.getColumn("name");
        assertNotNull(name);
        assertEquals("name", name.getName());
        assertEquals(Types.VARCHAR, name.getType());
        assertFalse(name.isPk());
        assertTrue(name.isNullable());

        DbColumnMetadata salary = t1.getColumn("salary");
        assertNotNull(salary);
        assertEquals("salary", salary.getName());
        assertEquals(Types.DOUBLE, salary.getType());
        assertFalse(salary.isPk());
        assertTrue(salary.isNullable());
    }
}
