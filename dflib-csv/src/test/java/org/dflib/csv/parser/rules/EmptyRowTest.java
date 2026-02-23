package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

class EmptyRowTest {

    @Test
    void skipLf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).skipEmptyRows().build());

        char[] buf = {'\n', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "LF-only row should be skipped");
    }

    @Test
    void skipCr() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.CR).skipEmptyRows().build());

        char[] buf = {'\r', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "CR-only row should be skipped");
    }

    @Test
    void skipAutoOnCr() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.AUTO).skipEmptyRows().build());

        char[] buf = {'\r', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "AUTO should skip row starting with CR");
    }

    @Test
    void skipCrlfPair() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).skipEmptyRows().build());

        char[] buf = {'\r', '\n', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "CRLF row should be skipped");
    }

    @Test
    void noSkipOnPartialCrlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).skipEmptyRows().build());

        char[] buf = {'\r', 'x'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(0, ctx.stateOverride, "State should not be overridden for partial CRLF");

        ctx.markRowEnd(buf.length);
        assertEquals(1, ctx.position().row, "Partial CRLF should not skip row");
    }

    @Test
    void noSkipOnNonLineBreak() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).skipEmptyRows().build());

        char[] buf = {'a', '\n'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(0, ctx.stateOverride, "State should not be overridden when row is not empty");

        ctx.markRowEnd(buf.length);
        assertEquals(1, ctx.position().row, "Non-empty row should not be skipped");
    }

    private ParserRule newRule(CsvFormat format) {
        return new EmptyRowFactory().create(format);
    }
}
