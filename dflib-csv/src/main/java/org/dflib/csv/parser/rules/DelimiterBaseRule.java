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

    protected static void throwUnexpectedDataAfterQuote() {
        throw new IllegalStateException("Unexpected data after closing quote");
    }
}

