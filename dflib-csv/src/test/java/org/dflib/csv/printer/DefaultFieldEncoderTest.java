package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.StringWriter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DefaultFieldEncoderTest {

    private static String enc(CsvFormat fmt, Object value) throws IOException {
        StringBuilder sb = new StringBuilder();
        new DefaultFieldEncoder(fmt).encode(value, sb);
        return sb.toString();
    }

    private static String encToWriter(CsvFormat fmt, Object value) throws IOException {
        StringWriter out = new StringWriter();
        new DefaultFieldEncoder(fmt).encode(value, out);
        return out.toString();
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

    @Test
    public void escapeBackslash_valueWithEscapeCharIsQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build();
        assertEquals("\"a\\\\b\"", enc(fmt, "a\\b"));
        assertEquals("\"end\\\\\"", enc(fmt, "end\\"));
    }

    @Test
    public void escapeCustom_valueWithEscapeCharIsQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape('$').build();
        assertEquals("\"a$$b\"", enc(fmt, "a$b"));
    }

    @Test
    public void escapeDouble_backslashNotQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().build();
        assertEquals("a\\b", enc(fmt, "a\\b"));
    }

    @Test
    public void quoteNone_noEscape_specialCharsThrow() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).build();
        assertThrows(IllegalArgumentException.class, () -> enc(fmt, "a,b"));
        assertThrows(IllegalArgumentException.class, () -> enc(fmt, "a\nb"));

        CsvFormat fmtNone = CsvFormat.defaultFormat().quote(Quote.none()).escape(Escape.NONE).build();
        assertThrows(IllegalArgumentException.class, () -> enc(fmtNone, "a,b"));
    }

    @Test
    public void quoteNone_noEscape_plainValue() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).build();
        assertEquals("a\"b", enc(fmt, "a\"b"));
    }

    @Test
    public void escapeNone_quoteCharThrows() {
        CsvFormat fmt = CsvFormat.defaultFormat().escape(Escape.NONE).build();
        assertThrows(IllegalArgumentException.class, () -> enc(fmt, "a\"b"));
    }

    @Test
    public void commentPrefix_isQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().enableComments("#").build();
        assertEquals("\"#x\"", enc(fmt, "#x"));
        assertEquals("x#", enc(fmt, "x#"));
        assertEquals("#x", enc(CsvFormat.defaultFormat().build(), "#x"));
    }

    @Test
    public void quoteAlways_escapedCharsAtEdges() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.of('"')).build();
        assertEquals("\"\"\"a\"\"b\"\"\"", enc(fmt, "\"a\"b\""));
        assertEquals("\"\"\"a\"\"b\"\"\"", encToWriter(fmt, "\"a\"b\""));
    }

    @Test
    public void escapeBackslash_escapedCharsAtEdges() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape(Escape.BACKSLASH).build();
        assertEquals("\"\\\"a\\\\b\\\"\"", enc(fmt, "\"a\\b\""));
        assertEquals("\"\\\"a\\\\b\\\"\"", encToWriter(fmt, "\"a\\b\""));
    }

    @Test
    public void quoteNone_escapeBackslash_escapedCharsAtEdges() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat()
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .delimiter("||")
                .build();
        assertEquals("\\||a\\\nb\\\\\\||", enc(fmt, "||a\nb\\||"));
        assertEquals("\\||a\\\nb\\\\\\||", encToWriter(fmt, "||a\nb\\||"));
    }

    @Test
    public void customQuoteChar_optional() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.optionalOf('\'')).build();
        assertEquals("a\"b", enc(fmt, "a\"b"));
        assertEquals("'a,b'", enc(fmt, "a,b"));
        assertEquals("'a''b'", enc(fmt, "a'b"));
    }

    @Test
    public void customQuoteChar_always() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.of('\'')).escape(Escape.BACKSLASH).build();
        assertEquals("'a\"b'", enc(fmt, "a\"b"));
        assertEquals("'a\\'b'", enc(fmt, "a'b"));
    }

    @Test
    public void multiCharDelimiter_partialAtEnd() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().delimiter("||").build();
        assertEquals("a|", enc(fmt, "a|"));

        CsvFormat fmtNone = CsvFormat.defaultFormat().quote(Quote.none()).delimiter("||").build();
        assertEquals("a|", enc(fmtNone, "a|"));
    }

    @Test
    public void escapeNone_specialCharsAreQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().escape(Escape.NONE).build();
        assertEquals("\"a,b\"", enc(fmt, "a,b"));
        assertEquals("\"a\nb\"", enc(fmt, "a\nb"));
        assertEquals("a\\b", enc(fmt, "a\\b"));
    }

    @Test
    public void escapeNone_quoteAlways_quoteCharThrows() {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.of('"')).escape(Escape.NONE).build();
        assertThrows(IllegalArgumentException.class, () -> enc(fmt, "a\"b"));
    }

    @Test
    public void quoteNone_escapeCustom() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).escape('$').build();
        assertEquals("a$,b", enc(fmt, "a,b"));
        assertEquals("a$\rb", enc(fmt, "a\rb"));
        assertEquals("a$$b", enc(fmt, "a$b"));
        assertEquals("a\\b", enc(fmt, "a\\b"));
    }

    @Test
    public void commentPrefix_multiChar() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().enableComments("//").build();
        assertEquals("\"//x\"", enc(fmt, "//x"));
        assertEquals("/x", enc(fmt, "/x"));
    }

    @Test
    public void quoteNone_commentPrefix() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).enableComments("#").build();
        assertEquals("#x", enc(fmt, "#x"));
        assertEquals("x#", encAtLineStart(fmt, "x#"));
        assertThrows(IllegalArgumentException.class, () -> encAtLineStart(fmt, "#x"));
    }

    @Test
    public void quoteNone_valueEqualToNullStringThrows() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().quote(Quote.none()).nullString("NULL").build();
        assertEquals("NULL", enc(fmt, null));
        assertEquals("NULLx", enc(fmt, "NULLx"));
        assertThrows(IllegalArgumentException.class, () -> enc(fmt, "NULL"));
    }

    @Test
    public void emptyNullString_emptyValueIsQuoted() throws IOException {
        CsvFormat fmt = CsvFormat.defaultFormat().nullString("").build();
        assertEquals("", enc(fmt, null));
        assertEquals("\"\"", enc(fmt, ""));

        CsvFormat fmtNone = CsvFormat.defaultFormat().quote(Quote.none()).nullString("").build();
        assertEquals("", enc(fmtNone, null));
        assertThrows(IllegalArgumentException.class, () -> enc(fmtNone, ""));
    }

    private static String encAtLineStart(CsvFormat fmt, Object value) throws IOException {
        StringBuilder sb = new StringBuilder();
        new DefaultFieldEncoder(fmt).encodeAtLineStart(value, sb);
        return sb.toString();
    }
}
