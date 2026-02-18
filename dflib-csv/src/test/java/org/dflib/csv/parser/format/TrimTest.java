package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class TrimTest {

    @Test
    void trimAndQuoteBehavior() {
        CsvFormat format = CsvFormat.builder()
                .trim(Trim.FULL)
                .build();

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
        CsvFormat none = CsvFormat.builder()
                .trim(Trim.NONE)
                .build();

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
        CsvFormat full = CsvFormat.builder()
                .trim(Trim.FULL)
                .build();

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
        CsvFormat left = CsvFormat.builder()
                .trim(Trim.LEFT)
                .build();

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
        CsvFormat right = CsvFormat.builder()
                .trim(Trim.RIGHT)
                .build();

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

        CsvFormat format = CsvFormat.builder()
                .trim(Trim.FULL)
                .column(CsvFormat.column("id").trim(Trim.NONE))
                .column(CsvFormat.column("name"))
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(1)
                .expectRow(0, " 1 ", "A");
    }
}
