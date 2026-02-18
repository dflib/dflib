package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CsvLoader_EdgeCasesTest {

    @Test
    void emptyCSV_header() {
        String csv = "A,B\n";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(0);
    }

    @Test
    void emptyCSV_header_noTrailingNewline() {
        String csv = "A,B";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(0);
    }

    @Test
    void singleColumn() {
        String csv = "A\n1\n2\n3";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A")
                .expectHeight(3)
                .expectRow(0, "1")
                .expectRow(1, "2")
                .expectRow(2, "3");
    }

    @Test
    void singleRow() {
        String csv = "A,B,C\n1,2,3";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    void singleCell() {
        String csv = "A\n1";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A")
                .expectHeight(1)
                .expectRow(0, "1");
    }

    @Test
    void offsetPastAllRows() {
        String csv = "A,B\n1,2\n3,4";

        DataFrame df = new CsvLoader()
                .offset(10)
                .generateHeader()
                .load(new StringReader(csv));

        assertEquals(0, df.height());
    }

    @Test
    void limitZero_emptyResult() {
        String csv = "A,B\n1,2\n3,4";

        DataFrame df = new CsvLoader()
                .limit(0)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(0);
    }

    @Test
    void generateHeader_limitZero_emptyResult() {
        String csv = "1,2\n3,4";

        DataFrame df = new CsvLoader()
                .generateHeader()
                .limit(0)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(0);
    }

    @Test
    void limit_malformedTail() {
        String csv = "A,B\n1,2\n\"bad";

        assertThrows(IllegalStateException.class, () -> new CsvLoader()
                .limit(1)
                .load(new StringReader(csv)));
    }

    @Test
    void limit_stopsBeforeMalformedTail() {
        String csv = "A,B\n1,2\n3,4\n\"bad";

        DataFrame df = new CsvLoader()
                .limit(1)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    void generateHeader_limit_stopsBeforeMalformedTail() {
        String csv = "1,2\n3,4\n\"bad";

        DataFrame df = new CsvLoader()
                .generateHeader()
                .limit(1)
                .load(new StringReader(csv));

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    void trailingNewline_one() {
        // single trailing newline should not produce extra rows
        String csv = "A,B\n1,2\n3,4\n";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void trailingNewline_many() {
        // multiple trailing newline should not produce extra rows
        String csv = "A,B\n1,2\n3,4\n\n\n";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void encoding_name() {
        byte[] bytes = "A,B\n1,2\n3,4".getBytes(StandardCharsets.UTF_16BE);

        DataFrame df = new CsvLoader()
                .encoding("UTF-16BE")
                .load(ByteSource.of(bytes));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void quotedFieldsWithCommas() {
        String csv = "A,B\n\"hello, world\",2\n3,\"a,b,c\"";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "hello, world", "2")
                .expectRow(1, "3", "a,b,c");
    }

    @Test
    void quotedFieldsWithNewlines() {
        String csv = "A,B\n\"line1\nline2\",2\n3,4";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "line1\nline2", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void quotedFieldsWithEscapedQuotes() {
        String csv = "A,B\n\"say \"\"hello\"\"\",2\n3,4";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, "say \"hello\"", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void whitespaceValues() {
        String csv = "A,B\n ,  \n   ,x";

        DataFrame df = new CsvLoader().load(new StringReader(csv));

        new DataFrameAsserts(df, "A", "B")
                .expectHeight(2)
                .expectRow(0, " ", "  ")
                .expectRow(1, "   ", "x");
    }

    @Test
    void largeNumberOfColumns() {
        StringBuilder header = new StringBuilder();
        StringBuilder row = new StringBuilder();
        for (int i = 0; i < 100; i++) {
            if (i > 0) {
                header.append(",");
                row.append(",");
            }
            header.append("c").append(i);
            row.append(i);
        }
        String csv = header + "\n" + row;

        DataFrame df = new CsvLoader().load(new StringReader(csv));
        assertEquals(100, df.width());
        assertEquals(1, df.height());
        assertEquals("0", df.getColumn("c0").get(0));
        assertEquals("99", df.getColumn("c99").get(0));
    }
}
