package org.dflib.csv.parser.format;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CsvColumnsBuilderTest {

    @Test
    void constructorStartsEmpty() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        assertTrue(columns.columns.isEmpty());
    }

    @Test
    void mergeNull() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        assertThrows(NullPointerException.class, () -> columns.merge((CsvColumnFormat.Builder)null));
    }

    @Test
    void mergeByIndexNew() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder next = CsvFormat.column(0)
                .name("A")
                .type(CsvColumnType.INTEGER);

        columns.merge(next);

        assertEquals(1, columns.columns.size());
        assertIndexes(columns);
        CsvColumnFormat added = columns.columns.get(0).build();
        assertEquals("A", added.name());
        assertEquals(0, added.index());
        assertEquals(CsvColumnType.INTEGER, added.type());
    }

    @Test
    void mergeByIndexedExisting() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder existing = CsvFormat.column(0)
                .name("A")
                .type(CsvColumnType.INTEGER)
                .skip();
        CsvColumnFormat.Builder incoming = CsvFormat.column(0)
                .name("A")
                .type(CsvColumnType.DOUBLE)
                .nullable(false);

        columns.merge(existing);
        columns.merge(incoming);

        assertEquals(1, columns.columns.size());
        assertIndexes(columns);
        CsvColumnFormat merged = columns.columns.get(0).build();
        assertEquals("A", merged.name());
        assertEquals(0, merged.index());
        assertEquals(CsvColumnType.DOUBLE, merged.type());
        assertTrue(merged.skip());
    }

    @Test
    void mergeByIndexWithGap() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        columns.merge(CsvFormat.column(0)
                .name("A"));
        CsvColumnFormat.Builder next = CsvFormat.column(3)
                .name("D")
                .type(CsvColumnType.LONG);

        columns.merge(next);

        assertEquals(4, columns.columns.size());
        assertIndexes(columns);
        CsvColumnFormat filler1 = columns.columns.get(1).build();
        CsvColumnFormat filler2 = columns.columns.get(2).build();
        CsvColumnFormat target = columns.columns.get(3).build();

        assertNull(filler1.name());
        assertEquals(CsvColumnType.STRING, filler1.type());
        assertNull(filler2.name());
        assertEquals(CsvColumnType.STRING, filler2.type());

        assertEquals("D", target.name());
        assertEquals(3, target.index());
        assertEquals(CsvColumnType.LONG, target.type());
    }

    @Test
    void mergeByNameNoIndex() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder next = CsvFormat.column("A")
                .type(CsvColumnType.INTEGER);

        columns.merge(next);
        assertEquals(1, columns.columns.size());
        assertIndexes(columns);
        CsvColumnFormat added = columns.columns.get(0).build();
        assertEquals("A", added.name());
        assertEquals(0, added.index());
        assertEquals(CsvColumnType.INTEGER, added.type());
    }

    @Test
    void mergeByNameExisting() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder existing = CsvFormat.column(0)
                .name("A")
                .type(CsvColumnType.INTEGER)
                .skip();
        CsvColumnFormat.Builder incoming = CsvFormat.column("A")
                .type(CsvColumnType.DOUBLE)
                .nullable(false);

        columns.merge(existing);
        columns.merge(incoming);

        assertEquals(1, columns.columns.size());
        assertIndexes(columns);
        CsvColumnFormat merged = columns.columns.get(0).build();
        assertEquals("A", merged.name());
        assertEquals(0, merged.index());
        assertEquals(CsvColumnType.DOUBLE, merged.type());
        assertFalse(merged.skip());
    }

    @Test
    void mergeByNameNotExisting() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder first = CsvFormat.column("A")
                .type(CsvColumnType.INTEGER);
        CsvColumnFormat.Builder second = CsvFormat.column("B")
                .type(CsvColumnType.DOUBLE);

        columns.merge(first);
        columns.merge(second);

        assertEquals(2, columns.columns.size());
        assertIndexes(columns);
        assertEquals("A", columns.columns.get(0).build().name());
        assertEquals("B", columns.columns.get(1).build().name());
        assertEquals(CsvColumnType.INTEGER, columns.columns.get(0).build().type());
        assertEquals(CsvColumnType.DOUBLE, columns.columns.get(1).build().type());
    }

    @Test
    void mergeMixed() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();

        CsvColumnFormat.Builder id = CsvFormat.column("id")
                .type(CsvColumnType.INTEGER);
        CsvColumnFormat.Builder byIndexGap = CsvFormat.column(2)
                .name("value")
                .type(CsvColumnType.DOUBLE);
        CsvColumnFormat.Builder rename = CsvFormat.column(2)
                .name("amount")
                .type(CsvColumnType.STRING);

        columns.merge(id);
        columns.merge(byIndexGap);
        columns.merge(rename);

        assertEquals(3, columns.columns.size());
        assertIndexes(columns);
        assertEquals("id", columns.columns.get(0).build().name());
        assertNull(columns.columns.get(1).build().name());
        assertEquals(CsvColumnType.STRING, columns.columns.get(1).build().type());
        assertEquals("amount", columns.columns.get(2).build().name());
        assertEquals(CsvColumnType.STRING, columns.columns.get(2).build().type());
    }

    @Test
    void mergeByNameAndIndex() {
        CsvColumnsBuilder columns = new CsvColumnsBuilder();
        CsvColumnFormat.Builder existing = CsvFormat.column(0)
                .name("A")
                .type(CsvColumnType.INTEGER);
        CsvColumnFormat.Builder incoming = CsvFormat.column(1)
                .name("B")
                .type(CsvColumnType.STRING);

        columns.merge(existing);
        columns.merge(incoming);

        assertEquals(2, columns.columns.size());
        assertIndexes(columns);
        assertEquals("A", columns.columns.get(0).build().name());
        assertEquals("B", columns.columns.get(1).build().name());
        assertEquals(CsvColumnType.INTEGER, columns.columns.get(0).build().type());
        assertEquals(CsvColumnType.STRING, columns.columns.get(1).build().type());
    }


    @Test
    void testAutoColumnsWithOverrides() {
        CsvFormat.builder()
                .autoColumns(true)
                .excludeHeaderValues(true);


        // HeaderMode.FIRST_ROW
        //
        // HeaderMode.EXPLICIT
        // HeaderMode.GENERATED
    }


    static void assertIndexes(CsvColumnsBuilder columns) {
        for(int i = 0; i < columns.columns.size(); i++) {
            assertEquals(i, columns.columns.get(i).idx);
        }
    }
}
