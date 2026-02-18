package org.dflib.csv.parser;

import org.dflib.DataFrame;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.ParserContext;
import org.dflib.csv.parser.format.CsvColumnFormat;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;
import org.dflib.csv.parser.format.Quote;
import org.dflib.junit5.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.io.StringReader;

import static org.junit.jupiter.api.Assertions.*;

class CsvScannerTest {

    @Test
    void endOfBufferNoColumnStarted() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "abc");

        scanner.endOfBuffer();

        assertEquals(0, scanner.readBuffer.position());
        assertEquals(scanner.readBuffer.capacity(), scanner.readBuffer.limit());
    }

    @Test
    void endOfBufferGrows() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "abcd");
        scanner.context.activeSlice().setFrom(0);

        int initialCapacity = scanner.readBuffer.capacity();
        scanner.endOfBuffer();

        assertEquals(initialCapacity * 2, scanner.readBuffer.capacity());
        assertEquals(4, scanner.readBuffer.position());
        assertEquals('a', scanner.readBuffer.get(0));
        assertEquals('d', scanner.readBuffer.get(3));
    }

    @Test
    void endOfBufferCompacts() {
        CsvScanner scanner = newScanner(oneColumnFormat());
        primeReadBuffer(scanner, "01234567");
        scanner.context.activeSlice().setFrom(3);

        scanner.endOfBuffer();

        assertEquals(0, scanner.context.activeSlice().from());
        assertEquals(5, scanner.readBuffer.position());
        assertEquals('3', scanner.readBuffer.get(0));
        assertEquals('7', scanner.readBuffer.get(4));
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
        CsvFormat format = CsvFormat.builder()
                .delimiter("||")
                .column(CsvFormat.column(0).name("c0"))
                .column(CsvFormat.column(1).name("c1"))
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
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.CRLF)
                .column(CsvFormat.column(0).name("c0"))
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
        CsvFormat format = CsvFormat.builder()
                .lineBreak(LineBreak.LF)
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .column(CsvFormat.column(0).name("c0"))
                .column(CsvFormat.column(1).name("c1"))
                .autoColumns(false)
                .excludeHeaderValues(false)
                .build();

        DataFrame df = parse(csv, format);

        new DataFrameAsserts(df, "c0", "c1")
                .expectHeight(1)
                .expectRow(0, "1", prefix + ",tail");
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

    private static DataFrame parse(String csv, CsvFormat format) {
        return new CsvParser(format).parse(new StringReader(csv));
    }

    private static CsvScanner scan(CsvFormat format, String csv) {
        CsvScanner scanner = newScanner(format);
        scanner.scan(new StringReader(csv));
        return scanner;
    }

    private static CsvScanner newScanner(CsvFormat format) {
        ParserContext context = new ParserContext();
        context.setCallback(new DataCallback() {
        });

        ParserRuleFlow ruleFlow = new ParserRuleFlow(format, context);
        if (!format.autoColumns()) {
            ruleFlow.initColumns(format.columnBuilders().stream().map(CsvColumnFormat.Builder::build).toList());
        }

        return new CsvScanner(context, ruleFlow);
    }

    private static CsvFormat oneColumnFormat() {
        return CsvFormat.builder()
                .column(CsvFormat.column(0).name("c0"))
                .excludeHeaderValues(false)
                .build();
    }

    private static CsvFormat twoColumnFormat() {
        return CsvFormat.builder()
                .column(CsvFormat.column(0).name("c0"))
                .column(CsvFormat.column(1).name("c1"))
                .excludeHeaderValues(false)
                .build();
    }

    private static void primeReadBuffer(CsvScanner scanner, String data) {
        scanner.readBuffer.clear();
        scanner.readBuffer.put(data);
        scanner.readBuffer.flip();
    }
}
