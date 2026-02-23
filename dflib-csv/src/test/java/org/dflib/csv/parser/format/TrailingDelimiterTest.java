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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().allowEmptyColumns())
                .column(CsvColumnMapping.column("c0"))
                .column(CsvColumnMapping.column("c1"))
                .column(CsvColumnMapping.column("c2"))
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

        CsvFormat format = CsvFormat.defaultFormat().trailingDelimiter(true).build();

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

        CsvFormat format = CsvFormat.defaultFormat().trailingDelimiter(true).build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void enabledAllowsTrailingDelimiterAfterQuotedLastColumn() {
        String csv = "\"a\",\n\"b\",";

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().trailingDelimiter(true))
                .column(CsvColumnMapping.column("c0"))
                .build();

        new DfParserAsserts(csv, format, "c0")
                .expectHeight(2)
                .expectRow(0, "a")
                .expectRow(1, "b");
    }

    @Test
    void enabledDoesNotTrimEscapedTrailingDelimiter() {
        String csv = "v\\,\n";

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat()
                        .trailingDelimiter(true)
                        .quote(Quote.none())
                        .escape(Escape.BACKSLASH))
                .column(CsvColumnMapping.column("c0"))
                .build();

        new DfParserAsserts(csv, format, "c0")
                .expectHeight(1)
                .expectRow(0, "v,");
    }
}
