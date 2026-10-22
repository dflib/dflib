package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * Encodes a single CSV field value to its textual representation
 * according to the rules defined in {@link CsvFormat}.
 */
class DefaultFieldEncoder {

    private final Quote quote;
    private final char quoteChar;
    private final Escape escape;
    private final boolean hasEscapeChar;
    private final char escapeChar;
    private final char[] delimiter;
    private final char delimiterFirstChar;
    private final String nullString;
    private final String comment;

    DefaultFieldEncoder(CsvFormat format) {
        Objects.requireNonNull(format);

        this.quote = format.quote();
        this.quoteChar = quote.quoteChar();
        this.escape = format.escape();
        this.hasEscapeChar = escape == Escape.BACKSLASH || escape == Escape.CUSTOM;
        this.escapeChar = escape == Escape.BACKSLASH ? '\\' : format.escapeChar();
        this.delimiter = format.delimiter().asArray();
        this.delimiterFirstChar = delimiter[0];
        this.nullString = format.nullString();
        this.comment = format.comment();
    }

    /**
     * Appends the encoded representation of the given value to the target.
     *
     * @param value object to encode
     * @param out target to append to
     */
    void encode(Object value, Appendable out) throws IOException {
        encode(value, false, out);
    }

    /**
     * Appends the encoded representation of the given value that starts a line (i.e., the first field of a record)
     * to the target. Such values may be read as comments, so they are subject to additional checks.
     *
     * @param value object to encode
     * @param out target to append to
     */
    void encodeAtLineStart(Object value, Appendable out) throws IOException {
        encode(value, true, out);
    }

    private void encode(Object value, boolean lineStart, Appendable out) throws IOException {
        if (value == null) {
            if (nullString != null) {
                out.append(nullString);
            }
            return;
        }

        String s = value.toString();
        if (quote.noQuote()) {
            checkUnquotedRepresentable(s, lineStart);
            encodeUnquoted(s, out);
        } else if (!quote.optional() || needsQuoting(s)) {
            encodeQuoted(s, out);
        } else {
            out.append(s);
        }
    }

    private boolean needsQuoting(String s) {
        // checked before the empty string, so that "" is distinguishable from null when the null string is ""
        if (nullString != null && nullString.equals(s)) {
            return true;
        }

        if (s.isEmpty()) {
            return false;
        }

        // a value at the start of the line would be read as a comment
        if (comment != null && s.startsWith(comment)) {
            return true;
        }

        // copying the fields to locals for the hot loop below
        char quoteChar = this.quoteChar;
        char delimiterFirstChar = this.delimiterFirstChar;

        // an unquoted escape char would be interpreted by the parser, so such values must be quoted (and escaped)
        char escapeChar = hasEscapeChar ? this.escapeChar : quoteChar;

        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == quoteChar
                    || c == '\r'
                    || c == '\n'
                    || c == escapeChar
                    || (c == delimiterFirstChar && isDelimiterAt(s, i))) {
                return true;
            }
        }
        return false;
    }

    // chars that need no escaping are appended in runs, as appending char by char is slow with most Appendables
    private void encodeQuoted(String s, Appendable out) throws IOException {
        out.append(quoteChar);

        int runStart = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == quoteChar) {
                if (escape == Escape.NONE) {
                    throw new IllegalArgumentException(
                            "Can't encode a value containing the quote char with Escape.NONE: " + s);
                }

                // with DOUBLE the quote char escapes itself, otherwise the escape char precedes the quote
                appendRun(s, runStart, i, out);
                out.append(escape == Escape.DOUBLE ? quoteChar : escapeChar);
                runStart = i;
            } else if (hasEscapeChar && c == escapeChar) {
                appendRun(s, runStart, i, out);
                out.append(escapeChar);
                runStart = i;
            }
        }

        appendRun(s, runStart, s.length(), out);
        out.append(quoteChar);
    }

    // without quoting there's no way to tell such values from nulls or comments, so they can't be written
    private void checkUnquotedRepresentable(String s, boolean lineStart) {
        if (nullString != null && nullString.equals(s)) {
            throw new IllegalArgumentException(
                    "Can't encode a value equal to the null string without quoting: " + s);
        }

        if (lineStart && comment != null && s.startsWith(comment)) {
            throw new IllegalArgumentException(
                    "Can't encode a value starting with the comment prefix at the start of a line without quoting: "
                            + s);
        }
    }

    private void encodeUnquoted(String s, Appendable out) throws IOException {
        int runStart = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == '\r' || c == '\n' || isDelimiterAt(s, i)) {
                if (!hasEscapeChar) {
                    throw new IllegalArgumentException(
                            "Can't encode a value containing a delimiter or a line break without quoting and with no "
                                    + "escape char. Use Escape.BACKSLASH or a custom escape char: " + s);
                }

                appendRun(s, runStart, i, out);
                out.append(escapeChar);
                runStart = i;
            } else if (hasEscapeChar && c == escapeChar) {
                appendRun(s, runStart, i, out);
                out.append(escapeChar);
                runStart = i;
            }
        }

        appendRun(s, runStart, s.length(), out);
    }

    private static void appendRun(String s, int from, int to, Appendable out) throws IOException {
        if (from == to) {
            return;
        }

        // Writer.append(CharSequence, int, int) copies the range to a new String, while Writer.write(..) doesn't
        if (out instanceof Writer w) {
            w.write(s, from, to - from);
        } else {
            out.append(s, from, to);
        }
    }

    private boolean isDelimiterAt(String s, int index) {
        if (s.charAt(index) != delimiterFirstChar) {
            return false;
        }

        int delimiterLength = delimiter.length;
        if (delimiterLength == 1) {
            return true;
        }

        if (index + delimiterLength > s.length()) {
            return false;
        }

        for (int i = 1; i < delimiterLength; i++) {
            if (s.charAt(index + i) != delimiter[i]) {
                return false;
            }
        }

        return true;
    }
}
