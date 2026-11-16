package org.dflib.csv.parser.rules;

import java.util.Objects;

abstract class DelimiterBaseRule implements ParserRule {

    protected static char[] requireDelimiter(char[] delimiter) {
        Objects.requireNonNull(delimiter);
        if (delimiter.length == 0) {
            throw new IllegalArgumentException("Delimiter cannot be empty");
        }
        return delimiter;
    }

    /**
     * Builds a lookup table of the line break, delimiter and escape chars, so that the per-char loops can skip the value
     * chars with a cheap check.
     */
    protected static boolean[] specialChars(char[] delimiter, char escapeChar) {
        char max = (char) Math.max('\r', escapeChar);
        for (char c : delimiter) {
            max = (char) Math.max(max, c);
        }

        boolean[] special = new boolean[max + 1];
        special['\n'] = true;
        special['\r'] = true;
        for (char c : delimiter) {
            special[c] = true;
        }
        if (escapeChar != 0) {
            special[escapeChar] = true;
        }
        return special;
    }

    /**
     * Checks a char after a closing quote. Only spaces and tabs are allowed.
     */
    protected static void checkAfterQuote(char c) {
        if (c != ' ' && c != '\t') {
            throw new IllegalStateException("Unexpected data after closing quote");
        }
    }
}

