package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.*;

class ColumnStartQuotedTest {

    @Test
    void marksColumnStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {' ', '\t', '"', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after quote");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(3, ctx.activeSlice().from(), "Column start should be marked at quote position");
        assertEquals(0, ctx.stateOverride, "Shouldn't override the state");
    }

    @Test
    void whitespaceOnly() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {' ', ' ', ' '};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if no quote found");
        assertTrue(ctx.activeSlice().open(), "Column should be started to keep the whitespace at the end of file");
        assertFalse(ctx.activeSlice().quoted(), "Column should be unquoted");
        assertEquals(0, ctx.activeSlice().from(), "Column start should be marked at the start position");
        assertEquals(0, ctx.stateOverride, "Shouldn't override the state");
    }

    @Test
    void whitespaceOnly_QuoteInNextChunk() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        assertEquals(-1, rule.consume(ctx, DataSlice.of(new char[]{' ', ' '})));

        int pos = rule.consume(ctx, DataSlice.of(new char[]{' ', ' ', '"', 'c'}));
        assertEquals(3, pos, "Should return index after quote");
        assertTrue(ctx.activeSlice().quoted(), "Column should be restarted as quoted");
        assertEquals(3, ctx.activeSlice().from(), "Column start should be marked at quote position");
    }

    @Test
    void unquoted() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', '"', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Should return the start position to reprocess the value");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertFalse(ctx.activeSlice().quoted(), "Column should be unquoted");
        assertEquals(0, ctx.activeSlice().from(), "Column start should be marked at the start position");
    }

    @Test
    void unquotedEmpty() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {',', '"', 'c', '"'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Should return the start position to reprocess the delimiter");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertFalse(ctx.activeSlice().quoted(), "Column should be unquoted");
    }

    @Test
    void quoteAtStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'"', 'a', 'b'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(1, pos, "Should return index after quote at start");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(1, ctx.activeSlice().from(), "Column start should be marked at quote position");
        assertEquals(0, ctx.stateOverride, "Shouldn't override the state");
    }

    @Test
    void quoteAtEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {' ', ' ', ' ', '"'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after quote at end");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(4, ctx.activeSlice().from(), "Column start should be marked at quote position");
        assertEquals(0, ctx.stateOverride, "Shouldn't override the state");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertFalse(ctx.activeSlice().open(), "Column shouldn't be started");
        assertEquals(-1, pos, "Should return -1 for empty buffer");
        assertEquals(0, ctx.stateOverride, "Shouldn't override the state");
    }

    @Test
    void unquotedEmpty_WhitespaceDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().delimiter("\t").build());
        char[] buf = {'\t', '"', 'c', '"'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Should return the start position to reprocess the delimiter");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertFalse(ctx.activeSlice().quoted(), "Column should be unquoted");
    }

    private ParserRule newRule() {
        return newRule(CsvFormat.defaultFormat().build());
    }

    private ParserRule newRule(CsvFormat format) {
        return new ColumnStartQuotedFactory().create(format);
    }
}
