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

class SkipRowTest {

    private static final String NEXT_ROW = "NEXT";

    @Test
    void lfUnquoted() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\n', 'c'}));
        assertEquals(4, pos, "Should return index after LF");
    }

    @Test
    void lfQuotedLineBreak() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
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
        ParserRule rule = newRule(CsvFormat.defaultFormat()
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
        ParserRule rule = newRule(CsvFormat.defaultFormat()
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
        ParserRule rule = newRule(CsvFormat.defaultFormat()
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
        ParserRule rule = newRule(CsvFormat.defaultFormat()
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
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.CRLF)
                .build());

        DataSlice s1 = DataSlice.of(new char[]{'a', '\r'});
        int pos1 = rule.consume(ctx, s1);

        assertEquals(ParserRule.CONTINUE, pos1, "First slice should wait for LF after CR");
        assertEquals(1, s1.from(), "CR should be rescanned when more data is available");

        int pos2 = rule.consume(ctx, DataSlice.of(new char[]{'\r', '\n', 'x'}));
        assertEquals(2, pos2, "Second slice should consume CRLF and finish skipped row");
    }

    @Test
    void cr() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.CR)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\r', 'c'}));
        assertEquals(4, pos, "Should return index after CR");
    }

    @Test
    void autoLf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.AUTO)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', ',', 'b', '\n', 'c'}));
        assertEquals(4, pos, "Should return index after LF in AUTO mode");
    }

    @Test
    void autoCrlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.AUTO)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', '\r', '\n', 'x'}));
        assertEquals(3, pos, "Should consume both CR and LF in AUTO mode");
    }

    @Test
    void crlf() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.CRLF)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'a', '\r', '\n', 'x'}));
        assertEquals(3, pos, "Should consume both CR and LF when available");
    }

    @Test
    void backslashEscapedBackslashBeforeClosingQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'"', 'a', '\\', '\\', '"', '\n', 'x'}));
        assertEquals(6, pos);
    }

    @Test
    void backslashEscapedBackslashAcrossSlices() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape(Escape.BACKSLASH)
                .build());

        assertEquals(ParserRule.CONTINUE, rule.consume(ctx, DataSlice.of(new char[]{'"', 'a', '\\'})));
        assertEquals(3, rule.consume(ctx, DataSlice.of(new char[]{'\\', '"', '\n'})));
    }

    @Test
    void customEscapedEscapeBeforeClosingQuote() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat()
                .lineBreak(LineBreak.LF)
                .quote(Quote.of('"'))
                .escape('$')
                .build());

        int pos = rule.consume(ctx, DataSlice.of(new char[]{'"', 'a', '$', '$', '"', '\n', 'x'}));
        assertEquals(6, pos);
    }

    @Test
    void quoteInsideUnquotedValue() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().build());

        int pos = rule.consume(ctx, DataSlice.of("Monitor 27\" sale,x\nA".toCharArray()));
        assertEquals(19, pos, "Quote inside an unquoted value should not start a quoted value");
    }

    @Test
    void quoteAfterDelimiter() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().build());

        int pos = rule.consume(ctx, DataSlice.of("a,\"b\nc\",d\nA".toCharArray()));
        assertEquals(10, pos, "Quote at the value start should start a quoted value");
    }

    @Test
    void split_Lf() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build(),
                "x,\"a,b\n\"\"c\",d\nNEXT");
    }

    @Test
    void split_Crlf() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF).build(),
                "\"q\r\n\",z\rw\r\nNEXT");
    }

    @Test
    void split_Cr() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.CR).build(),
                "\"a\rb\",c\rNEXT");
    }

    @Test
    void split_Auto() {
        assertSkipsTo(CsvFormat.defaultFormat().build(),
                "\"a\r\nb\",c\r\nNEXT");
    }

    @Test
    void split_AutoSingleCr() {
        assertSkipsTo(CsvFormat.defaultFormat().build(),
                "\"a\rb\",c\rNEXT");
    }

    @Test
    void split_BackslashEscape() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).escape(Escape.BACKSLASH).build(),
                "\"a\\\"\n\\\\\",b\\,c\nNEXT");
    }

    @Test
    void split_NoEscape() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).escape(Escape.NONE).build(),
                "\"a\n\"b\",c\nNEXT");
    }

    @Test
    void split_NoQuote() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).quote(Quote.none()).build(),
                "\"a,b\nNEXT");
    }

    @Test
    void split_QuoteNotAtValueStart() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).build(),
                "5\"inch,x\nNEXT");
    }

    @Test
    void split_MultiCharDelimiter() {
        assertSkipsTo(CsvFormat.defaultFormat().lineBreak(LineBreak.LF).delimiter("||").build(),
                "x||\"a||\n\"||y\nNEXT");
    }

    /**
     * Checks that the rule stops right before the {@link #NEXT_ROW} marker, with the data split into two slices at
     * every possible position.
     */
    private static void assertSkipsTo(CsvFormat format, String data) {
        int expected = data.indexOf(NEXT_ROW);
        for (int split = 1; split <= data.length(); split++) {
            ParserRule rule = newRule(format);

            DataSlice first = slice(data, 0, split);
            int pos = rule.consume(newContext(), first);
            if (pos == ParserRule.CONTINUE) {
                pos = rule.consume(newContext(), slice(data, first.from(), data.length()));
            }

            assertEquals(expected, pos, "Wrong row end, split at " + split);
        }
    }

    private static DataSlice slice(String data, int from, int to) {
        DataSlice slice = new DataSlice();
        slice.setData(data.toCharArray());
        slice.setFrom(from);
        slice.setTo(to);
        return slice;
    }

    private static ParserRule newRule(CsvFormat format) {
        return new SkipRowFactory.SkipRow(format);
    }
}
