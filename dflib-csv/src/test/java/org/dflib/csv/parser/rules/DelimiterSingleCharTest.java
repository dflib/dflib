package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class DelimiterSingleCharTest {

    @Test
    void findsDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', ';', 'c'};
        ctx.markColumnStart(0); // Simulate that a column has started
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after delimiter");
        assertEquals(-1, ctx.activeSlice().to(), "currentColumnData should be reset after marking column end");
        assertEquals(-1, ctx.activeSlice().from(), "currentColumnData should be reset after marking column end");
    }

    @Test
    void noDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if no delimiter found");
    }

    @Test
    void delimiterAtStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {';', 'a', 'b'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(1, pos, "Should return index after delimiter at start");
    }

    @Test
    void delimiterAtEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', ';'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after delimiter at end");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 for empty buffer");
    }

    @Test
    void escapedDelimiterNoSplit() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.defaultFormat()
                .delimiter(";")
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();
        ParserRule rule = new DelimiterFactory().create(format);

        char[] buf = {'a', '\\', ';', 'b'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(-1, pos, "Escaped delimiter should stay in the current column");
        assertTrue(ctx.activeSlice().open(), "Column should remain open");
    }

    private ParserRule newRule() {
        CsvFormat format = CsvFormat.defaultFormat().delimiter(";").build();
        return new DelimiterFactory().create(format);
    }
}
