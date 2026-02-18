package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.*;

class ColumnStartAnyTest {

    @Test
    void overridesStateToQuoted() {
        ParserContext ctx = new ParserContext();
        ctx.setCallback(new DataCallback(){});
        ParserRule rule = newRule();
        char[] buf = {'"', 'a', 'b'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(ParserState.START_QUOTED_OPTIONAL.ordinal(), ctx.stateOverride, "State should be COL_START_QUOTED");
        assertEquals(0, pos, "Should return fromInclusive position");
        assertFalse(ctx.activeSlice().open(), "Column shouldn't be started");
    }

    @Test
    void marksColumnStart() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(ParserState.NO.ordinal(), ctx.currentState(), "State should remain default if not overridden");
        assertEquals(0, pos, "Should return fromInclusive position");
        assertEquals(0, ctx.activeSlice().from());
        assertTrue(ctx.activeSlice().open(), "Column should be started");
    }

    private ParserRule newRule() {
        CsvFormat format = CsvFormat.builder().excludeHeaderValues(false).build();
        return new ColumnStartAnyFactory().create(format);
    }
}
