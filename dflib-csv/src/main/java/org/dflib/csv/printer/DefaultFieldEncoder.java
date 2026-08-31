package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Delimiter;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;

import java.io.IOException;
import java.util.Objects;

/**
 * Encodes a single CSV field value to its textual representation
 * according to the rules defined in {@link CsvFormat}.
 * <br>
 * Internal API. Part of the {@link org.dflib.csv.CsvSaver} API.
 *
 * @since 2.0.0
 */
class DefaultFieldEncoder implements CsvFieldEncoder {

    private final CsvFormat format;

    DefaultFieldEncoder(CsvFormat format) {
        this.format = Objects.requireNonNull(format);
    }

    /**
     * Appends the encoded representation of the given value to the target.
     *
     * @param value object to encode
     * @param out target to append to
     */
    @Override
    public void encode(Object value, Appendable out) throws IOException {
        if (value == null) {
            String nullString = format.nullString();
            if (nullString != null) {
                out.append(nullString);
            }
            return;
        }

        String s = value.toString();
        Quote quote = format.quote();
        if (quote.noQuote()) {
            encodeUnquoted(s, out);
        } else if (!quote.optional() || needsQuoting(s)) {
            encodeQuoted(s, out);
        } else {
            out.append(s);
        }
    }

    private boolean needsQuoting(CharSequence s) {
        if (s.isEmpty()) {
            return false;
        }
        String nullString = format.nullString();
        if (nullString != null && nullString.contentEquals(s)) {
            return true;
        }
        Quote quote = format.quote();
        Delimiter delimiter = format.delimiter();
        char quoteChar = quote.quoteChar();
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == quoteChar || c == '\r' || c == '\n' || isDelimiterAt(s, i, delimiter)) {
                return true;
            }
        }
        return false;
    }

    private void encodeQuoted(CharSequence s, Appendable out) throws IOException {
        Quote quote = format.quote();
        Escape escape = format.escape();
        char quoteChar = quote.quoteChar();
        char escapeChar = format.escapeChar();
        out.append(quoteChar);
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == quoteChar) {
                switch (escape) {
                    case DOUBLE -> out.append(quoteChar);
                    case BACKSLASH -> out.append('\\');
                    case CUSTOM -> out.append(escapeChar);
                    case NONE -> { /* no escape emit quote char as-is */ }
                }
                out.append(quoteChar);
            } else if (c == '\\' && escape == Escape.BACKSLASH) {
                out.append('\\').append('\\');
            } else if (c == escapeChar && escape == Escape.CUSTOM) {
                out.append(escapeChar).append(escapeChar);
            } else {
                out.append(c);
            }
        }
        out.append(quoteChar);
    }

    private void encodeUnquoted(CharSequence s, Appendable out) throws IOException {
        Escape escape = format.escape();
        char escapeChar = format.escapeChar();
        Delimiter delimiter = format.delimiter();
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == '\r' || c == '\n' || isDelimiterAt(s, i, delimiter)) {
                switch (escape) {
                    case BACKSLASH -> out.append('\\').append(c);
                    case CUSTOM -> out.append(escapeChar).append(c);
                    default -> out.append(c);
                }
            } else if (c == '\\' && escape == Escape.BACKSLASH) {
                out.append('\\').append('\\');
            } else if (c == escapeChar && escape == Escape.CUSTOM) {
                out.append(escapeChar).append(escapeChar);
            } else {
                out.append(c);
            }
        }
    }

    private boolean isDelimiterAt(CharSequence s, int index, Delimiter delimiter) {
        if (delimiter.isSingleChar()) {
            return s.charAt(index) == delimiter.singleChar();
        }

        char[] delimiterChars = delimiter.asArray();
        char delimiterFirstChar = delimiterChars[0];
        return s.charAt(index) == delimiterFirstChar && matchesDelimiter(s, index, delimiterChars);
    }

    private boolean matchesDelimiter(CharSequence s, int index, char[] delimiterChars) {
        int delimiterLength = delimiterChars.length;
        if (index + delimiterLength > s.length()) {
            return false;
        }

        for (int i = 1; i < delimiterLength; i++) {
            if (s.charAt(index + i) != delimiterChars[i]) {
                return false;
            }
        }

        return true;
    }
}
