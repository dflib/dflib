package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class ColumnByIndexTest {

    @Test
    void nullableByIndex() {
        String csv = """
                id,name,age
                1,NA,10
                2,B,20
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1).nullable(true, "NA"))
                .column(CsvFormat.column(2))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectRow(0, "1", null, "10")
                .expectRow(1, "2", "B", "20");
    }

    @Test
    void trimQuoteByIndex() {
        String csv = """
                id,name
                 1 , "A"
                """;

        CsvFormat format = CsvFormat.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .trim(Trim.FULL)
                .quote(Quote.optionalOf('"'))
                .column(CsvFormat.column(0).trim(Trim.NONE))
                .column(CsvFormat.column(1).quote(Quote.none()))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, " 1 ", "\"A\"");
    }

    @Test
    void allowEmptyByIndex() {
        String csv = """
                id,name,age
                1,A,10
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1))
                .column(CsvFormat.column(2))
                .allowEmptyColumns()
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "A", "10")
                .expectRow(1, "2", "B", null);
    }
}
