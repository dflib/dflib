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

        CsvParserConfig format = CsvParserConfig.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1).format(CsvFormat.columnFormat().nullString("NA").build()).nullable(true))
                .column(CsvColumnMapping.column(2))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .excludeHeaderValues(true)
                .autoColumns(false)
                .csvFormat(CsvFormat.defaultFormat()
                        .trim(Trim.FULL)
                        .quote(Quote.optionalOf('"'))
                        .build()
                )
                .column(CsvColumnMapping.column(0).format(CsvFormat.columnFormat().trim(Trim.NONE).build()))
                .column(CsvColumnMapping.column(1).format(CsvFormat.columnFormat().quote(Quote.none()).build()))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
                .column(CsvColumnMapping.column(2))
                .csvFormat(CsvFormat.defaultFormat().allowEmptyColumns().build())
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "A", "10")
                .expectRow(1, "2", "B", null);
    }
}
