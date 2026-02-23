package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class HeaderTest {

    private static String csv100Rows() {
        StringBuilder out = new StringBuilder("id,name,value\n");
        for (int i = 1; i <= 100; i++) {
            out.append(i).append(",Name ").append(i).append(",Value ").append(i).append('\n');
        }
        return out.toString();
    }

    @Test
    void excludeHeaderValues() {
        CsvParserConfig format = CsvParserConfig.builder()
                .autoColumns(false)
                .excludeHeaderValues(false)
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("value"))
                .build();

        new DfParserAsserts(csv100Rows(), format, "id", "name", "value")
                .expectHeight(101)
                .expectRow(0, "id", "name", "value");
    }

    @Test
    void includeHeaderValues() {
        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name"))
                .column(CsvColumnMapping.column("value"))
                .build();

        String csv = """
                id,name,value
                1,Name 1,Value 1
                2,Name 2,Value 2
                3,Name 3,Value 3
                4,Name 4,Value 4
                5,Name 5,Value 5
                6,Name 6,Value 6
                7,Name 7,Value 7
                8,Name 8,Value 8
                9,Name 9,Value 9
                10,Name 10,Value 10
                """;

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectHeight(10)
                .expectRow(0, "1", "Name 1", "Value 1");
    }

    @Test
    void duplicateNamesInHeader() {
        String csv = """
                id,name,name
                1,A,foo
                2,B,bar
                3,C,baz
                """;

        new DfParserAsserts(csv, "id", "name", "name_")
                .expectHeight(3);
    }

    @Test
    void headerValuesIncluded() {
        CsvParserConfig excluded = CsvParserConfig.builder()
                .autoColumns(true)
                .excludeHeaderValues(true)
                .build();

        new DfParserAsserts(csv100Rows(), excluded, "id", "name", "value")
                .expectHeight(100)
                .expectRow(0, "1", "Name 1", "Value 1");

        CsvParserConfig included = CsvParserConfig.builder()
                .autoColumns(true)
                .excludeHeaderValues(false)
                .build();

        new DfParserAsserts(csv100Rows(), included, "c0", "c1", "c2")
                .expectHeight(101)
                .expectRow(0, "id", "name", "value");
    }

    @Test
    void includeHeaderAutoColumns() {
        String csv = """
                id,name,value
                1,A,foo
                2,B,bar
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .excludeHeaderValues(false)
                .autoColumns(true)
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(3)
                .expectRow(0, "id", "name", "value");
    }

    @Test
    void includeHeaderIndexColumns() {
        String csv = """
                id,name,value
                1,A,foo
                2,B,bar
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .excludeHeaderValues(false)
                .autoColumns(false)
                .column(CsvColumnMapping.column(0))
                .column(CsvColumnMapping.column(1))
                .column(CsvColumnMapping.column(2))
                .build();

        new DfParserAsserts(csv, format, "c0", "c1", "c2")
                .expectHeight(3)
                .expectRow(0, "id", "name", "value");
    }

    @Test
    void emptyHeader() {
        String csv = """
                ,,
                123,TEST,text1
                345,TEST,text2
                """;

        new DfParserAsserts(csv, "", "_", "__")
                .expectHeight(2)
                .expectRow(0, "123", "TEST", "text1")
                .expectRow(1, "345", "TEST", "text2");
    }

    @Test
    void missingHeaderColumn() {
        String csv = """
                a,,c
                123,TEST,text1
                345,TEST,text2
                """;

        new DfParserAsserts(csv, "a", "", "c")
                .expectHeight(2)
                .expectRow(0, "123", "TEST", "text1")
                .expectRow(1, "345", "TEST", "text2");
    }
}
