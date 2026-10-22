package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Objects;

public class CsvSaver_RoundTripTest {

    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            "plain", "x,y",
            "z\"w", "a\nb",
            "tab\tend", "",
            "a\\\\b", "end\\",
            "\\", "C:\\dir\\",
            "$custom$", "a$$b,c$");

    @Test
    public void defaultFormat() {
        assertRoundTrip(CsvFormat.defaultFormat().build());
    }

    @Test
    public void escapeBackslash() {
        assertRoundTrip(CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build());
    }

    @Test
    public void escapeCustom() {
        assertRoundTrip(CsvFormat.defaultFormat().escape('$').build());
    }

    @Test
    public void quoteAlwaysEscapeBackslash() {
        assertRoundTrip(CsvFormat.defaultFormat().quote(Quote.of('"')).escape(Escape.BACKSLASH).build());
    }

    @Test
    public void quoteAlways() {
        assertRoundTrip(CsvFormat.defaultFormat().quote(Quote.of('"')).build());
    }

    @Test
    public void lineBreakLF() {
        assertRoundTrip(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build());
    }

    @Test
    public void customDelimiter() {
        assertRoundTrip(CsvFormat.defaultFormat().delimiter(";").build());
    }

    @Test
    public void lineBreakCR() {
        assertRoundTrip(CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build());
    }

    @Test
    public void multiCharDelimiter() {
        assertRoundTrip(CsvFormat.defaultFormat().delimiter("||").build());
    }

    @Test
    public void trailingDelimiter() {
        assertRoundTrip(CsvFormat.defaultFormat().trailingDelimiter(true).build());
    }

    @Test
    public void customQuoteChar() {
        assertRoundTrip(CsvFormat.defaultFormat().quote(Quote.optionalOf('\'')).build());
    }

    @Test
    public void quoteNoneEscapeBackslash() {
        // no line breaks, as escaped line breaks without quoting are not supported by the parser yet
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                "plain", "x,y",
                "z\"w", "a\\b",
                "end\\", "#x");
        assertRoundTrip(CsvFormat.defaultFormat().quote(Quote.none()).escape(Escape.BACKSLASH).build(), df);
    }

    @Test
    public void quoteNoneEscapeBackslashLineBreak() {
        DataFrame df = DataFrame.foldByRow("A", "B").of("a\nb", "c");
        assertRoundTrip(CsvFormat.defaultFormat().quote(Quote.none()).escape(Escape.BACKSLASH).build(), df);
    }

    @Test
    public void nullString() {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                "x", null,
                "NULL", "",
                null, "y");
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("NULL").build();
        assertRoundTrip(fmt, df, Csv.loader().format(fmt).nullString("NULL"));
    }

    @Test
    public void emptyNullString() {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                "x", null,
                "", "y");
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("").build();
        assertRoundTrip(fmt, df, Csv.loader().format(fmt).emptyStringIsNull());
    }

    @Test
    public void commentPrefix() {
        CsvFormat fmt = CsvFormat.defaultFormat().enableComments("#").build();
        DataFrame df = DataFrame.foldByRow("A", "B").of("#x", "y", "z", "#w");
        String csv = Csv.saver().format(fmt).saveToString(df);
        DataFrame loaded = Csv.loader().format(fmt).load(new StringReader(csv));
        new DataFrameAsserts(loaded, "A", "B")
                .expectHeight(2)
                .expectRow(0, "#x", "y")
                .expectRow(1, "z", "#w");
    }

    @Test
    public void singleColumnEmptyValues() {
        DataFrame df = DataFrame.foldByRow("A").of("", "x", "");
        String csv = Csv.saver().saveToString(df);
        DataFrame loaded = Csv.loader().load(new StringReader(csv));
        new DataFrameAsserts(loaded, "A")
                .expectHeight(3)
                .expectRow(0, "")
                .expectRow(1, "x")
                .expectRow(2, "");
    }

    private void assertRoundTrip(CsvFormat fmt) {
        assertRoundTrip(fmt, DF);
    }

    private void assertRoundTrip(CsvFormat fmt, DataFrame df) {
        assertRoundTrip(fmt, df, Csv.loader().format(fmt));
    }

    private void assertRoundTrip(CsvFormat fmt, DataFrame df, CsvLoader loader) {
        String csv = Csv.saver().format(fmt).saveToString(df);
        DataFrame loaded = loader.load(new StringReader(csv));
        new DataFrameAsserts(loaded, "A", "B").expectHeight(df.height());

        for (int i = 0; i < df.height(); i++) {
            for (int j = 0; j < df.width(); j++) {
                Object expected = df.getColumn(j).get(i);
                Object actual = loaded.getColumn(j).get(i);
                if (!Objects.equals(expected, actual)) {
                    throw new AssertionError("Mismatch at [" + i + "," + j + "]:"
                            + " expected=<" + expected
                            + "> actual=<" + actual
                            + "> csv=<" + csv + ">");
                }
            }
        }
    }
}
