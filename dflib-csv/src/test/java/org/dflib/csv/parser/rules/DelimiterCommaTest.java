package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

class DelimiterCommaTest {

    @Test
    void findsComma() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(",");
        char[] buf = {'a', 'b', ',', 'c'};
        ctx.markColumnStart(0); // Simulate that a column has started
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after comma");
        assertEquals(-1, ctx.activeSlice().to(), "currentColumnData should be reset after marking column end");
        assertEquals(-1, ctx.activeSlice().from(), "currentColumnData should be reset after marking column end");
    }

    @Test
    void noComma() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(",");
        char[] buf = {'a', 'b', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if no comma found");
    }

    @Test
    void commaAtStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(",");
        char[] buf = {',', 'a', 'b'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(1, pos, "Should return index after comma at start");
    }

    @Test
    void commaAtEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(",");
        char[] buf = {'a', 'b', ','};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after comma at end");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(",");
        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 for empty buffer");
    }

    private ParserRule newRule(String delimiter) {
        CsvFormat format = CsvFormat.builder().delimiter(delimiter).excludeHeaderValues(false).build();
        return new DelimiterFactory().create(format);
    }
}
