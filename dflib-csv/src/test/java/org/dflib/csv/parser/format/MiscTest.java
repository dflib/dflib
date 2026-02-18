package org.dflib.csv.parser.format;

import org.dflib.csv.parser.test.DfParserAsserts;
import org.junit.jupiter.api.Test;

class MiscTest {

    private static String csv100Rows() {
        StringBuilder out = new StringBuilder("id,name,value\n");
        for (int i = 1; i <= 100; i++) {
            out.append(i).append(",Name ").append(i).append(",Value ").append(i).append('\n');
        }
        return out.toString();
    }

    @Test
    void parseDefaultFormat() {
        new DfParserAsserts(csv100Rows(), "id", "name", "value")
                .expectHeight(100)
                .expectRow(0, "1", "Name 1", "Value 1")
                .expectRow(49, "50", "Name 50", "Value 50")
                .expectRow(99, "100", "Name 100", "Value 100");
    }

    @Test
    void parseStringReader() {
        String csv = """
                Year,Age,Ethnic,Sex,Area,count
                2018,"000",1,1,01,795
                2019,"1",2,2,02,6
                """;
        new DfParserAsserts(csv, "Year", "Age", "Ethnic", "Sex", "Area", "count")
                .expectHeight(2);
    }

    @Test
    void emptyCellsDefaultEmpty() {
        String csv = """
                id,name,value
                1,,foo
                2,B,
                3,,
                """;

        new DfParserAsserts(csv, "id", "name", "value")
                .expectHeight(3)
                .expectRow(0, "1", "", "foo")
                .expectRow(1, "2", "B", "")
                .expectRow(2, "3", "", "");
    }

    @Test
    void earlyOut() {
        String csv = """
                id,val,name
                1,
                ,name1
                2,val2,name2
                """;

        new DfParserAsserts(csv, "id", "val", "name")
                .expectHeight(2)
                .expectColumn("id", "1", "2")
                .expectColumn("val", "\n", "val2")
                .expectColumn("name", "name1", "name2");
    }

    @Test
    void handlesSymbolsUnicode() {
        String csv = """
                állo,teÔst,☀,#  Aₐₓ #,ᴴᵉᴸᴸᵒ   ᵂᵒᴿᴸᵈ , Qty (m²)
                1,2,☀,Aₐₓ,ᴴᵉᴸᴸᵒ   ᵂᵒᴿᴸᵈ,100
                """;
        new DfParserAsserts(csv, "állo", "teÔst", "☀", "#  Aₐₓ #", "ᴴᵉᴸᴸᵒ   ᵂᵒᴿᴸᵈ ", " Qty (m²)")
                .expectHeight(1)
                .expectRow(0, "1", "2", "☀", "Aₐₓ", "ᴴᵉᴸᴸᵒ   ᵂᵒᴿᴸᵈ", "100");
    }

    @Test
    void largeCellBufferGrowth() {
        // Create a large single cell near buffer size to ensure growth logic runs without overflow
        int size = 9000; // larger than INITIAL_BUFFER_SIZE but far below MAX_BUFFER_SIZE
        String value = "a".repeat(size);
        String csv = "col\n\"" + value + "\"\n";

        new DfParserAsserts(csv, "col")
                .expectHeight(1)
                .expectRow(0, value);
    }

    @Test
    void sizeHintIgnored() {
        String csv = """
                id,name
                1,A
                2,B
                """;

        CsvFormat format = CsvFormat.builder()
                .sizeHint(2)
                .build();

        new DfParserAsserts(csv, format, "id", "name")
                .expectHeight(2)
                .expectRow(0, "1", "A")
                .expectRow(1, "2", "B");
    }

    @Test
    void emptyColumns() {
        String csv = """
                a,b,c,d
                ,mid,,
                """;

        new DfParserAsserts(csv, "a", "b", "c", "d")
                .expectHeight(1)
                .expectRow(0, "", "mid", "", "");
    }
}
