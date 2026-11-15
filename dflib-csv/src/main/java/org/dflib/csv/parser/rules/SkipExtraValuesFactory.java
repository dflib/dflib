package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;

/**
 * Skips the rest of the row after the last column defined by the header.
 */
class SkipExtraValuesFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new SkipExtraValues(format);
    }

    static final class SkipExtraValues implements ParserRule {

        private final char quote;
        private final boolean doubleQuoteEscape;
        private final char escapeChar;
        private final char[] delimiter;
        private final LineBreak lineBreak;

        // row state, kept between the calls, so that the consumed data is not rescanned
        private boolean sawData;
        private boolean valueStart;
        private boolean inQuotes;
        private boolean pendingQuote;
        private boolean escapeNext;
        private int delimiterCharsMatched;

        SkipExtraValues(CsvFormat format) {
            this.quote = format.quote().quoteChar();
            this.doubleQuoteEscape = format.escape() == Escape.DOUBLE;
            this.escapeChar = RuleFactorySupport.getEscapeChar(format);
            this.delimiter = format.delimiter().asArray();
            this.lineBreak = format.lineBreak();
            resetRowState();
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();

            for (int i = from; i < to; i++) {
                char c = data[i];

                if (inQuotes) {
                    if (escapeNext) {
                        escapeNext = false;
                        continue;
                    }

                    if (pendingQuote) {
                        pendingQuote = false;
                        if (c == quote) {
                            // escaped quote, i.e. ""
                            continue;
                        }

                        // the previous quote closed the value
                        inQuotes = false;
                    } else {
                        if (c == quote) {
                            if (doubleQuoteEscape) {
                                // closing or escaped quote, decided by the next char
                                pendingQuote = true;
                            } else {
                                inQuotes = false;
                            }
                        } else if (escapeChar != 0 && c == escapeChar) {
                            escapeNext = true;
                        }
                        continue;
                    }
                }

                switch (lineBreak) {
                    case LF -> {
                        if (c == '\n') {
                            return endRow(context, i, i + 1);
                        }
                    }
                    case CR -> {
                        if (c == '\r') {
                            return endRow(context, i, i + 1);
                        }
                    }
                    case AUTO -> {
                        if (c == '\n') {
                            return endRow(context, i, i + 1);
                        } else if (c == '\r') {
                            if (i + 1 >= to) {
                                // need the next char to tell CRLF from CR, rescan when more data is available
                                slice.setFrom(i);
                                return CONTINUE;
                            }
                            return endRow(context, i, data[i + 1] == '\n' ? i + 2 : i + 1);
                        }
                    }
                    case CRLF -> {
                        if (c == '\r') {
                            if (i + 1 >= to) {
                                slice.setFrom(i);
                                return CONTINUE;
                            } else if (data[i + 1] == '\n') {
                                return endRow(context, i, i + 2);
                            }
                        }
                    }
                }

                sawData = true;

                if (escapeNext) {
                    escapeNext = false;
                    valueStart = false;
                    delimiterCharsMatched = 0;
                    continue;
                }

                if (valueStart && quote != 0 && c == quote) {
                    valueStart = false;
                    inQuotes = true;
                    continue;
                }

                if (c == delimiter[delimiterCharsMatched]) {
                    if (++delimiterCharsMatched == delimiter.length) {
                        delimiterCharsMatched = 0;
                        valueStart = true;
                    }
                    continue;
                }

                delimiterCharsMatched = 0;
                valueStart = false;
                if (escapeChar != 0 && c == escapeChar) {
                    escapeNext = true;
                }
            }

            slice.setFrom(to);
            return CONTINUE;
        }

        @Override
        public void validateAtEof(RuleContext context) {
            if (inQuotes && !pendingQuote) {
                throw new IllegalStateException("No closing quote found");
            }
            if (sawData) {
                context.markExtraValues();
            }
        }

        private int endRow(RuleContext context, int lineBreakPosition, int next) {
            if (sawData) {
                context.markExtraValues();
            }
            resetRowState();
            context.markRowEnd(lineBreakPosition);
            return next;
        }

        private void resetRowState() {
            sawData = false;
            valueStart = true;
            inQuotes = false;
            pendingQuote = false;
            escapeNext = false;
            delimiterCharsMatched = 0;
        }
    }
}
