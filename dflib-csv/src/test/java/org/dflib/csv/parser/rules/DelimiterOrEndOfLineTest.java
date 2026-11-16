package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class DelimiterOrEndOfLineTest {

    @Test
    void multiCharDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.CR, "||");
        char[] buf = {'a', '|', '|', 'b'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after delimiter");
        assertEquals(1, ctx.position().column, "Should have advanced to next column");
        assertEquals(-1, ctx.activeSlice().from(), "column data should be reset after end");
        assertEquals(-1, ctx.activeSlice().to(), "column data should be reset after end");
    }

    @Test
    void endOfLine() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.LF, ";");
        char[] buf = {'a', 'b', '\n'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after LF");
        assertEquals(1, ctx.position().row, "Row count should be incremented");
        assertEquals(0, ctx.position().column, "Column count should be reset after row end");
        assertEquals(-1, ctx.activeSlice().from(), "Column data should be reset after row end");
        assertEquals(-1, ctx.activeSlice().to(), "Column data should be reset after row end");
    }

    @Test
    void noMatch() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.LF, "ab");
        char[] buf = {'x', 'y', 'z'};
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(-1, pos, "Should return -1 when neither delimiter nor LF found");
        assertEquals(0, ctx.position().row, "Row should not change");
        assertEquals(0, ctx.position().column, "Column should not change");
    }

    @Test
    void partialDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.LF, "ab");
        char[] buf = {'a', '\n'}; // 'a' partially matches delimiter, then LF
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(2, pos, "Should consume LF and return index after it");
        assertEquals(1, ctx.position().row, "Row should increment on LF");
    }

    @Test
    void emptyDelimiter() {
        assertThrows(IllegalArgumentException.class, () -> newRule(LineBreak.LF, ""));
    }

    @Test
    void partialDelimiterAcrossChunks() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.LF, "ab");
        char[] buf1 = {'a'}; // partial match 'a'
        char[] buf2 = {'\n'}; // LF should take precedence over completing delimiter

        ctx.markColumnStart(0);
        int pos1 = rule.consume(ctx, DataSlice.of(buf1));
        assertEquals(-1, pos1, "First chunk should not contain full delimiter");

        int pos2 = rule.consume(ctx, DataSlice.of(buf2));
        assertEquals(1, pos2, "Should consume LF in second chunk and return index after it");
        assertEquals(1, ctx.position().row, "Row should increment on LF");
        assertEquals(0, ctx.position().column, "Column should reset on row end");
        assertEquals(-1, ctx.activeSlice().from(), "Column data should be reset after row end");
        assertEquals(-1, ctx.activeSlice().to(), "Column data should be reset after row end");
    }

    @Test
    void autoLineLf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, ";");
        char[] buf = {'a', 'b', '\n'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after LF");
        assertEquals(1, ctx.position().row, "Row count should be incremented");
        assertEquals(0, ctx.position().column, "Column count should be reset after row end");
    }

    @Test
    void autoLineCr() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, ";");
        char[] buf = {'a', 'b', '\r'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(3, pos, "Should return index after CR");
        assertEquals(1, ctx.position().row, "Row count should be incremented");
        assertEquals(0, ctx.position().column, "Column count should be reset after row end");
    }

    @Test
    void autoLineCrlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, ";");
        char[] buf = {'a', 'b', '\r', '\n'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after CRLF (consuming both chars)");
        assertEquals(1, ctx.position().row, "Row count should be incremented");
        assertEquals(0, ctx.position().column, "Column count should be reset after row end");
    }

    @Test
    void autoDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, ";");
        char[] buf = {'a', ';', 'b'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(2, pos, "Should return index after delimiter");
        assertEquals(1, ctx.position().column, "Should have advanced to next column");
    }

    @Test
    void escapedDelimiterNoSplit() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.defaultFormat()
                .delimiter(";")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();
        ParserRule rule = new DelimiterOrEndOfLineFactory().create(format);

        char[] buf = {'a', '\\', ';', 'b'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(-1, pos, "Escaped delimiter should stay in the current column");
        assertEquals(0, ctx.position().row, "Row should not end");
        assertEquals(0, ctx.position().column, "Column should not advance");
    }

    @Test
    void escapedDelimiterBeforeEnd() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.defaultFormat()
                .delimiter(";")
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();
        ParserRule rule = new DelimiterOrEndOfLineFactory().create(format);

        char[] buf = {'a', '\\', ';', 'b', '\n'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(5, pos, "Row should end at LF");
        assertEquals(1, ctx.position().row, "Row should increment");
        assertEquals(0, ctx.position().column, "Column should reset after row end");
    }

    @Test
    void overlappingPattern() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, "aba");
        char[] buf = {'x', 'a', 'b', 'a', 'b', 'a', 'y'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Should return index after first occurrence 'aba' at 1..3");
        assertEquals(1, ctx.position().column, "Column advanced after first match");
    }

    @Test
    void nonZeroStartIndex() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, "mn");
        char[] buf = {'a', 'b', 'm', 'n', 'c'};
        ctx.markColumnStart(2);
        DataSlice data = DataSlice.of(buf);
        data.setFrom(2);
        int pos = rule.consume(ctx, data);
        assertEquals(4, pos, "Match starting at non-zero index should end at 4");
        assertEquals(1, ctx.position().column, "Column advanced after match");
    }

    @Test
    void singleCharLf_CrIsData() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.LF, ",");
        char[] buf = {'a', '\r', 'b', '\n', 'x'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "CR is a part of the value in LF mode");
        assertEquals(1, ctx.position().row);
    }

    @Test
    void singleCharCr_LfIsData() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.CR, ";");
        char[] buf = {'a', '\n', 'b', ';', 'x'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "LF is a part of the value in CR mode");
        assertEquals(1, ctx.position().column);
    }

    @Test
    void multiChar_PartialMatchRestarts() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, "ab");
        char[] buf = {'x', 'a', 'a', 'b', 'y'};
        ctx.markColumnStart(0);
        int pos = rule.consume(ctx, DataSlice.of(buf));
        assertEquals(4, pos, "Delimiter should be found after a partial match");
        assertEquals(1, ctx.position().column);
    }

    @Test
    void multiChar_PartialMatchAtSliceEnd() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(LineBreak.AUTO, "||");
        DataSlice slice = DataSlice.of(new char[]{'x', '|'});
        ctx.markColumnStart(0);
        assertEquals(ParserRule.CONTINUE, rule.consume(ctx, slice), "Delimiter may continue in the next read");
        assertEquals(0, ctx.position().column);
    }

    private ParserRule newRule(LineBreak lineBreak, String delimiter) {
        CsvFormat format = CsvFormat.defaultFormat()
                .delimiter(delimiter)
                .lineBreak(lineBreak)
                .build();
        return new DelimiterOrEndOfLineFactory().create(format);
    }
}
