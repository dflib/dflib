package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvLoader_ImperfectSourceTest {

    static final ByteSource INCOMPLETE_ROWS = ByteSource.of("""
            A,B,C
            0,1,2
            3,4
            5""".getBytes());

    @Test
    public void duplicateColumnNames() {
        String csv = """
                A,A,A
                1,2,3
                4,5,6""";

        DataFrame df = new CsvLoader().load(ByteSource.of(csv.getBytes()));

        // if the duplicate column names are present, the following would throw
        assertNotNull(df.getColumn("A"));
        assertNotNull(df.getColumn("A_"));
        assertNotNull(df.getColumn("A__"));

        new DataFrameAsserts(df, "A", "A_", "A__")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void incompleteRows() {
        DataFrame df = new CsvLoader().load(INCOMPLETE_ROWS);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(3)
                .expectRow(0, "0", "1", "2")
                .expectRow(1, "3", "4", null)
                .expectRow(2, "5", null, null);
    }

    @Test
    public void incompleteRows_Primitives() {
        IllegalStateException e = assertThrows(IllegalStateException.class, () ->
                new CsvLoader().intCol("C").load(INCOMPLETE_ROWS));
        assertEquals("Can't convert value of column 'C': missing or null value, and no default is set for the int " +
                "column (row 3): 3,4", e.getMessage());
    }

    @Test
    public void incompleteRows_PrimitiveDefaults() {
        DataFrame df = new CsvLoader()
                .intCol("C", -1)
                .load(INCOMPLETE_ROWS);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(3)
                .expectRow(0, "0", "1", 2)
                .expectRow(1, "3", "4", -1)
                .expectRow(2, "5", null, -1);
    }

    @Test
    public void headerMismatch() {
        ByteSource csv = ByteSource.of("""
                0,1
                2,3""".getBytes());

        DataFrame df = new CsvLoader()
                .header("A", "B", "C")
                .load(csv);

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(2)
                .expectRow(0, "0", "1", null)
                .expectRow(1, "2", "3", null);
    }

    @Test
    public void whitespaceAfterClosingQuote() {
        String csv = "\"a\" ,b\n\"x\"  ,\"y\"\t\n1,\"2\" \r\n\"3\"\t\t,4 ";

        DataFrame df = new CsvLoader().load(ByteSource.of(csv.getBytes()));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "x", "y")
                .expectRow(1, "1", "2")
                .expectRow(2, "3", "4 ");
    }

    @Test
    public void whitespaceAfterClosingQuote_AtEof() {
        String csv = "a,b\n1,\"2\"  ";

        DataFrame df = new CsvLoader().load(ByteSource.of(csv.getBytes()));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    public void whitespaceAfterClosingQuote_TabDelimiter() {
        String csv = "a\tb\n\"1\" \t\"2\" \n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().delimiter("\t").build())
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    public void whitespaceAfterClosingQuote_MultiCharDelimiter() {
        String csv = "a||b\n\"1\" ||\"2\"\t\n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().delimiter("||").build())
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    public void whitespaceAfterClosingQuote_Escape() {
        String csv = "a,b\n\"1\\\"\" ,\"2\" \n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build())
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1\"", "2");
    }

    @Test
    public void whitespaceAfterClosingQuote_ExplicitLineBreak() {
        String csv = "a,b\r\n\"1\" ,\"2\" \r\n3,4\r\n";

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build())
                .load(ByteSource.of(csv.getBytes()));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }
}
