package org.dflib.csv.parser.mappers;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;

import java.util.function.Function;
import java.util.function.IntFunction;

/**
 * Internal API. Part of the {@link org.dflib.csv.CsvLoader} API
 * @since 2.0.0
 */
// public for Column generator access
public final class QuoteProcessor {

    private QuoteProcessor() {
    }

    public static Function<DataSlice, String> forFormat(CsvFormat format,
                                                        Quote quote,
                                                        IntFunction<char[]> bufferProvider) {
        if(format.escape() == Escape.NONE) {
            return DataSlice::toString;
        }

        // this is a special case, we process escape if no quotes are set explicitly
        char quoteChar = quote.quoteChar();
        char escapeChar = switch (format.escape()) {
            case NONE -> 0;
            case DOUBLE -> quoteChar;
            case BACKSLASH -> '\\';
            case CUSTOM -> format.escapeChar();
        };
        if (quote.noQuote()) {
            return slice -> unescapeAny(slice, escapeChar, bufferProvider);
        }
        return slice -> unescapeQuote(slice, escapeChar, quoteChar, bufferProvider);
    }

    static String unescapeAny(DataSlice slice, char escapeChar, IntFunction<char[]> bufferProvider) {
        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();

        boolean containsEscape = slice.escaped();
        if(!containsEscape) {
            // can't use slice.escaped() directly as there could be no rule to set this flag (e.g., column at the EoF)
            for (int i = from; i < to; i++) {
                if (data[i] == escapeChar) {
                    containsEscape = true;
                    break;
                }
            }
        }
        // fast path, no escaping needed
        if (!containsEscape) {
            return slice.toString();
        }

        char[] result = bufferProvider.apply(to - from);
        int next = 0;

        for (int i = from; i < to; i++) {
            char c = data[i];
            if (c == escapeChar && i < to - 1) {
                result[next++] = data[i + 1];
                i++;
            } else {
                result[next++] = c;
            }
        }

        return new String(result, 0, next);
    }

    static String unescapeQuote(DataSlice slice, char escapeChar, char quoteChar, IntFunction<char[]> bufferProvider) {
        if (!slice.escaped()) {
            return slice.toString();
        }

        char[] data = slice.data();
        int from = slice.from();
        int to = slice.to();
        char[] result = bufferProvider.apply(to - from);
        int next = 0;
        boolean changed = false;

        for (int i = from; i < to; i++) {
            char c = data[i];
            if (c == escapeChar && i < to - 1 && data[i + 1] == quoteChar) {
                result[next++] = quoteChar;
                i++;
                changed = true;
            } else {
                result[next++] = c;
            }
        }

        return changed ? new String(result, 0, next) : slice.toString();
    }
}
