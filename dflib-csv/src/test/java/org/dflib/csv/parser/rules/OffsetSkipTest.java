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

class OffsetSkipTest {

    @Test
    void lfUnquoted() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\n', 'c'}));
        assertEquals(4, pos, "Should return index after LF");
    }

    @Test
    void lfQuotedLineBreak() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.DOUBLE)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'"', 'a', '\n', 'b', '"', ',', 'c', '\n', 'x'}));
        assertEquals(8, pos, "Should skip newline inside quotes and stop at row end");
    }

    @Test
    void doubleQuoteAcrossSlices() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.DOUBLE)
                .build());

        DataSlice s1 = DataSlice.of(new char[]{'"', 'a', '"'});
        int pos1 = rule.consume(ctx, s1);

        assertEquals(ParserRule.CONTINUE, pos1, "First slice should be incomplete");
        assertEquals(3, s1.from(), "Slice start should advance to end when continuing");

        int pos2 = rule.consume(ctx, DataSlice.of(new char[]{'\n', 'x'}));
        assertEquals(1, pos2, "Second slice should close quoted value and consume LF");
    }

    @Test
    void doubledQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.DOUBLE)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'"', 'a', '"', '"', 'b', '"', '\n', 'x'}));
        assertEquals(7, pos, "Should treat doubled quote as escaped quote and consume LF");
    }

    @Test
    void backslashEscapeAcrossSlices() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .build());

        DataSlice s1 = DataSlice.of(new char[]{'"', 'a', '\\'});
        int pos1 = rule.consume(ctx, s1);

        assertEquals(ParserRule.CONTINUE, pos1, "First slice should leave pending escape");
        assertEquals(3, s1.from(), "Slice start should advance to end when continuing");

        int pos2 = rule.consume(ctx, DataSlice.of(new char[]{'"', 'b', '"', '\n'}));
        assertEquals(4, pos2, "Second slice should consume escaped quote and stop at LF");
    }

    @Test
    void customEscapeAcrossSlices() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape('$')
                .build());

        DataSlice s1 = DataSlice.of(new char[]{'"', 'a', '$'});
        int pos1 = rule.consume(ctx, s1);

        assertEquals(ParserRule.CONTINUE, pos1, "First slice should leave pending custom escape");
        assertEquals(3, s1.from(), "Slice start should advance to end when continuing");

        int pos2 = rule.consume(ctx, DataSlice.of(new char[]{'"', 'b', '"', '\n'}));
        assertEquals(4, pos2, "Second slice should consume escaped quote and stop at LF");
    }

    @Test
    void crlfAcrossSlices() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .build());

        DataSlice s1 = DataSlice.of(new char[]{'a', '\r'});
        int pos1 = rule.consume(ctx, s1);

        assertEquals(ParserRule.CONTINUE, pos1, "First slice should wait for LF after CR");
        assertEquals(2, s1.from(), "Slice start should advance to end when continuing");

        int pos2 = rule.consume(ctx, DataSlice.of(new char[]{'\n', 'x'}));
        assertEquals(1, pos2, "Second slice should consume LF and finish skipped row");
    }

    @Test
    void cr() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.CR)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\r', 'c'}));
        assertEquals(4, pos, "Should return index after CR");
    }

    @Test
    void autoLf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.AUTO)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\n', 'c'}));
        assertEquals(4, pos, "Should return index after LF in AUTO mode");
    }

    @Test
    void autoCrlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.AUTO)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', '\r', '\n', 'x'}));
        assertEquals(3, pos, "Should consume both CR and LF in AUTO mode");
    }

    @Test
    void crlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', '\r', '\n', 'x'}));
        assertEquals(3, pos, "Should consume both CR and LF when available");
    }

    private ParserRule newRule(CsvFormat format) {
        return new OffsetSkipFactory.OffsetRowSkipRule(format);
    }
}
