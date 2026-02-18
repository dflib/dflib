package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ColumnStartUnquotedTest {

    @Test
    void columnStartFromIndex() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c'};
        DataSlice data = DataSlice.of(buf);
        data.setFrom(1);
        int pos = rule.consume(ctx, data);
        assertEquals(1, pos, "Should return fromInclusive");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(1, ctx.activeSlice().from(), "Column start should be marked at fromInclusive");
    }

    @Test
    void columnStartAtZero() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'x', 'y'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Should return fromInclusive");
        assertTrue(ctx.activeSlice().open(), "Column should be started");
        assertEquals(0, ctx.activeSlice().from(), "Column start should be marked at fromInclusive");
    }

    private ParserRule newRule() {
        CsvFormat format = CsvFormat.builder().excludeHeaderValues(false).build();
        return new ColumnStartUnquotedFactory().create(format);
    }
}
