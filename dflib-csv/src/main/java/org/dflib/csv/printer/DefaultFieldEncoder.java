package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
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

    private boolean needsQuoting(String s) {
        if (s.isEmpty()) {
            return false;
        }
        char quoteChar = format.quote().quoteChar();
        String delimiter = new String(format.delimiter().asArray());
        char delimiterFirstChar = delimiter.charAt(0);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == quoteChar || c == '\r' || c == '\n') {
                return true;
            }
            if (c == delimiterFirstChar && s.regionMatches(i, delimiter, 0, delimiter.length())) {
                return true;
            }
        }
        return s.equals(format.nullString());
    }

    private void encodeQuoted(String s, Appendable out) throws IOException {
        char quoteChar = format.quote().quoteChar();
        Escape escape = format.escape();
        char escapeChar = format.escapeChar();

        out.append(quoteChar);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (c == quoteChar) {
                appendQuoteEscape(out, escape, quoteChar, escapeChar);
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

    private static void appendQuoteEscape(Appendable out, Escape escape, char quoteChar, char escapeChar)
            throws IOException {
        switch (escape) {
            case DOUBLE -> out.append(quoteChar);
            case BACKSLASH -> out.append('\\');
            case CUSTOM -> out.append(escapeChar);
            case NONE -> { /* no escape emit quote char as-is */ }
        }
    }

    private void encodeUnquoted(String s, Appendable out) throws IOException {
        Escape escape = format.escape();
        char escapeChar = format.escapeChar();
        String delimiter = new String(format.delimiter().asArray());
        char delimiterFirstChar = delimiter.charAt(0);

        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            boolean structural = c == '\r' || c == '\n'
                    || (c == delimiterFirstChar && s.regionMatches(i, delimiter, 0, delimiter.length()));
            if (structural) {
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
}
