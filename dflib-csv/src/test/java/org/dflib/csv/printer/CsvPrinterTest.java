package org.dflib.csv.printer;

import org.dflib.DataFrame;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CsvPrinterTest {

    private static final DataFrame DF = DataFrame.foldByRow("A", "B").of(
            1, 2,
            3, 4);

    private static String print(CsvFormat fmt, DataFrame df) {
        return print(fmt, true, df);
    }

    private static String print(CsvFormat fmt, boolean printHeader, DataFrame df) {
        StringBuilder out = new StringBuilder();
        try {
            new CsvPrinter(fmt, printHeader).print(df, out);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return out.toString();
    }

    @Test
    public void lineBreak() {
        assertEquals("A,B\r1,2\r3,4\r", print(CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build(), DF));
        assertEquals("A,B\n1,2\n3,4\n", print(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build(), DF));
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", print(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build(), DF));
        assertEquals("A,B\r\n1,2\r\n3,4\r\n", print(CsvFormat.defaultFormat().lineBreak(LineBreak.AUTO).build(), DF));
    }

    @Test
    public void multiCharDelimiter() {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter("||").build();
        assertEquals("A||B\r\n1||2\r\n3||4\r\n", print(fmt, DF));
    }

    @Test
    public void multiCharDelimiter_trailingDelimiter() {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter("||").trailingDelimiter(true).build();
        assertEquals("A||B||\r\n1||2||\r\n3||4||\r\n", print(fmt, DF));
    }

    @Test
    public void trailingDelimiter_noHeader() {
        CsvFormat fmt = CsvFormat.defaultFormat().trailingDelimiter(true).build();
        assertEquals("1,2,\r\n3,4,\r\n", print(fmt, false, DF));
    }

    @Test
    public void headerLabelsAreEncoded() {
        DataFrame df = DataFrame.foldByRow("a,b", "x\"y").of(1, 2);
        assertEquals("\"a,b\",\"x\"\"y\"\r\n1,2\r\n", print(CsvFormat.defaultFormat().build(), df));
    }

    @Test
    public void nonStringValues() {
        DataFrame df = DataFrame.foldByRow("A", "B", "C").of(LocalDate.of(2026, 1, 2), 1.5, true);
        assertEquals("A,B,C\r\n2026-01-02,1.5,true\r\n", print(CsvFormat.defaultFormat().build(), df));
    }

    @Test
    public void noRows() {
        assertEquals("A,B\r\n", print(CsvFormat.defaultFormat().build(), DataFrame.empty("A", "B")));
        assertEquals("", print(CsvFormat.defaultFormat().build(), false, DataFrame.empty("A", "B")));
    }

    @Test
    public void singleColumn_noHeader() {
        DataFrame df = DataFrame.foldByRow("A").of("", "x", null);
        assertEquals("\"\"\r\nx\r\n\"\"\r\n", print(CsvFormat.defaultFormat().build(), false, df));
    }

    @Test
    public void singleColumn_emptyHeader() {
        DataFrame df = DataFrame.foldByRow("").of("x");
        assertEquals("\"\"\r\nx\r\n", print(CsvFormat.defaultFormat().build(), df));
    }

    @Test
    public void singleColumn_nullString() {
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("NULL").build();
        DataFrame df = DataFrame.foldByRow("A").of(null, "", "NULL");
        assertEquals("A\r\nNULL\r\n\"\"\r\n\"NULL\"\r\n", print(fmt, df));
    }

    @Test
    public void singleColumn_quoteAlways() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.of('\'')).build();
        DataFrame df = DataFrame.foldByRow("A").of("", "x", null);
        assertEquals("'A'\r\n''\r\n'x'\r\n''\r\n", print(fmt, df));
    }

    @Test
    public void singleColumn_quoteNone() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).escape(Escape.BACKSLASH).build();
        assertEquals("A\r\nx\r\n", print(fmt, DataFrame.foldByRow("A").of("x")));
    }

    @Test
    public void singleColumn_quoteNone_emptyValueThrows() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).escape(Escape.BACKSLASH).build();
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("A").of("x", "")));
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("A").of("x", null)));
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("").of("x")));
    }

    @Test
    public void singleColumn_emptyNullString_nullThrows() {
        // an empty line is skipped by the parser, and a quoted empty string is never read as null
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("").build();
        assertEquals("A\r\n\"\"\r\nx\r\n", print(fmt, DataFrame.foldByRow("A").of("", "x")));
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("A").of("x", null)));

        CsvFormat fmtTrailing = CsvFormat.defaultFormat().nullString("").trailingDelimiter(true).build();
        assertEquals("A,\r\nx,\r\n,\r\n", print(fmtTrailing, DataFrame.foldByRow("A").of("x", null)));
    }

    @Test
    public void singleColumn_quoteNone_nullString() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).nullString("NULL").build();
        assertEquals("A\r\nNULL\r\n", print(fmt, DataFrame.foldByRow("A").of((Object) null)));
    }

    @Test
    public void singleColumn_quoteNone_trailingDelimiter() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).trailingDelimiter(true).build();
        assertEquals("A,\r\n,\r\n", print(fmt, DataFrame.foldByRow("A").of("")));
    }

    @Test
    public void quoteNone_commentPrefixAtLineStartThrows() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).enableComments("#").build();

        // only the first field of a record can be read as a comment
        assertEquals("A,B\r\nx,#y\r\n", print(fmt, DataFrame.foldByRow("A", "B").of("x", "#y")));

        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("A", "B").of("#x", "y")));
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("#A", "B").of("x", "y")));
        assertThrows(IllegalArgumentException.class, () -> print(fmt, DataFrame.foldByRow("A").of("#x")));
    }
}
