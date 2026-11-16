package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class RaggedRowsTest {

    @Test
    void missingTrailingColumns() {
        String csv = """
                id,name,age
                1,A,10
                2,B
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("age"))
                .build();

        new DfParserAsserts(csv, format, "id", "name", "age")
                .expectHeight(2)
                .expectRow(0, "1", "A", "10")
                .expectRow(1, "2", "B", null);
    }

    @Test
    void multipleMissingColumns() {
        String csv = """
                id,name,age,city
                1,A,10,Boston
                2,B
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("age"))
                .column(CsvColumnMapping.column("city"))
                .build();

        new DfParserAsserts(csv, format, "id", "name", "age", "city")
                .expectHeight(2)
                .expectRow(0, "1", "A", "10", "Boston")
                .expectRow(1, "2", "B", null, null);
    }

    @Test
    void inconsistentColumnCount() {
        String csv = """
                id,name,value,extra
                1,A,foo
                2,B,bar,extraVal,unused
                3,,baz
                4,D,bar,extra
                """;

        new DfParserAsserts(csv, "id", "name", "value", "extra", "c4")
                .expectHeight(4)
                .expectRow(0, "1", "A", "foo", null, null)
                .expectRow(1, "2", "B", "bar", "extraVal", "unused")
                .expectRow(2, "3", "", "baz", null, null)
                .expectRow(3, "4", "D", "bar", "extra", null);
    }
}
