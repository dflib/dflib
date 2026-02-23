package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class NoopTest {

    @Test
    void doesNotChangeContext() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.defaultFormat().build();
        ParserRule rule = new NoopFactory().create(format);

        char[] buf = {'a', 'b', 'c'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Noop should not consume any characters");

        assertEquals(0, ctx.position().row, "Row should remain unchanged");
        assertEquals(0, ctx.position().column, "Column should remain unchanged");
        assertFalse(ctx.activeSlice().open(), "Column should not be marked as started");
        assertEquals(-1, ctx.activeSlice().from(), "Column data start should remain reset");
        assertEquals(-1, ctx.activeSlice().to(), "Column data length should remain reset");
    }

    @Test
    void emptyBuffer() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.defaultFormat().build();
        ParserRule rule = new NoopFactory().create(format);

        char[] buf = {};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(0, pos, "Noop should return 0 even for empty buffer");
    }
}
