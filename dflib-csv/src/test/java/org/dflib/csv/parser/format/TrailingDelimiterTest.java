package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class TrailingDelimiterTest {

    @Test
    void defaultKeepsTrailingDelimiterAsEmptyColumn() {
        String csv = """
                1,2,
                3,4,
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .allowEmptyColumns()
                .column(CsvFormat.column("c0"))
                .column(CsvFormat.column("c1"))
                .column(CsvFormat.column("c2"))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", "")
                .expectRow(1, "3", "4", "");
    }

    @Test
    void enabledIgnoresTrailingDelimiter() {
        String csv = """
                c0,c1,
                1,2,
                3,4,
                """;

        CsvFormat format = CsvFormat.builder()
                .trailingDelimiter(true)
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void enabledIgnoresTrailingDelimiterAtEof() {
        String csv = """
                c0,c1,
                1,2,
                3,4,""";

        CsvFormat format = CsvFormat.builder()
                .trailingDelimiter(true)
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void enabledAllowsTrailingDelimiterAfterQuotedLastColumn() {
        String csv = "\"a\",\n\"b\",";

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .trailingDelimiter(true)
                .column(CsvFormat.column("c0"))
                .build();

        new DfParserAsserts(csv, format, "c0")
                .expectHeight(2)
                .expectRow(0, "a")
                .expectRow(1, "b");
    }

    @Test
    void enabledDoesNotTrimEscapedTrailingDelimiter() {
        String csv = "v\\,\n";

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .trailingDelimiter(true)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .column(CsvFormat.column("c0"))
                .build();

        new DfParserAsserts(csv, format, "c0")
                .expectHeight(1)
                .expectRow(0, "v,");
    }
}
