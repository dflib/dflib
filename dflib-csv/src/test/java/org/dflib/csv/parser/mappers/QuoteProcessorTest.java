package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.junit.jupiter.api.Test;

import java.util.function.Function;

import static org.junit.jupiter.api.Assertions.assertEquals;

class QuoteProcessorTest {

    @Test
    void formatNoEscape() {
        CsvFormat format = CsvFormat.builder().escape(Escape.NONE).build();
        Function<DataSlice, String> processor = QuoteProcessor.forFormat(format, Quote.of('"'), CharBufferProvider.singleton());

        String value = processor.apply(DataSlice.of("a\\\"b".toCharArray(), true));
        assertEquals("a\\\"b", value);
    }

    @Test
    void formatNoQuote() {
        CsvFormat format = CsvFormat.builder()
                .quote(Quote.none())
                .escape(Escape.BACKSLASH)
                .build();
        Function<DataSlice, String> processor = QuoteProcessor.forFormat(format, Quote.none(), CharBufferProvider.singleton());

        String value = processor.apply(DataSlice.of("a\\,b".toCharArray(), true));
        assertEquals("a,b", value);
    }

    @Test
    void formatQuoted() {
        CsvFormat format = CsvFormat.builder().escape(Escape.BACKSLASH).build();
        Function<DataSlice, String> processor = QuoteProcessor.forFormat(format, Quote.of('"'), CharBufferProvider.singleton());

        String value = processor.apply(DataSlice.of("a\\,\\\"b".toCharArray(), true));
        assertEquals("a\\,\"b", value);
    }

    @Test
    void anyDetectsEscapes() {
        String value = QuoteProcessor.unescapeAny(DataSlice.of("x\\,y".toCharArray(), false), '\\', CharBufferProvider.singleton());
        assertEquals("x,y", value);
    }

    @Test
    void anyTrailingEscape() {
        String value = QuoteProcessor.unescapeAny(DataSlice.of("abc\\".toCharArray(), true), '\\', CharBufferProvider.singleton());
        assertEquals("abc\\", value);
    }

    @Test
    void anyNoEscapes() {
        String value = QuoteProcessor.unescapeAny(DataSlice.of("plain".toCharArray(), false), '\\', CharBufferProvider.singleton());
        assertEquals("plain", value);
    }

    @Test
    void quoteNotEscaped() {
        String value = QuoteProcessor.unescapeQuote(DataSlice.of("a\\\"b".toCharArray(), false), '\\', '"', CharBufferProvider.singleton());
        assertEquals("a\\\"b", value);
    }

    @Test
    void quoteLeavesNonQuoteEscapes() {
        String value = QuoteProcessor.unescapeQuote(DataSlice.of("a\\,b".toCharArray(), true), '\\', '"', CharBufferProvider.singleton());
        assertEquals("a\\,b", value);
    }

    @Test
    void quoteDoubleMode() {
        String value = QuoteProcessor.unescapeQuote(DataSlice.of("a\"\"b".toCharArray(), true), '"', '"', CharBufferProvider.singleton());
        assertEquals("a\"b", value);
    }

    @Test
    void quoteCustomEscape() {
        String value = QuoteProcessor.unescapeQuote(DataSlice.of("a$\"$,$\"".toCharArray(), true), '$', '"', CharBufferProvider.singleton());
        assertEquals("a\"$,\"", value);
    }
}
