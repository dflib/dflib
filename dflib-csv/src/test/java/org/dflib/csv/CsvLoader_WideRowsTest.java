package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.RowPredicate;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Rows with more values than the header: the extra values are loaded as new columns, and the rows before them get
 * nulls in those columns.
 */
public class CsvLoader_WideRowsTest {

    private static ByteSource src(String csv) {
        return ByteSource.of(csv.getBytes());
    }

    @Test
    public void titleAboveHeader() {
        String csv = """
                Some Title
                "",,"c1","c2","c3"
                "r1",1,"x",2,3
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "Some Title", "c1", "c2", "c3", "c4")
                .expectHeight(2)
                .expectRow(0, "", "", "c1", "c2", "c3")
                .expectRow(1, "r1", "1", "x", "2", "3");
    }

    @Test
    public void unquoted() {
        String csv = """
                a,b
                1,2,3
                4,5
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", null);
    }

    @Test
    public void addedLater() {
        String csv = """
                a,b
                1,2
                3,4
                5,6,7
                8,9
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(4)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", null)
                .expectRow(2, "5", "6", "7")
                .expectRow(3, "8", "9", null);
    }

    @Test
    public void addedInSteps() {
        String csv = """
                a,b,c
                1,2,3,4
                5,6,7,8,9,10
                11,12
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c", "c3", "c4", "c5")
                .expectHeight(3)
                .expectRow(0, "1", "2", "3", "4", null, null)
                .expectRow(1, "5", "6", "7", "8", "9", "10")
                .expectRow(2, "11", "12", null, null, null, null);
    }

    @Test
    public void addedAfterManyRows() {
        StringBuilder csv = new StringBuilder("a,b\n");
        for (int i = 0; i < 5000; i++) {
            csv.append(i).append(",x\n");
        }
        csv.append("5000,y,z\n");

        DataFrame df = new CsvLoader().load(new StringReader(csv.toString()));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(5001)
                .expectRow(0, "0", "x", null)
                .expectRow(4999, "4999", "x", null)
                .expectRow(5000, "5000", "y", "z");
    }

    @Test
    public void nameConflict() {
        String csv = """
                c2,a
                1,2,3
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "c2", "a", "c2_")
                .expectHeight(1)
                .expectRow(0, "1", "2", "3");
    }

    @Test
    public void lastColumnQuoted() {
        String csv = """
                a,b
                1,"2",3
                4,"5"
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", null);
    }

    @Test
    public void lastColumnTyped() {
        String csv = """
                a,b
                1,2,x
                3,4
                """;

        DataFrame df = new CsvLoader().intCol("b").load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectIntColumns("b")
                .expectRow(0, "1", 2, "x")
                .expectRow(1, "3", 4, null);
    }

    @Test
    public void quotedExtras() {
        String csv = """
                a,b
                1,2,"x,y","multi
                line",3
                4,5,"he said ""hi"", ok"
                6,7
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2", "c3", "c4")
                .expectHeight(3)
                .expectRow(0, "1", "2", "x,y", "multi\nline", "3")
                .expectRow(1, "4", "5", "he said \"hi\", ok", null, null)
                .expectRow(2, "6", "7", null, null, null);
    }

    @Test
    public void quoteInsideUnquotedExtra() {
        // a quote char in the middle of an unquoted value does not start a quoted value
        String csv = """
                a,b
                1,2,5"inch,x
                3,4
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(2)
                .expectRow(0, "1", "2", "5\"inch", "x")
                .expectRow(1, "3", "4", null, null);
    }

    @Test
    public void emptyExtras() {
        // a trailing empty value doesn't add a column, the empty values before it do
        String csv = """
                a,b
                1,2,
                3,4,,
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "");
    }

    @Test
    public void emptyQuotedExtra() {
        String csv = """
                a,b
                1,2,""
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(1)
                .expectRow(0, "1", "2", "");
    }

    @Test
    public void lastRowWithoutLineBreak() {
        String csv = "a,b\n1,2\n3,4,5";

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5");
    }

