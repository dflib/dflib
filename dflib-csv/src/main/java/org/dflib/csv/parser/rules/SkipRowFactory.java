package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.Escape;
import org.dflib.csv.parser.format.LineBreak;

/**
 * Skips a row, taking into account the line breaks in the quoted values. Used for the offset rows.
 */
class SkipRowFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new SkipRow(format);
    }

    static final class SkipRow implements ParserRule {

        private final char quote;
        private final boolean doubleQuoteEscape;
        private final char escapeChar;
        private final char[] delimiter;
        private final LineBreak lineBreak;

        // row state, kept between the calls, so that the consumed data is not rescanned
        private boolean valueStart;
        private boolean inQuotes;
        private boolean pendingQuote;
        private boolean escapeNext;
        private int delimiterCharsMatched;

        SkipRow(CsvFormat format) {
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
                            return endRow(i + 1);
                        }
                    }
                    case CR -> {
                        if (c == '\r') {
                            return endRow(i + 1);
                        }
                    }
                    case AUTO -> {
                        if (c == '\n') {
                            return endRow(i + 1);
                        } else if (c == '\r') {
                            if (i + 1 >= to) {
                                // need the next char to tell CRLF from CR, rescan when more data is available
                                slice.setFrom(i);
                                return CONTINUE;
                            }
                            return endRow(data[i + 1] == '\n' ? i + 2 : i + 1);
                        }
                    }
                    case CRLF -> {
                        if (c == '\r') {
                            if (i + 1 >= to) {
                                slice.setFrom(i);
                                return CONTINUE;
                            } else if (data[i + 1] == '\n') {
                                return endRow(i + 2);
                            }
                        }
                    }
                }

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

        private int endRow(int next) {
            resetRowState();
            return next;
        }

        private void resetRowState() {
            valueStart = true;
            inQuotes = false;
            pendingQuote = false;
            escapeNext = false;
            delimiterCharsMatched = 0;
        }
    }
}
