package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvColumnMapping;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.CsvParserConfig;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.dflib.junit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CsvScannerTest {

    @Test
    void endOfBufferNoColumnStarted() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "abc");
        scanner.lastParsePos = 3;

        scanner.endOfBuffer();

        assertEquals(0, scanner.readBuffer.position());
        assertEquals(scanner.readBuffer.capacity(), scanner.readBuffer.limit());
        assertEquals(0, scanner.lastParsePos);
    }

    @Test
    void endOfBufferKeepsUnconsumed() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "abc");
        scanner.lastParsePos = 1;

        scanner.endOfBuffer();

        assertEquals(2, scanner.readBuffer.position());
        assertEquals('b', scanner.readBuffer.get(0));
        assertEquals(0, scanner.lastParsePos);
    }

    @Test
    void endOfBufferKeepsUnconsumedAtStart() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "\r");
        scanner.lastParsePos = 0;

        int initialCapacity = scanner.readBuffer.capacity();
        scanner.endOfBuffer();

        assertEquals(initialCapacity, scanner.readBuffer.capacity());
        assertEquals(1, scanner.readBuffer.position());
        assertEquals('\r', scanner.readBuffer.get(0));
    }

    @Test
    void endOfBufferContinuesIfNotFull() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "abcd");
        scanner.context.activeSlice().setFrom(0);
        scanner.lastParsePos = 4;

        int initialCapacity = scanner.readBuffer.capacity();
        scanner.endOfBuffer();

        assertEquals(initialCapacity, scanner.readBuffer.capacity());
        assertEquals(4, scanner.readBuffer.position());
        assertEquals(initialCapacity, scanner.readBuffer.limit());
        assertEquals('a', scanner.readBuffer.get(0));
        assertEquals('d', scanner.readBuffer.get(3));
    }

    @Test
    void endOfBufferGrowsIfFull() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 1) + "d");
        scanner.context.activeSlice().setFrom(0);
        scanner.lastParsePos = CsvScanner.INITIAL_BUFFER_SIZE;

        scanner.endOfBuffer();

        assertEquals(CsvScanner.INITIAL_BUFFER_SIZE * 2, scanner.readBuffer.capacity());
        assertEquals(CsvScanner.INITIAL_BUFFER_SIZE, scanner.readBuffer.position());
        assertEquals('a', scanner.readBuffer.get(0));
        assertEquals('d', scanner.readBuffer.get(CsvScanner.INITIAL_BUFFER_SIZE - 1));
    }

    @Test
    void endOfBufferCompacts() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "01234567");
        scanner.context.activeSlice().setFrom(3);
        scanner.lastParsePos = 8;

        scanner.endOfBuffer();

        assertEquals(0, scanner.context.activeSlice().from());
        assertEquals(5, scanner.readBuffer.position());
        assertEquals('3', scanner.readBuffer.get(0));
        assertEquals('7', scanner.readBuffer.get(4));
    }

    @Test
    void smallReads() {
        String value = "a".repeat(200);
        String csv = "c0,c1\n" + (value + "," + value + "\n").repeat(100);

        // the buffer used to grow on each partial read
        DataFrame df = new CsvParser().parse(new SmallReadsReader(csv, 5));
        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(100)
                .expectRow(99, value, value);
    }

    @Test
    void singleCharReads() {
        String csv = "a,b\r\n1,2\r\n\r\n\"3\r\n\",4\r\n5,\"6\"\r\n";

        DataFrame df = new CsvParser().parse(new SmallReadsReader(csv, 1));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, "1", "2")
                .expectRow(1, "3\r\n", "4")
                .expectRow(2, "5", "6");
    }

    @Test
    void singleCharReads_CrlfLineBreak() {
        String csv = "a,b\r\n1,2\r\n\r\n3,4\r\n";

        CsvParserConfig format = CsvParserConfig.builder()
                .csvFormat(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF))
                .build();

        DataFrame df = new CsvParser(format).parse(new SmallReadsReader(csv, 1));
        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, "1", "2")
                .expectRow(1, "3", "4");
    }

    @Test
    void crlfSplitAcrossReads() {
        String first = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 1);
        String csv = "h\r\n" + first.substring(3) + "\r\nb\r\n";

        DataFrame df = parse(csv, oneColumnFormat());

        new DataFrameAsserts(df, "c0")
                .expectHeight(3)
                .expectRow(0, "h")
                .expectRow(1, first.substring(3))
                .expectRow(2, "b");
    }

    @Test
    void endingAfterDelimiter() {
        String first = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 1);
        String csv = first + ",b\n";

        DataFrame df = parse(csv, twoColumnFormat());

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, first, "b");
    }

    @Test
    void exactlyAtBufferBoundary() {
        String value = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE);
        String csv = value + "\n";

        DataFrame df = parse(csv, oneColumnFormat());

        new DataFrameAsserts(df, "c0")
                .expectHeight(1)
                .expectRow(0, value);
    }

    @Test
    void singleBufferGrowth() {
        String value = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE + 1000);
        String csv = value + "\n";

        DataFrame df = parse(csv, oneColumnFormat());
        CsvScanner scanner = scan(oneColumnFormat(), csv);

        new DataFrameAsserts(df, "c0")
                .expectHeight(1)
                .expectRow(0, value);
        assertEquals(CsvScanner.INITIAL_BUFFER_SIZE * 2, scanner.readBuffer.capacity());
    }

    @Test
    void multipleBufferGrowth() {
        String value = "a".repeat(20_000);
        String csv = value + "\n";

        DataFrame df = parse(csv, oneColumnFormat());
        CsvScanner scanner = scan(oneColumnFormat(), csv);

        new DataFrameAsserts(df, "c0")
                .expectHeight(1)
                .expectRow(0, value);
        assertEquals(CsvScanner.INITIAL_BUFFER_SIZE * 4, scanner.readBuffer.capacity());
    }

    @Test
    void bufferKeepsSize() {
        String large = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE + 1000);
        String csv = large + "\nsmall\n";

        DataFrame df = parse(csv, oneColumnFormat());
        CsvScanner scanner = scan(oneColumnFormat(), csv);

        new DataFrameAsserts(df, "c0")
                .expectHeight(2)
                .expectRow(0, large)
                .expectRow(1, "small");
        assertEquals(CsvScanner.INITIAL_BUFFER_SIZE * 2, scanner.readBuffer.capacity());
    }

    @Test
    void maxBufferSize() {
        String hugeValue = "a".repeat(CsvScanner.MAX_BUFFER_SIZE + 1);
        String csv = hugeValue + "\n";

        RuntimeException e = assertThrows(RuntimeException.class, () -> scan(oneColumnFormat(), csv));

        assertTrue(e.getMessage().contains("too small to read a single value"));
    }

    @Test
    void multiCharDelimiterAfterQuotedColumn() {
        String first = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 3);
        String csv = "\"" + first + "\"||b\n";
        CsvParserConfig format = CsvParserConfig.builder()
                .csvFormat(CsvFormat.defaultFormat().delimiter("||"))
                .column(CsvColumnMapping.column(0).name("c0"))
                .column(CsvColumnMapping.column(1).name("c1"))
                .excludeHeaderValues(false)
                .build();

        DataFrame df = parse(csv, format);

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, first, "b");
    }

    @Test
    void crlfAfterQuotedColumn() {
        String first = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 3);
        String csv = "\"" + first + "\"\r\nb\r\n";
        CsvParserConfig format = CsvParserConfig.builder()
                .csvFormat(CsvFormat.defaultFormat().lineBreak(LineBreak.CRLF))
                .column(CsvColumnMapping.column(0).name("c0"))
                .excludeHeaderValues(false)

                .build();

        DataFrame df = parse(csv, format);

        new DataFrameAsserts(df, "c0")
                .expectHeight(2)
                .expectRow(0, first)
                .expectRow(1, "b");
    }

    @Test
    void escapeCharAtBufferBoundary() {
        String prefix = "x".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 3);
        String csv = "1," + prefix + "\\,tail\n";
        CsvParserConfig format = CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("c0"))
                .column(CsvColumnMapping.column(1).name("c1"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat()
                        .lineBreak(LineBreak.LF)
                        .quote(Quote.none())
                        .escape(Escape.BACKSLASH))
                .build();

        DataFrame df = parse(csv, format);

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, "1", prefix + ",tail");
    }

    @Test
    void quotedEscapedEscapeAtBufferBoundary() {
        // the first escape char of the "\\" pair is the last char of the first buffer read
        String prefix = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 2);
        String csv = "\"" + prefix + "\\\\\",b\n";

        DataFrame df = parse(csv, twoColumnBackslashFormat());

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, prefix + "\\", "b");
    }

    @Test
    void quotedEscapedQuoteAtBufferBoundary() {
        // the escape char of the "\"" pair is the last char of the first buffer read
        String prefix = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 2);
        String csv = "\"" + prefix + "\\\"\",b\n";

        DataFrame df = parse(csv, twoColumnBackslashFormat());

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, prefix + "\"", "b");
    }

    @Test
    void openingQuoteAtBufferBoundary() {
        String first = "a".repeat(CsvScanner.INITIAL_BUFFER_SIZE - 2);
        String csv = first + ",\"tail\"\n";

        DataFrame df = parse(csv, twoColumnFormat());

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, first, "tail");
    }

    private static DataFrame parse(String csv, CsvParserConfig format) {
        return new CsvParser(format).parse(new StringReader(csv));
    }

    private static CsvScanner scan(CsvParserConfig format, String csv) {
        CsvScanner scanner = newScanner(format);
        scanner.scan(new StringReader(csv));
        return scanner;
    }

    private static CsvScanner newScanner(CsvParserConfig format) {
        ParserContext context = new ParserContext();
        context.setCallback(new DataCallback() {
        });

        ParserRuleFlow ruleFlow = new ParserRuleFlow(format, context);
        if (!format.autoColumns()) {
            ruleFlow.initColumns(format.columnMappings());
        }

        return new CsvScanner(context, ruleFlow);
    }

    private static CsvParserConfig oneColumnFormat() {
        return CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("c0"))
                .excludeHeaderValues(false)
                .build();
    }

    private static CsvParserConfig twoColumnFormat() {
        return CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("c0"))
                .column(CsvColumnMapping.column(1).name("c1"))
                .excludeHeaderValues(false)
                .build();
    }

    private static CsvParserConfig twoColumnBackslashFormat() {
        return CsvParserConfig.builder()
                .column(CsvColumnMapping.column(0).name("c0"))
                .column(CsvColumnMapping.column(1).name("c1"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .csvFormat(CsvFormat.defaultFormat()
                        .lineBreak(LineBreak.LF)
                        .escape(Escape.BACKSLASH))
                .build();
    }

    /**
     * A reader that returns at most N chars per read, like a slow network stream.
     */
    static class SmallReadsReader extends Reader {
        private final StringReader delegate;
        private final int maxRead;

        SmallReadsReader(String data, int maxRead) {
            this.delegate = new StringReader(data);
            this.maxRead = maxRead;
        }

        @Override
        public int read(char[] cbuf, int off, int len) throws IOException {
            return delegate.read(cbuf, off, Math.min(len, maxRead));
        }

        @Override
        public void close() {
            delegate.close();
        }
    }

    private static void primeReadBuffer(CsvScanner scanner, String data) {
        scanner.readBuffer.clear();
        scanner.readBuffer.put(data);
        scanner.readBuffer.flip();
    }
}
