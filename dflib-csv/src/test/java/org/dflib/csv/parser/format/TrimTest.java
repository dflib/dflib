package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class TrimTest {

    @Test
    void trimAndQuoteBehavior() {
        CsvFormat format = CsvFormat.defaultFormat().trim(Trim.FULL).build();

        String csv = """
                id,name,value
                " 1 ","  Name 1  ","  Value 1  "
                "2 ","Name, 2","Value 2  "
                3 ,  Name 3  ,  Value 3
                """;

        new DfParserAsserts(csv, format, "id", "name", "value")
                .expectHeight(3)
                .expectRow(0, " 1 ", "  Name 1  ", "  Value 1  ")
                .expectRow(1, "2 ", "Name, 2", "Value 2  ")
                .expectRow(2, "3", "Name 3", "Value 3");
    }

    @Test
    void trimNone() {
        CsvFormat none = CsvFormat.defaultFormat().trim(Trim.NONE).build();

        String csv = """
                id,name,value
                1,  NameL  ,  Val
                2,NameR  ,  Val
                3,  NameBoth  ,  ValBoth
                """;

        new DfParserAsserts(csv, none, "id", "name", "value")
                .expectColumn("name", "  NameL  ", "NameR  ", "  NameBoth  ");
    }

    @Test
    void trimFull() {
        CsvFormat full = CsvFormat.defaultFormat().trim(Trim.FULL).build();

        String csv = """
                id,name,value
                1,  NameL  ,  Val
                2,NameR  ,  Val
                3,  NameBoth  ,  ValBoth
                """;

        new DfParserAsserts(csv, full, "id", "name", "value")
                .expectColumn("name", "NameL", "NameR", "NameBoth");
    }

    @Test
    void trimLeft() {
        CsvFormat left = CsvFormat.defaultFormat().trim(Trim.LEFT).build();

        String csv = """
                id,name,value
                1,  NameL  ,  Val
                2,NameR  ,  Val
                3,  NameBoth  ,  ValBoth
                """;

        new DfParserAsserts(csv, left, "id", "name", "value")
                .expectColumn("name", "NameL  ", "NameR  ", "NameBoth  ");
    }

    @Test
    void trimRight() {
        CsvFormat right = CsvFormat.defaultFormat().trim(Trim.RIGHT).build();

        String csv = """
                id,name,value
                1,  NameL  ,  Val
                2,NameR  ,  Val
                3,  NameBoth  ,  ValBoth
                """;

        new DfParserAsserts(csv, right, "id", "name", "value")
                .expectColumn("name", "  NameL", "NameR", "  NameBoth");
    }

    @Test
    void perColumnTrim() {
        String csv = """
                id,name
                 1 ,  A\s
                """;

        CsvParserConfig format = CsvParserConfig.builder()
                .csvFormat(CsvFormat.defaultFormat().trim(Trim.FULL))
                .column(CsvColumnMapping.column("id").format(CsvFormat.columnFormat().trim(Trim.NONE)))
                .column(CsvColumnMapping.column("name"))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(1)
                .expectRow(0, " 1 ", "A");
    }
}
