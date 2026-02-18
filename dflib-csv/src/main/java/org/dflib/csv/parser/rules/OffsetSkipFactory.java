package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;

/**
 * Special rule that skips row taking into account new line chars in the quoted columns.
 */
class OffsetSkipFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new OffsetRowSkipRule(format);
    }

    final static class OffsetRowSkipRule implements ParserRule {

        private final LineBreak lineBreak;
        private final char quote;
        private final Escape escape;
        private final char escapeChar;

        private boolean inQuotedValue;
        private boolean pendingDoubleQuote;
        private boolean escapeNextQuote;
        private boolean pendingCR;

        public OffsetRowSkipRule(CsvFormat format) {
            this.lineBreak = format.lineBreak();
            this.quote = format.quote().quoteChar();
            this.escape = format.escape();
            this.escapeChar = escape == Escape.BACKSLASH ? '\\' : format.escapeChar();
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();

            for (int i = from; i < to; i++) {
                char c = data[i];

                if (lineBreak == LineBreak.CRLF && pendingCR) {
                    pendingCR = false;
                    if (c == '\n') {
                        resetRowState();
                        return i + 1;
                    }
                }

                if (quote != 0) {
                    if (escape == Escape.DOUBLE) {
                        if (pendingDoubleQuote) {
                            pendingDoubleQuote = false;
                            if (c == quote) {
                                continue;
                            }
                            inQuotedValue = false;
                        }

                        if (c == quote) {
                            if (inQuotedValue) {
                                if (i + 1 >= to) {
                                    pendingDoubleQuote = true;
                                    continue;
                                }

                                if (data[i + 1] == quote) {
                                    i++;
                                    continue;
                                }
                            }

                            inQuotedValue = !inQuotedValue;
                            continue;
                        }
                    } else {
                        if (escape == Escape.BACKSLASH || escape == Escape.CUSTOM) {
                            if (escapeNextQuote) {
                                escapeNextQuote = false;
                                if (c == quote) {
                                    continue;
                                }
                            }

                            if (c == escapeChar) {
                                escapeNextQuote = true;
                                continue;
                            }
                        }

                        if (c == quote) {
                            inQuotedValue = !inQuotedValue;
                            continue;
                        }
                    }
                }

                if (inQuotedValue) {
                    continue;
                }

                if (isLineBreak(data, i, to)) {
                    resetRowState();
                    return (lineBreak == LineBreak.AUTO && data[i] == '\r' && i + 1 < to && data[i + 1] == '\n')
                            || (lineBreak == LineBreak.CRLF && i + 1 < to)
                            ? i + 2
                            : i + 1;
                }
            }

            slice.setFrom(to);
            return CONTINUE;
        }

        private boolean isLineBreak(char[] data, int i, int to) {
            char c = data[i];

            return switch (lineBreak) {
                case LF -> c == '\n';
                case CR -> c == '\r';
                case AUTO -> c == '\n' || c == '\r';
                case CRLF -> {
                    if (c != '\r') {
                        yield false;
                    }

                    if (i + 1 < to) {
                        yield data[i + 1] == '\n';
                    }

                    pendingCR = true;
                    yield false;
                }
            };
        }

        private void resetRowState() {
            inQuotedValue = false;
            pendingDoubleQuote = false;
            escapeNextQuote = false;
            pendingCR = false;
        }
    }
}
