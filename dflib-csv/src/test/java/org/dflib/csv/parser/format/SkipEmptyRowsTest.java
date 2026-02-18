package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class SkipEmptyRowsTest {

    @Test
    void skipEmptyWithComments() {
        String csv = """

                # comment before data
                1,A

                # comment between rows
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column(0).name("id"))
                .column(CsvFormat.column(1).name("name"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .enableComments("#")
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyAtStart() {
        String csv = """

                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .column(CsvFormat.column(0).name("id"))
                .column(CsvFormat.column(1).name("name"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyAutoHeader() {
        String csv = """

                id,name
                1,A

                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyHeaderComments() {
        String csv = """

                # comment before header
                id,name
                1,A

                # comment between rows
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .enableComments("#")
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyCrBreak() {
        String csv = "id,name\r\r1,A\r\r2,B\r";

        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CR)
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyCrlfBreak() {
        String csv = "id,name\r\n\r\n1,A\r\n\r\n2,B\r\n";

        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .skipEmptyRows()
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void skipEmptyLimitOffset() {
        String csv = """
                id,name

                1,A

                2,B
                3,C
                """;

        CsvFormat format = CsvFormat.builder()
                .skipEmptyRows()
                .offset(1)
                .limit(1)
                .build();

        new DfParserAsserts(csv, format, "1", "A")
                .expectHeight(1)
                .expectRow(0, "2", "B");
    }
}
