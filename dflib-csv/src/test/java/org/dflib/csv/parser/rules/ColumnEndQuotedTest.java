package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.*;

class ColumnEndQuotedTest {

    @Test
    void marksColumnEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c', '"', ','};
        ctx.markColumnStart(1);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos);
    }

    @Test
    void consecutiveQuotes() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule();
        char[] buf = {'a', '"', '"', 'b', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(5, pos);
        assertTrue(tracking.callback().escaped());
    }

    @Test
    void quoteAtBufferEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c', '"'};
        ctx.markColumnStart(1);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if quote is at buffer end");
    }

    @Test
    void emptyColumnWithQuotes() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'"', '"', ','};
        ctx.overrideState(ParserState.START_QUOTED);
        ctx.markColumnStart(0);
        DataSlice data = DataSlice.of(buf);
        data.setFrom(1);
        int pos = rule.consume(ctx, data);
        assertEquals(2, pos, "Should return index after closing quote");
    }

    @Test
    void noEscapeFlagClear() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(3, pos);
        assertFalse(tracking.callback().escaped());
    }

    @Test
    void backslashEscapesSetFlag() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule(CsvFormat.builder()
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .excludeHeaderValues(false)
                .build());
        char[] buf = {'a', '\\', '"', 'b', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(5, pos);
        assertTrue(tracking.callback().escaped());
    }

    @Test
    void trailingBackslashNoFlag() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule(CsvFormat.builder()
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .excludeHeaderValues(false)
                .build());
        char[] buf = {'a', '\\'};
        ctx.markColumnStart(0);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(-1, pos);
        assertFalse(tracking.callback().escaped());
    }

    @Test
    void backslashNearEndEscapes() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule(CsvFormat.builder()
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .excludeHeaderValues(false)
                .build());
        char[] buf = {'a', '\\', '"', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(4, pos);
        assertTrue(tracking.callback().escaped());
    }

    @Test
    void customEscapeSetsFlag() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule(CsvFormat.builder()
                .quote(Quote.of('"'))
                .escape('x')
                .excludeHeaderValues(false)
                .build());
        char[] buf = {'a', 'x', '"', 'b', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(5, pos);
        assertTrue(tracking.callback().escaped());
    }

    @Test
    void customNearEndEscapes() {
        Tracking tracking = newTrackingContext();
        ParserContext ctx = tracking.context();
        ParserRule rule = newRule(CsvFormat.builder()
                .quote(Quote.of('"'))
                .escape('x')
                .excludeHeaderValues(false)
                .build());
        char[] buf = {'a', 'x', '"', '"', ','};
        ctx.markColumnStart(1);
        DataSlice slice = DataSlice.of(buf);
        int pos = rule.consume(ctx, slice);
        assertEquals(4, pos);
        assertTrue(tracking.callback().escaped());
    }

    @Test
    void noQuotes() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule();
        char[] buf = {'a', 'b', 'c', ','};
        ctx.markColumnStart(1);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 if no quotes present");
    }

    private ParserRule newRule() {
        CsvFormat format = CsvFormat.builder().quote(Quote.of('"')).excludeHeaderValues(false).build();
        return newRule(format);
    }

    private ParserRule newRule(CsvFormat format) {
        return new ColumnEndQuotedFactory().create(format);
    }

    private static Tracking newTrackingContext() {
        ParserContext context = new ParserContext();
        EscapeTrackingCallback callback = new EscapeTrackingCallback();
        context.setCallback(callback);
        return new Tracking(context, callback);
    }

    private record Tracking(ParserContext context, EscapeTrackingCallback callback) {
    }

    private static class EscapeTrackingCallback implements DataCallback {
        private boolean escaped;

        @Override
        public void onNewColumn(DataSlice slice) {
            escaped = slice.escaped();
        }

        boolean escaped() {
            return escaped;
        }
    }
}
