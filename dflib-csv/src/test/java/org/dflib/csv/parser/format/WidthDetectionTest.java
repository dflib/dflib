package org.dflib.csv.parser.format;

import org.dflib.csv.CsvSchemaFactory;
import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class WidthDetectionTest {

    @Test
    void exactMatch() {
        String csv = """
                a,b,c
                1,2,3
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column("x"))
                .column(CsvFormat.column("y"))
                .column(CsvFormat.column("z"))
                .build();

        new DfParserAsserts(csv, format, "x", "y", "z")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    void csvWiderThanUser() {
        String csv = """
                1,A,10,extra
                2,B,20,more
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvFormat.column("id").index(0))
                .column(CsvFormat.column("name").index(1))
                .schemaFactory(CsvSchemaFactory.ofCols("id", "name"))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void csvNarrowerThanUser() {
        String csv = """
                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvFormat.column("id"))
                .column(CsvFormat.column("name"))
                .column(CsvFormat.column("extra").nullable(true))
                .allowEmptyColumns()
                .build();

        new DfParserAsserts(csv, format, "id", "name", "extra")
                .expectHeight(2)
                .expectRow(0, "1", "A", null)
                .expectRow(1, "2", "B", null);
    }

    @Test
    void csvNarrowerThanUser_typed() {
        String csv = """
                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvFormat.column(0).type(CsvColumnType.INTEGER))
                .column(CsvFormat.column(1).type(CsvColumnType.STRING))
                .column(CsvFormat.column(2).type(CsvColumnType.DOUBLE).nullableWithDefault(true, 0.0))
                .allowEmptyColumns()
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, 1, "A", 0.0)
                .expectRow(1, 2, "B", 0.0);
    }

    @Test
    void excludeHeaderTrue() {
        String csv = """
                h1,h2
                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvFormat.column("c1"))
                .column(CsvFormat.column("c2"))
                .build();

        new DfParserAsserts(csv, format, "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void excludeHeaderFalse() {
        String csv = """
                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void withOffsetAndLimit() {
        String csv = """
                h,h
                1,A
                2,B
                3,C
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .offset(1)
                .limit(1)
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, "2", "B");
    }

    @Test
    void widthMismatchWithAllowEmptyColumns() {
        String csv = """
                1,A,10
                2,B
                3,C,30
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .allowEmptyColumns()
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1))
                .column(CsvFormat.column(2))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(3)
                .expectRow(0, "1", "A", "10")
                .expectRow(1, "2", "B", null)
                .expectRow(2, "3", "C", "30");
    }

    @Test
    void quotedFirstRow() {
        String csv = """
                " a ",c
                " x ",y
                """;

        CsvFormat format = CsvFormat.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .trim(Trim.FULL)
                .column(CsvFormat.column(0))
                .column(CsvFormat.column(1))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, " a ", "c")
                .expectRow(1, " x ", "y");
    }
}
