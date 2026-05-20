package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class DefaultFieldEncoderTest {

    private static String enc(CsvFormat fmt, Object value) throws IOException {
        StringBuilder sb = new StringBuilder();
        new DefaultFieldEncoder(fmt).encode(value, sb);
        return sb.toString();
    }

    @Test
    public void plainValue_noQuoting() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().build();
        assertEquals("hello", enc(fmt, "hello"));
        assertEquals("42", enc(fmt, 42));
    }

    @Test
    public void nullValue_noNullString() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().build();
        assertEquals("", enc(fmt, null));
    }

    @Test
    public void nullValue_withNullString() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("NULL").build();
        assertEquals("NULL", enc(fmt, null));
    }

    @Test
    public void valueEqualToNullString_isQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("NULL").build();
        assertEquals("\"NULL\"", enc(fmt, "NULL"));
    }

    @Test
    public void minimalQuoting_specialChars() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().build();
        assertEquals("\"a,b\"", enc(fmt, "a,b"));
        assertEquals("\"a\"\"b\"", enc(fmt, "a\"b"));
        assertEquals("\"a\nb\"", enc(fmt, "a\nb"));
        assertEquals("\"a\rb\"", enc(fmt, "a\rb"));
    }

    @Test
    public void escapeBackslash_quotedField() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build();
        assertEquals("\"a\\\"b\"", enc(fmt, "a\"b"));
        // backslash inside an already-quoted field is doubled
        assertEquals("\"a\\\\,b\"", enc(fmt, "a\\,b"));
    }

    @Test
    public void escapeCustom_quotedField() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape('$').build();
        assertEquals("\"a$\"b\"", enc(fmt, "a\"b"));
        // custom escape char inside an already-quoted field is doubled
        assertEquals("\"a$$,b\"", enc(fmt, "a$,b"));
    }

    @Test
    public void quoteAlways() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.of('"')).build();
        assertEquals("\"plain\"", enc(fmt, "plain"));
        assertEquals("\"\"", enc(fmt, ""));
    }

    @Test
    public void quoteNone_escapeBackslash() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat()
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();
        assertEquals("a\\,b", enc(fmt, "a,b"));
        assertEquals("a\\\nb", enc(fmt, "a\nb"));
        assertEquals("a\\\\b", enc(fmt, "a\\b"));
    }

    @Test
    public void customDelimiter_singleChar() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter(";").build();
        assertEquals("\"a;b\"", enc(fmt, "a;b"));
        assertEquals("a,b", enc(fmt, "a,b"));
    }

    @Test
    public void customDelimiter_multiChar() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter("||").build();
        assertEquals("\"a||b\"", enc(fmt, "a||b"));
        assertEquals("a|b", enc(fmt, "a|b"));
    }
}
