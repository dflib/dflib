package org.dflib.csv;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CsvSaver_FormatTest {

    private static final CsvFormat DEFAULT_FORMAT = CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build();
    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);
    

    @Test
    public void defaultFormat() {
        assertEquals("A,B\n1,2\n3,4\n", Csv.saver().format(DEFAULT_FORMAT).saveToString(DF));
    }

    @Test
    public void minimalQuoting_specialChars() {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                "x,y", "z\"w",
                "a\nb", "c\rd");

        assertEquals("A,B\n\"x,y\",\"z\"\"w\"\n\"a\nb\",\"c\rd\"\n",
                Csv.saver().format(DEFAULT_FORMAT).saveToString(df));
    }

    @Test
    public void quoteAlways() {
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).quote(Quote.of('"')).build();

        assertEquals("\"A\",\"B\"\n\"1\",\"2\"\n\"3\",\"4\"\n",
                Csv.saver().format(fmt).saveToString(DF));
    }

    @Test
    public void escapeBackslash_inQuotedField() {
        DataFrame df = DataFrame.foldByRow("A").of("z\"w");
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).escape(Escape.BACKSLASH).build();

        assertEquals("A\n\"z\\\"w\"\n", Csv.saver().format(fmt).saveToString(df));
    }

    @Test
    public void escapeCustom_inQuotedField() {
        DataFrame df = DataFrame.foldByRow("A").of("z\"w");
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).escape('$').build();

        assertEquals("A\n\"z$\"w\"\n", Csv.saver().format(fmt).saveToString(df));
    }

    @Test
    public void nullString() {
        DataFrame df = DataFrame.foldByRow("A", "B").of(
                "x", null,
                null, "y");
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).nullString("NULL").build();

        assertEquals("A,B\nx,NULL\nNULL,y\n", Csv.saver().format(fmt).saveToString(df));
    }

    @Test
    public void trailingDelimiter() {
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).trailingDelimiter(true).build();

        assertEquals("A,B,\n1,2,\n3,4,\n", Csv.saver().format(fmt).saveToString(DF));
    }

    @Test
    public void customDelimiter() {
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT).delimiter(";").build();

        assertEquals("A;B\n1;2\n3;4\n", Csv.saver().format(fmt).saveToString(DF));
    }

    @Test
    public void quoteNone_plainData() {
        CsvFormat fmt = CsvFormat.defaultFormat().copyFrom(DEFAULT_FORMAT)
                .quote(Quote.none())
                .escape(Escape.NONE)
                .build();

        assertEquals("A,B\n1,2\n3,4\n", Csv.saver().format(fmt).saveToString(DF));
    }
}
