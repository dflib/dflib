package org.dflib.csv.parser.format;

import org.dflib.csv.parser.CsvSchemaFactory;
import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertThrows;

class ColumnDefinitionTest {

    private static String csv100Rows() {
        StringBuilder out = new StringBuilder("id,name,value\n");
        for (int i = 1; i <= 100; i++) {
            out.append(i).append(",Name ").append(i).append(",Value ").append(i).append('\n');
        }
        return out.toString();
    }

    @Test
    void parseSkipColumn() {
        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name").skip())
                .column(CsvColumnMapping.column("value"))
                .schemaFactory(CsvSchemaFactory.ofCols("id", "value"))
                .build();

        new DfParserAsserts(csv100Rows(), format, "id", "value")
                .expectHeight(100);
    }

    @Test
    void autoColumnsByDefault() {
        //  Here we do not specify the "name" column, relying on autoColumns=true by default
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

        new DfParserAsserts(csv, CsvParserConfig.builder(), "id", "name", "value")
                .expectHeight(10)
                .expectRow(0, "1", "Name 1", "Value 1");
    }

    @Test
    void autoDisabledWithColumns() {
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
    void autoDisabledNoColumns() {
        //  Here we do not specify the "name" column, with `autoColumns`=false
        assertThrows(IllegalArgumentException.class, () -> CsvParserConfig.builder()
                .autoColumns(false)
                .build(), "Expected IllegalArgumentException when autoColumns is false and no columns are defined manually");
    }

    @Test
    void skipTypedNullableColumn() {
        String csv = """
                id,name,age
                1,NA,30
                2,B,40
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column("id"))
                .column(CsvColumnMapping.column("name")
                        .skip()
                        .nullable(true)
                        .format(CsvFormat.columnFormat().nullString("NA").build()))
                .column(CsvColumnMapping.column("age").type(CsvColumnType.INTEGER))
                .schemaFactory(CsvSchemaFactory.ofCols("id", "age"))
                .build();

        new DfParserAsserts(csv, format, "id", "age")
                .expectHeight(2)
                .expectRow(0, "1", 30)
                .expectRow(1, "2", 40);
    }

    @Test
    void skipColumnByIndex() {
        String csv = """
                id,name,age
                1,A,10
                2,B,20
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("id"))
                .column(CsvColumnMapping.column(1).name("name").skip())
                .column(CsvColumnMapping.column(2).name("age"))
                .schemaFactory(CsvSchemaFactory.ofCols("id", "age"))
                .build();

        new DfParserAsserts(csv, format, "id", "age")
                .expectHeight(2)
                .expectRow(0, "1", "10")
                .expectRow(1, "2", "20");
    }

}
