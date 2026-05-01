package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;
import java.util.Objects;

public class CsvSaver_RoundTripTest {

    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            "plain", "x,y",
            "z\"w", "a\nb",
            "tab\tend", "");

    @Test
    public void defaultFormat() {
        assertRoundTrip(CsvFormat.defaultFormat().build());
    }

    @Test
    public void escapeBackslash() {
        assertRoundTrip(CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build());
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

    private void assertRoundTrip(CsvFormat fmt) {
        String csv = Csv.saver().format(fmt).saveToString(DF);
        DataFrame loaded = Csv.loader().format(fmt).load(new StringReader(csv));
        new DataFrameAsserts(loaded, "A", "B").expectHeight(3);

        for (int i = 0; i < DF.height(); i++) {
            for (int j = 0; j < DF.width(); j++) {
                Object expected = DF.getColumn(j).get(i);
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
