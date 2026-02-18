package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;

final class RuleFactorySupport {

    private RuleFactorySupport() {
    }

    static boolean shouldHandleEscapes(CsvFormat format) {
        return format.escape() == Escape.BACKSLASH || format.escape() == Escape.CUSTOM;
    }

    static char getEscapeChar(CsvFormat format) {
        return switch (format.escape()) {
            case BACKSLASH -> '\\';
            case CUSTOM -> format.escapeChar();
            case NONE, DOUBLE -> 0;
        };
    }
}
