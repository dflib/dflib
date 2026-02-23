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
        char[] buf = {'a', 'b', '"', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after quote");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(3, ctx.activeSlice().from(), "Column start should be marked at quote position");
        assertEquals(0, ctx.stateOverride, "Should override state to COL_END_QUOTED");
    }

    @Test
    void noQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if no quote found");
        assertFalse(ctx.activeSlice().open(), "Column shouldn't be started");
        assertEquals(0, ctx.stateOverride, "Should override state to COL_END_QUOTED");
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
        assertEquals(0, ctx.stateOverride, "Should override state to COL_END_QUOTED");
    }

    @Test
    void quoteAtEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c', '"'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after quote at end");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(4, ctx.activeSlice().from(), "Column start should be marked at quote position");
        assertEquals(0, ctx.stateOverride, "Should override state to COL_END_QUOTED");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertFalse(ctx.activeSlice().open(), "Column shouldn't be started");
        assertEquals(-1, pos, "Should return -1 for empty buffer");
        assertEquals(0, ctx.stateOverride, "Should override state to COL_END_QUOTED");
    }

    private ParserRule newRule() {
        CsvFormat format = CsvFormat.defaultFormat().build();
        return new ColumnStartQuotedFactory().create(format);
    }
}
