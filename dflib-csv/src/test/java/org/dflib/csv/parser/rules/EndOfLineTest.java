package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;
import org.junit.jupiter.api.Test;

import static org.dflib.csv.parser.test.ContextUtil.newContext;
import static org.junit.jupiter.api.Assertions.*;

class EndOfLineTest {

    @Test
    void lfLineEnd() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\nxyz".toCharArray();
        // Simulate a started column from position 0 (markColumnStart expects position of the first char + 1)
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(4, pos, "Should return index immediately after LF");
        assertEquals(1, ctx.position().row, "Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void crLineEnd() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CR)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\rxyz".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(4, pos, "Should return index next to \\r");
        assertEquals(1, ctx.position().row,"Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void crlfLineEnd() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\r\ndef".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(5, pos, "Should return index immediately after CRLF");
        assertEquals(1, ctx.position().row, "Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void autoLineEndLf() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.AUTO)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\nxyz".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(4, pos, "Should return index immediately after LF");
        assertEquals(1, ctx.position().row, "Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void autoLineEndCr() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.AUTO)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\rxyz".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(4, pos, "Should return index immediately after CR");
        assertEquals(1, ctx.position().row, "Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void autoLineEndCrlf() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.AUTO)
                .excludeHeaderValues(false)
                .build();

        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abc\r\ndef".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(5, pos, "Should return index immediately after CRLF");
        assertEquals(1, ctx.position().row, "Row should be incremented");
        assertFalse(ctx.activeSlice().open(), "Column should be closed after row end");
    }

    @Test
    void noLineEnd() {
        ParserContext ctx = newContext();
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .excludeHeaderValues(false)
                .build();
        
        ParserRule rule = new EndOfLineFactory().create(format);

        char[] buf = "abcdef".toCharArray();
        ctx.markColumnStart(1);

        int pos = rule.consume(ctx, DataSlice.of(buf));

        assertEquals(-1, pos, "Should return -1 when no line ending found");
        assertEquals(0, ctx.position().row, "Row should not change");
        assertTrue(ctx.activeSlice().open(), "Column should remain started");
    }

}