    @Test
    public void lastRowWithoutLineBreak_QuotedExtra() {
        String csv = "a,b\n1,2\n3,4,\"5\"";

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5");
    }

    @Test
    public void lastRowWithoutLineBreak_EmptyExtra() {
        String csv = "a,b\n1,2\n3,4,";

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void lastRowWithoutLineBreak_UnclosedQuoteInExtra() {
        String csv = "a,b\n1,2\n3,4,\"5";
        assertThrows(IllegalStateException.class, () -> new CsvLoader().load(src(csv)));
    }

    @Test
    public void trailingDelimiter() {
        String csv = """
                a,b,
                1,2,
                3,4,5,"6,7",
                8,9,
                """;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().trailingDelimiter(true).build())
                .load(src(csv));

        // once a column is added, the trailing delimiter in the rows after it is followed by an empty value
        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(3)
                .expectRow(0, "1", "2", null, null)
                .expectRow(1, "3", "4", "5", "6,7")
                .expectRow(2, "8", "9", "", null);
    }

    @Test
    public void shortAndWideRows() {
        String csv = """
                a,b,c
                1,2
                3,4,5,6
                7,8,9
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c", "c3")
                .expectHeight(3)
                .expectRow(0, "1", "2", null, null)
                .expectRow(1, "3", "4", "5", "6")
                .expectRow(2, "7", "8", "9", null);
    }

    @Test
    public void generateHeader() {
        String csv = """
                1,2
                3,4,5
                """;

        DataFrame df = new CsvLoader().generateHeader().load(src(csv));
        new DataFrameAsserts(df, "c0", "c1", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5");
    }

    @Test
    public void header() {
        // explicit header fixes the columns
        String csv = """
                1,2
                3,4,5
                """;

        DataFrame df = new CsvLoader().header("a", "b").load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void cols() {
        // explicitly selected columns are not extended
        String csv = """
                a,b,c
                1,2,3,4
                5,6,7
                """;

        DataFrame df = new CsvLoader().cols("c", "a").load(src(csv));
        new DataFrameAsserts(df, "c", "a")
                .expectHeight(2)
                .expectRow(0, "3", "1")
                .expectRow(1, "7", "5");
    }

    @Test
    public void colsExcept() {
        String csv = """
                a,b,c
                1,2,3,4
                5,6,7
                """;

        DataFrame df = new CsvLoader().colsExcept("b").load(src(csv));
        new DataFrameAsserts(df, "a", "c", "c3")
                .expectHeight(2)
                .expectRow(0, "1", "3", "4")
                .expectRow(1, "5", "7", null);
    }

    @Test
    public void colBeyondHeader() {
        String csv = """
                a,b
                1,2
                3,4,5,6
                """;

        DataFrame df = new CsvLoader().intCol(3, -1).load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(2)
                .expectIntColumns("c3")
                .expectRow(0, "1", "2", null, -1)
                .expectRow(1, "3", "4", "5", 6);
    }

    @Test
    public void rowsPredicate() {
        String csv = """
                a,b
                1,2
                3,4,5
                6,7
                """;

        DataFrame df = new CsvLoader()
                .rows(RowPredicate.of("a", (String a) -> !a.equals("6")))
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5");
    }

    @Test
    public void rowsSample() {
        String csv = """
                a,b
                1,2
                3,4,5
                6,7
                """;

        DataFrame df = new CsvLoader()
                .rowsSample(5, new Random(1))
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(3)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5")
                .expectRow(2, "6", "7", null);
    }

    @Test
    public void rowsSample_Replaced() {
        StringBuilder csv = new StringBuilder("a,b\n");
        for (int i = 0; i < 100; i++) {
            csv.append(i).append(',').append(i);
            if (i >= 50) {
                csv.append(',').append(i);
            }
            csv.append('\n');
        }

        DataFrame df = new CsvLoader()
                .intCol("a")
                .rowsSample(10, new Random(1))
                .load(new StringReader(csv.toString()));

        new DataFrameAsserts(df, "a", "b", "c2").expectHeight(10);

        // the rows sampled before the column was added have nulls, others have values matching the "a" column
        for (int i = 0; i < 10; i++) {
            int a = (Integer) df.get("a", i);
            Object c2 = df.get("c2", i);
            if (a < 50) {
                assertNull(c2);
            } else {
                assertEquals(String.valueOf(a), c2);
            }
        }
    }

    @Test
    public void limitBeforeWideRow() {
        String csv = """
                a,b
                1,2
                3,4,5
                """;

        DataFrame df = new CsvLoader().limit(1).load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(1)
                .expectRow(0, "1", "2");
    }

    @Test
    public void offsetLimit() {
        String csv = """
                title
                a,b
                1,2,3
                4,5,6
                7,8
                """;

        DataFrame df = new CsvLoader().offset(1).limit(2).load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c2")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3")
                .expectRow(1, "4", "5", "6");
    }

    @Test
    public void multiCharDelimiter() {
        String csv = """
                a||b
                1||2||3||"x||y"
                4||5
                """;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().delimiter("||").build())
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(2)
                .expectRow(0, "1", "2", "3", "x||y")
                .expectRow(1, "4", "5", null, null);
    }

    @Test
    public void backslashEscape() {
        String csv = """
                a,b
                1,2,"x\\",y
                z",w\\,v
                3,4
                """;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build())
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(2)
                // with optional quotes, only the quotes are unescaped (the same as in any other column)
                .expectRow(0, "1", "2", "x\",y\nz", "w\\,v")
                .expectRow(1, "3", "4", null, null);
    }

    @ParameterizedTest
    @EnumSource(LineBreak.class)
    public void lineBreaks(LineBreak lineBreak) {
        String nl = switch (lineBreak) {
            case LF -> "\n";
            case CR -> "\r";
            case CRLF, AUTO -> "\r\n";
        };

        String csv = "a,b" + nl
                + "1,2,\"x" + nl + "y\",3" + nl
                + "4,5," + nl
                + "6,7" + nl;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().lineBreak(lineBreak).build())
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b", "c2", "c3")
                .expectHeight(3)
                .expectRow(0, "1", "2", "x" + nl + "y", "3")
                .expectRow(1, "4", "5", "", null)
                .expectRow(2, "6", "7", null, null);
    }

    /**
     * Enough data to cross the scanner buffer boundaries at different positions within the extra values.
     */
    @ParameterizedTest
    @ValueSource(strings = {"\n", "\r\n"})
    public void largeInput(String nl) {
        StringBuilder csv = new StringBuilder("a,b").append(nl);
        int rows = 20_000;
        for (int i = 0; i < rows; i++) {
            csv.append(i).append(",v").append(i);
            switch (i % 4) {
                case 0 -> csv.append(",\"q,\"\"").append(nl).append("x\"\"\",tail");
                case 1 -> csv.append(",plain,\"\"");
                case 2 -> csv.append(",\"").append(nl).append("\"");
                default -> {
                    // no extras
                }
            }
            csv.append(nl);
        }

        LineBreak lineBreak = nl.equals("\n") ? LineBreak.LF : LineBreak.CRLF;
        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().lineBreak(lineBreak).build())
                .load(new StringReader(csv.toString()));

        DataFrameAsserts asserts = new DataFrameAsserts(df, "a", "b", "c2", "c3").expectHeight(rows);
        for (int i = 0; i < rows; i++) {
            String a = String.valueOf(i);
            String b = "v" + i;
            switch (i % 4) {
                case 0 -> asserts.expectRow(i, a, b, "q,\"" + nl + "x\"", "tail");
                case 1 -> asserts.expectRow(i, a, b, "plain", "");
                case 2 -> asserts.expectRow(i, a, b, nl, null);
                default -> asserts.expectRow(i, a, b, null, null);
            }
        }
    }
}
