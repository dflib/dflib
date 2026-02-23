package org.dflib.csv.parser.format;

import org.dflib.csv.parser.CsvSchemaFactory;
import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class WidthDetectionTest {

    @Test
    void exactMatch() {
        String csv = """
                a,b,c
                1,2,3
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvColumnMapping.column("x"))
                .column(CsvColumnMapping.column("y"))
                .column(CsvColumnMapping.column("z"))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvColumnMapping.column("id").index(0))
                .column(CsvColumnMapping.column("name").index(1))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("extra").nullable(true))
                .csvFormat(CsvFormat.defaultFormat().allowEmptyColumns())
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvColumnMapping.column(0).type(CsvColumnType.INTEGER))
                .column(CsvColumnMapping.column(1).type(CsvColumnType.STRING))
                .column(CsvColumnMapping.column(2).type(CsvColumnType.DOUBLE).nullableWithDefault(true, 0.0))
                .csvFormat(CsvFormat.defaultFormat().allowEmptyColumns())
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .column(CsvColumnMapping.column("c1"))
                .column(CsvColumnMapping.column("c2"))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(true)
                .offset(1)
                .limit(1)
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().allowEmptyColumns())
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
                .column(CsvColumnMapping.column(2))
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

        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat().trim(Trim.FULL))
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1")
                .expectHeight(2)
                .expectRow(0, " a ", "c")
                .expectRow(1, " x ", "y");
    }
}
