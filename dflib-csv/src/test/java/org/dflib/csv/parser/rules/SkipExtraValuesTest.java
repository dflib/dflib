package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class SkipExtraValuesTest {

    private static final String NEXT_ROW = "NEXT";

    @Test
    void lf() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build(),
                "x,\"a,b\n\"\"c\",d\nNEXT");
    }

    @Test
    void crlf() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build(),
                "\"q\r\n\",z\rw\r\nNEXT");
    }

    @Test
    void cr() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build(),
                "\"a\rb\",c\rNEXT");
    }

    @Test
    void auto() {
        assertSkipsTo(CsvFormat.defaultFormat().build(),
                "\"a\r\nb\",c\r\nNEXT");
    }

    @Test
    void auto_SingleCr() {
        assertSkipsTo(CsvFormat.defaultFormat().build(),
                "\"a\rb\",c\rNEXT");
    }

    @Test
    void backslashEscape() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).escape(Escape.BACKSLASH).build(),
                "\"a\\\"\n\\\\\",b\\,c\nNEXT");
    }

    @Test
    void noEscape() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).escape(Escape.NONE).build(),
                "\"a\n\"b\",c\nNEXT");
    }

    @Test
    void noQuote() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).quote(Quote.none()).build(),
                "\"a,b\nNEXT");
    }

    @Test
    void quoteNotAtValueStart() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build(),
                "5\"inch,x\nNEXT");
    }

    @Test
    void multiCharDelimiter() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).delimiter("||").build(),
                "x||\"a||\n\"||y\nNEXT");
    }

    @Test
    void emptyExtras() {
        ParserContext ctx = newContext();
        ParserRule rule = new SkipExtraValuesFactory().create(CsvFormat.defaultFormat().build());

        assertEquals(1, rule.consume(ctx, slice("\nNEXT", 0, 5)));
        assertEquals(0, ctx.extraValuesRows());
    }

    @Test
    void eof_UnclosedQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = new SkipExtraValuesFactory().create(CsvFormat.defaultFormat().build());

        assertEquals(ParserRule.CONTINUE, rule.consume(ctx, slice("x,\"ab", 0, 5)));
        assertThrows(IllegalStateException.class, () -> rule.validateAtEof(ctx));
    }

    @Test
    void eof_ClosingQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = new SkipExtraValuesFactory().create(CsvFormat.defaultFormat().build());

        assertEquals(ParserRule.CONTINUE, rule.consume(ctx, slice("x,\"ab\"", 0, 6)));
        assertDoesNotThrow(() -> rule.validateAtEof(ctx));
        assertEquals(1, ctx.extraValuesRows());
    }

    /**
     * Checks that the rule stops right before the {@link #NEXT_ROW} marker, with the data split into two slices at
     * every possible position.
     */
    private static void assertSkipsTo(CsvFormat format, String data) {
        int expected = data.indexOf(NEXT_ROW);
        for (int split = 1; split <= data.length(); split++) {
            assertSkipsTo(format, data, split, expected);
        }
    }

    private static void assertSkipsTo(CsvFormat format, String data, int split, int expected) {
        ParserContext ctx = newContext();
        ParserRule rule = new SkipExtraValuesFactory().create(format);

        DataSlice first = slice(data, 0, split);
        int pos = rule.consume(ctx, first);
        if (pos == ParserRule.CONTINUE) {
            pos = rule.consume(ctx, slice(data, first.from(), data.length()));
        }

        assertEquals(expected, pos, "Wrong row end, split at " + split);
        assertEquals(1, ctx.extraValuesRows(), "Extra values must be reported once, split at " + split);
    }

    private static DataSlice slice(String data, int from, int to) {
        DataSlice slice = new DataSlice();
        slice.setData(data.toCharArray());
        slice.setFrom(from);
        slice.setTo(to);
        return slice;
    }
}
