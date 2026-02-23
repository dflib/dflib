package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.assertEquals;

class CommentStartTest {

    @Test
    void defaultPrefixMatchSkipsRow() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().enableComments().build());

        char[] buf = "# this is a comment".toCharArray();
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "Skipped row should not advance row position");
    }

    @Test
    void multiCharPrefixMatchAtOffset() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().enableComments("//").build());

        char[] buf = {'x', '/', '/', 'c'};
        DataSlice slice = DataSlice.of(buf);
        slice.setFrom(1);

        int pos = rule.consume(ctx, slice);

        assertEquals(1, pos, "Should return current slice start");
        assertEquals(ParserState.END_OF_LINE.ordinal(), ctx.stateOverride, "State should be overridden to END_OF_LINE");

        ctx.markRowEnd(buf.length);
        assertEquals(0, ctx.position().row, "Skipped row should not advance row position");
    }

    @Test
    void nonMatchDoesNotSkip() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().enableComments("#").build());

        char[] buf = "id,name".toCharArray();
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start when there is no comment");
        assertEquals(0, ctx.stateOverride, "State should not be overridden when prefix does not match");

        ctx.markRowEnd(buf.length);
        assertEquals(1, ctx.position().row, "Non-comment row should advance row position");
    }

    @Test
    void partialPrefixDoesNotMatch() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().enableComments("//").build());

        char[] buf = {'/'};
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start when prefix cannot fully fit");
        assertEquals(0, ctx.stateOverride, "State should not be overridden for partial prefix");

        ctx.markRowEnd(buf.length);
        assertEquals(1, ctx.position().row, "Partial prefix row should not be skipped");
    }

    @Test
    void disabledCommentsNoOp() {
        ParserContext ctx = newContext();
        ParserRule rule = newRule(CsvFormat.defaultFormat().build());

        char[] buf = "# maybe comment".toCharArray();
        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(0, pos, "Should return current slice start when comments are disabled");
        assertEquals(0, ctx.stateOverride, "State should not be overridden when comments are disabled");

        ctx.markRowEnd(buf.length);
        assertEquals(1, ctx.position().row, "Row should not be skipped when comments are disabled");
    }

    private ParserRule newRule(CsvFormat format) {
        return new CommentStartFactory().create(format);
    }
}
