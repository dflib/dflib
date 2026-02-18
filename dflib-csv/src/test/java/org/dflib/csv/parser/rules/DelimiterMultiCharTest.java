package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelimiterMultiCharTest {

    @Test
    void multiCharDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("||");
        char[] buf = {'a', '|', '|', 'b'};
        ctx.markColumnStart(0); // Simulate that a column has started
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after delimiter");
        // markColumnEnd is called with quoted=true; ensure currentColumnData is reset
        assertEquals(-1, ctx.activeSlice().from(), "currentColumnData.start should be reset after marking column end");
        assertEquals(-1, ctx.activeSlice().to(), "currentColumnData.length should be reset after marking column end");
    }

    @Test
    void noMatch() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("||");
        char[] buf = {'a', '|', 'b', '|'}; // never two consecutive '|'
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if delimiter not found");
    }

    @Test
    void partialMatch() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("ab");
        char[] buf = {'x', 'a', 'x', 'a', 'b', 'y'}; // 'a' followed by wrong char, then 'ab'
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(5, pos, "Should return index after full match at positions 3-4 ending at 5");
    }

    @Test
    void matchAtStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("##");
        char[] buf = {'#', '#', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(2, pos, "Should return index after delimiter at start");
    }

    @Test
    void matchAtEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("end");
        char[] buf = {'x', 'e', 'n', 'd'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after delimiter at end");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("ab");
        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 for empty buffer");
    }

    @Test
    void emptyDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> newRule(""));
    }

    @Test
    void overlappingPattern() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("aba");
        char[] buf = {'x', 'a', 'b', 'a', 'b', 'a', 'y'}; // contains 'aba' twice with overlap potential
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after first occurrence 'aba' at 1..3");
        assertEquals(1, ctx.position().column, "Column advanced after first match");
    }

    @Test
    void nonZeroStartIndex() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule("mn");
        char[] buf = {'a', 'b', 'm', 'n', 'c'};
        ctx.markColumnStart(2);
        DataSlice data = DataSlice.of(buf);
        data.setFrom(2);
        int pos = rule.consume(ctx, data);
        assertEquals(4, pos, "Match starting at non-zero index should end at 4");
        assertEquals(1, ctx.position().column, "Column advanced after match");
    }

    private ParserRule newRule(String delimiter) {
        CsvFormat format = CsvFormat.builder().delimiter(delimiter).excludeHeaderValues(false).build();
        return new DelimiterFactory().create(format);
    }
}
