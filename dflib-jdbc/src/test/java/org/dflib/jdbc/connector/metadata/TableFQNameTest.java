package org.dflib.jdbc.connector.metadata;

import org.dflib.jdbc.connector.metadata.TableFQName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class TableFQNameTest {

    @Test
    public void equals_CatalogSchemaName() {

        TableFQName n1 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t1");
        TableFQName n2 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t1");
        TableFQName n3 = TableFQName.forCatalogSchemaAndName("c2", "s1", "t1");
        TableFQName n4 = TableFQName.forCatalogSchemaAndName("c1", "s2", "t1");
        TableFQName n5 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t2");

        assertEquals(n1, n1);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertNotEquals(n1, n4);
        assertNotEquals(n1, n5);
    }

    @Test
    public void equals_CatalogName() {

        TableFQName n1 = TableFQName.forCatalogAndName("c1", "t1");
        TableFQName n2 = TableFQName.forCatalogAndName("c1", "t1");
        TableFQName n3 = TableFQName.forCatalogAndName("c2", "t1");
        TableFQName n4 = TableFQName.forCatalogAndName("c1", "t2");

        assertEquals(n1, n1);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertNotEquals(n1, n4);
    }

    @Test
    public void equals_SchemaName() {

        TableFQName n1 = TableFQName.forSchemaAndName("s1", "t1");
        TableFQName n2 = TableFQName.forSchemaAndName("s1", "t1");
        TableFQName n3 = TableFQName.forSchemaAndName("s2", "t1");
        TableFQName n4 = TableFQName.forSchemaAndName("s1", "t2");

        assertEquals(n1, n1);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
        assertNotEquals(n1, n4);
    }

    @Test
    public void equals_Name() {

        TableFQName n1 = TableFQName.forName("t1");
        TableFQName n2 = TableFQName.forName("t1");
        TableFQName n3 = TableFQName.forName("t2");

        assertEquals(n1, n1);
        assertEquals(n1, n2);
        assertNotEquals(n1, n3);
    }

    @Test
    public void hashCode_CatalogSchemaName() {

        TableFQName n1 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t1");
        TableFQName n2 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t1");
        TableFQName n3 = TableFQName.forCatalogSchemaAndName("c2", "s1", "t1");
        TableFQName n4 = TableFQName.forCatalogSchemaAndName("c1", "s2", "t1");
        TableFQName n5 = TableFQName.forCatalogSchemaAndName("c1", "s1", "t2");

        assertEquals(n1.hashCode(), n1.hashCode());
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1.hashCode(), n3.hashCode());
        assertNotEquals(n1.hashCode(), n4.hashCode());
        assertNotEquals(n1.hashCode(), n5.hashCode());
    }

    @Test
    public void hashCode_CatalogName() {

        TableFQName n1 = TableFQName.forCatalogAndName("c1", "t1");
        TableFQName n2 = TableFQName.forCatalogAndName("c1", "t1");
        TableFQName n3 = TableFQName.forCatalogAndName("c2", "t1");
        TableFQName n4 = TableFQName.forCatalogAndName("c1", "t2");

        assertEquals(n1.hashCode(), n1.hashCode());
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1.hashCode(), n3.hashCode());
        assertNotEquals(n1.hashCode(), n4.hashCode());
    }

    @Test
    public void hashCode_SchemaName() {

        TableFQName n1 = TableFQName.forSchemaAndName("s1", "t1");
        TableFQName n2 = TableFQName.forSchemaAndName("s1", "t1");
        TableFQName n3 = TableFQName.forSchemaAndName("s2", "t1");
        TableFQName n4 = TableFQName.forSchemaAndName("s1", "t2");

        assertEquals(n1.hashCode(), n1.hashCode());
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1.hashCode(), n3.hashCode());
        assertNotEquals(n1.hashCode(), n4.hashCode());
    }

    @Test
    public void hashCode_Name() {

        TableFQName n1 = TableFQName.forName("t1");
        TableFQName n2 = TableFQName.forName("t1");
        TableFQName n3 = TableFQName.forName("t2");

        assertEquals(n1.hashCode(), n1.hashCode());
        assertEquals(n1.hashCode(), n2.hashCode());
        assertNotEquals(n1.hashCode(), n3.hashCode());
    }

}
