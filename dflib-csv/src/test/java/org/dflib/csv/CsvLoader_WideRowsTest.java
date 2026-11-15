package org.dflib.csv;

import org.dflib.ByteSource;
import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Rows with more values than the header: the extra values are ignored.
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
        new DataFrameAsserts(df, "Some Title")
                .expectHeight(2)
                .expectRow(0, "")
                .expectRow(1, "r1");
    }

    @Test
    public void unquoted() {
        String csv = """
                a,b
                1,2,3
                4,5
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
    }

    @Test
    public void lastColumnQuoted() {
        String csv = """
                a,b
                1,"2",3
                4,"5"
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
    }

    @Test
    public void lastColumnTyped() {
        String csv = """
                a,b
                1,2,x
                3,4
                """;

        DataFrame df = new CsvLoader().intCol("b").load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", 2)
                .expectRow(1, "3", 4);
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
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5")
                .expectRow(2, "6", "7");
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
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void emptyExtras() {
        String csv = """
                a,b
                1,2,
                3,4,,
                """;

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void lastRowWithoutLineBreak() {
        String csv = "a,b\n1,2\n3,4,5";

        DataFrame df = new CsvLoader().load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    public void lastRowWithoutLineBreak_QuotedExtra() {
        String csv = "a,b\n1,2\n3,4,\"5\"";

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

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4")
                .expectRow(2, "8", "9");
    }

    @Test
    public void trailingDelimiter_QuotedLastColumn() {
        String csv = """
                a,b,
                1,"2",3,
                4,"5",
                """;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().trailingDelimiter(true).build())
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
    }

    @Test
    public void nullPadRows() {
        String csv = """
                a,b,c
                1,2
                3,4,5,6
                7,8,9
                """;

        DataFrame df = new CsvLoader().nullPadRows().load(src(csv));
        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, "1", "2", null)
                .expectRow(1, "3", "4", "5")
                .expectRow(2, "7", "8", "9");
    }

    @Test
    public void header() {
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
    public void offsetLimit() {
        String csv = """
                title
                a,b
                1,2,3
                4,5,6
                7,8
                """;

        DataFrame df = new CsvLoader().offset(1).limit(2).load(src(csv));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
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

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
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

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
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
                + "4,5" + nl;

        DataFrame df = new CsvLoader()
                .format(CsvFormat.defaultFormat().lineBreak(lineBreak).build())
                .load(src(csv));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "4", "5");
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

        DataFrameAsserts asserts = new DataFrameAsserts(df, "a", "b").expectHeight(rows);
        for (int i = 0; i < rows; i++) {
            asserts.expectRow(i, String.valueOf(i), "v" + i);
        }
    }
}
