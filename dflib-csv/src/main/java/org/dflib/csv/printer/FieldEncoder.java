package org.dflib.csv.printer;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.Quote;
import org.dflib.csv.parser.format.Trim;

import java.io.IOException;
import java.io.Writer;
import java.util.Objects;

/**
 * Encodes a single CSV field value to its textual representation
 * according to the rules defined in {@link CsvFormat}.
 */
class FieldEncoder {

    private final Quote quote;
    private final char quoteChar;
    private final Escape escape;
    private final boolean hasEscapeChar;
    private final char escapeChar;
    private final char[] delimiter;
    private final char delimiterFirstChar;
    private final String nullString;
    private final String comment;
    private final boolean trimLeft;
    private final boolean trimRight;
    private final String nullStringError;
    private final boolean nullStringIsComment;

    FieldEncoder(CsvFormat format) {
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

        Trim trim = format.trim();
        this.trimLeft = trim == Trim.LEFT || trim == Trim.FULL;
        this.trimRight = trim == Trim.RIGHT || trim == Trim.FULL;

        this.nullStringError = nullStringError();
        this.nullStringIsComment = nullString != null && comment != null && nullString.startsWith(comment);
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
                checkNullRepresentable(lineStart);
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

        // a value at the start of a line would be read as a comment. Quoting it in any position for consistency
        if (comment != null && s.startsWith(comment)) {
            return true;
        }

        // unquoted values are trimmed by the parser
        if (isTrimmable(s)) {
            return true;
        }

        // copying the fields to locals for the hot loop below
        char quoteChar = this.quoteChar;
        char delimiterFirstChar = this.delimiterFirstChar;

        // an unquoted escape char would be interpreted by the parser, so such values must be quoted (and escaped).
        // Without an escape char, falling back to the quote char to keep the loop condition simple
        char escapeOrQuoteChar = hasEscapeChar ? this.escapeChar : quoteChar;

        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == quoteChar
                    || c == '\r'
                    || c == '\n'
                    || c == escapeOrQuoteChar
                    // not checking for the full delimiter match, as a value that ends with a part of a multi-char
                    // delimiter would form a delimiter together with the one that follows it
                    || c == delimiterFirstChar) {
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

        if (isTrimmable(s)) {
            throw new IllegalArgumentException(
                    "Can't encode a value with leading or trailing whitespace without quoting, as it will be trimmed "
                            + "when read: " + s);
        }
    }

    // a null is always saved unquoted, as a quoted value is never read as null. So the null string must be readable
    // as is
    private void checkNullRepresentable(boolean lineStart) {
        if (nullStringError != null) {
            throw new IllegalArgumentException(
                    "Can't encode a null, as the null string " + nullStringError + ": " + nullString);
        }

        if (lineStart && nullStringIsComment) {
            throw new IllegalArgumentException(
                    "Can't encode a null at the start of a line, as the null string starts with the comment prefix: "
                            + nullString);
        }
    }

    private String nullStringError() {
        String s = nullString;
        if (s == null) {
            return null;
        }

        if (isTrimmable(s)) {
            return "has leading or trailing whitespace that will be trimmed when read";
        }

        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == '\r' || c == '\n') {
                return "contains a line break";
            } else if (c == delimiterFirstChar) {
                return "contains a delimiter char";
            } else if (!quote.noQuote() && c == quoteChar) {
                return "contains the quote char";
            } else if (hasEscapeChar && c == escapeChar) {
                return "contains the escape char";
            }
        }

        return null;
    }

    private boolean isTrimmable(String s) {
        return !s.isEmpty()
                && ((trimLeft && Character.isWhitespace(s.charAt(0)))
                || (trimRight && Character.isWhitespace(s.charAt(s.length() - 1))));
    }

    private void encodeUnquoted(String s, Appendable out) throws IOException {
        int runStart = 0;
        for (int i = 0, len = s.length(); i < len; i++) {
            char c = s.charAt(i);
            if (c == '\r' || c == '\n') {
                // the parser can't read escaped line breaks outside of quotes
                throw new IllegalArgumentException("Can't encode a value containing a line break without quoting: " + s);
            } else if (c == delimiterFirstChar) {
                if (hasEscapeChar) {
                    // escaping every occurrence of the first delimiter char (and not just the full delimiters), as a
                    // value that ends with a part of a multi-char delimiter would form a delimiter together with the
                    // one that follows it
                    appendRun(s, runStart, i, out);
                    out.append(escapeChar);
                    runStart = i;
                } else if (isDelimiterOrItsPrefixAt(s, i)) {
                    throw new IllegalArgumentException(
                            "Can't encode a value containing a delimiter, or ending with a part of it, without quoting "
                                    + "and with no escape char. Use Escape.BACKSLASH or a custom escape char: " + s);
                }
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

    // checks whether there's a delimiter at the index, or a beginning of a multi-char delimiter at the end of the value
    private boolean isDelimiterOrItsPrefixAt(String s, int index) {
        int len = Math.min(delimiter.length, s.length() - index);
        for (int i = 0; i < len; i++) {
            if (s.charAt(index + i) != delimiter[i]) {
                return false;
            }
        }

        return true;
    }
}
