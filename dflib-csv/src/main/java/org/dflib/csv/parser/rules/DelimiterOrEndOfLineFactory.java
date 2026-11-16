package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;

class DelimiterOrEndOfLineFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        LineBreak lineBreak = format.lineBreak();
        char[] delimiter = format.delimiter().asArray();

        if (RuleFactorySupport.shouldHandleEscapes(format)) {
            char escapeChar = RuleFactorySupport.getEscapeChar(format);
            return lineBreak == LineBreak.AUTO
                    ? new DelimiterOrAnyEndOfLineEscaped(delimiter, escapeChar)
                    : new DelimiterOrEndOfLineEscaped(lineBreak, delimiter, escapeChar);
        }

        // specialized rules for a single-char delimiter
        if (delimiter.length == 1) {
            switch (lineBreak) {
                case AUTO:
                    return new DelimiterSingleCharOrAnyEndOfLine(delimiter[0]);
                case LF:
                    return new DelimiterSingleCharOrSingleCharEndOfLine(delimiter[0], '\n');
                case CR:
                    return new DelimiterSingleCharOrSingleCharEndOfLine(delimiter[0], '\r');
                default:
                    break;
            }
        }

        return lineBreak == LineBreak.AUTO
                ? new DelimiterOrAnyEndOfLine(delimiter)
                : new DelimiterOrEndOfLine(lineBreak, delimiter);
    }

    static class DelimiterOrEndOfLine extends DelimiterBaseRule {
        private final char[] delimiter;
        private final LineBreak lineBreak;
        private final boolean[] special;

        DelimiterOrEndOfLine(LineBreak lineBreak, char[] delimiter) {
            this.delimiter = requireDelimiter(delimiter);
            this.lineBreak = lineBreak;
            this.special = specialChars(delimiter, (char) 0);
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            // state, matching end-of-line and delimiter simultaneous
            int delimiterCharsMatched = 0;
            boolean seenCR = false;
            boolean afterQuote = context.isLastSliceQuoted();
            boolean[] special = this.special;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c >= special.length || !special[c]) {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    delimiterCharsMatched = 0;
                    seenCR = false;
                    continue;
                }

                // End-of-line check has priority
                if (c == '\r') {
                    if (lineBreak == LineBreak.CR) {
                        context.markRowEnd(i);
                        return i + 1;
                    } else if (lineBreak == LineBreak.CRLF) {
                        seenCR = !seenCR;
                    }
                } else if (c == '\n') {
                    if (lineBreak == LineBreak.LF) {
                        context.markRowEnd(i);
                        return i + 1;
                    } else if (lineBreak == LineBreak.CRLF) {
                        if (seenCR) {
                            context.markRowEnd(i - 1);
                            return i + 1;
                        }
                    }
                } else if (c == delimiter[delimiterCharsMatched]) {
                    if (++delimiterCharsMatched == delimiter.length) {
                        context.markColumnEnd(i - delimiter.length + 1, true);
                        return i + 1;
                    }
                } else {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    delimiterCharsMatched = 0;
                    seenCR = false;
                }
            }
            return CONTINUE;
        }
    }

    /**
     * A version of {@link DelimiterOrAnyEndOfLine} for the most common case of a single-char delimiter.
     */
    static class DelimiterSingleCharOrAnyEndOfLine extends DelimiterBaseRule {
        private final char delimiter;
        // skips most value chars with a single comparison
        private final char maxSpecialChar;

        DelimiterSingleCharOrAnyEndOfLine(char delimiter) {
            this.delimiter = delimiter;
            this.maxSpecialChar = (char) Math.max(delimiter, '\r');
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();

            boolean afterQuote = context.isLastSliceQuoted();
            char maxSpecialChar = this.maxSpecialChar;

            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c > maxSpecialChar) {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    continue;
                }

                // the delimiter can't be a line break char (see CsvFormatValidator), so the order of checks doesn't matter
                if (c == delimiter) {
                    context.markColumnEnd(i, true);
                    return i + 1;
                } else if (c == '\n') {
                    context.markRowEnd(i);
                    return i + 1;
                } else if (c == '\r') {
                    if (i + 1 < to && data[i + 1] == '\n') {
                        context.markRowEnd(i);
                        return i + 2;
                    }
                    context.markRowEnd(i);
                    return i + 1;
                } else if (afterQuote) {
                    checkAfterQuote(c);
                }
            }
            return CONTINUE;
        }
    }

    /**
     * A version of {@link DelimiterOrEndOfLine} for a single-char delimiter and a single-char line break (LF or CR).
     */
    static class DelimiterSingleCharOrSingleCharEndOfLine extends DelimiterBaseRule {
        private final char delimiter;
        private final char lineBreak;
        private final char maxSpecialChar;

        DelimiterSingleCharOrSingleCharEndOfLine(char delimiter, char lineBreak) {
            this.delimiter = delimiter;
            this.lineBreak = lineBreak;
            this.maxSpecialChar = (char) Math.max(delimiter, '\r');
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();

            boolean afterQuote = context.isLastSliceQuoted();
            char maxSpecialChar = this.maxSpecialChar;

            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c > maxSpecialChar) {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    continue;
                }

                if (c == delimiter) {
                    context.markColumnEnd(i, true);
                    return i + 1;
                } else if (c == lineBreak) {
                    context.markRowEnd(i);
                    return i + 1;
                } else if (afterQuote && c != '\r' && c != '\n') {
                    // the other line break char is a value char, allowed after a quote
                    checkAfterQuote(c);
                }
            }
            return CONTINUE;
        }
    }

    static class DelimiterOrAnyEndOfLine extends DelimiterBaseRule {
        private final char[] delimiter;

        DelimiterOrAnyEndOfLine(char[] delimiter) {
            this.delimiter = requireDelimiter(delimiter);
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            boolean afterQuote = context.isLastSliceQuoted();
            char[] delimiter = this.delimiter;
            char first = delimiter[0];

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                // a value char
                if (c != first && c > '\r') {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    continue;
                }

                // End-of-line check has priority
                if (c == '\n') {
                    context.markRowEnd(i);
                    return i + 1;
                } else if (c == '\r') {
                    if (i + 1 < to && data[i + 1] == '\n') {
                        context.markRowEnd(i);
                        return i + 2;
                    }
                    context.markRowEnd(i);
                    return i + 1;
                } else if (c == first) {
                    // match the rest of the delimiter in place
                    int matched = 1;
                    while (matched < delimiter.length && i + matched < to && data[i + matched] == delimiter[matched]) {
                        matched++;
                    }

                    if (matched == delimiter.length) {
                        context.markColumnEnd(i, true);
                        return i + matched;
                    }

                    if (i + matched == to) {
                        // the delimiter may continue in the next read
                        return CONTINUE;
                    }
                }

                if (afterQuote) {
                    checkAfterQuote(c);
                }
            }
            return CONTINUE;
        }
    }

    static class DelimiterOrEndOfLineEscaped extends DelimiterBaseRule {
        private final char[] delimiter;
        private final LineBreak lineBreak;
        private final char escapeChar;
        private final boolean[] special;

        DelimiterOrEndOfLineEscaped(LineBreak lineBreak, char[] delimiter, char escapeChar) {
            this.delimiter = requireDelimiter(delimiter);
            this.lineBreak = lineBreak;
            this.escapeChar = escapeChar;
            this.special = specialChars(delimiter, escapeChar);
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int delimiterCharsMatched = 0;
            boolean seenCR = false;
            boolean escapeNext = false;
            boolean afterQuote = context.isLastSliceQuoted();
            boolean[] special = this.special;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c >= special.length || !special[c]) {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    escapeNext = false;
                    delimiterCharsMatched = 0;
                    seenCR = false;
                    continue;
                }

                if (c == '\r') {
                    if (lineBreak == LineBreak.CR) {
                        context.markRowEnd(i);
                        return i + 1;
                    }

                    if (lineBreak == LineBreak.CRLF) {
                        seenCR = !seenCR;
                        delimiterCharsMatched = 0;
                        if (escapeNext) {
                            escapeNext = false;
                        }
                        continue;
                    }
                } else if (c == '\n') {
                    if (lineBreak == LineBreak.LF) {
                        context.markRowEnd(i);
                        return i + 1;
                    }

                    if (lineBreak == LineBreak.CRLF && seenCR) {
                        context.markRowEnd(i - 1);
                        return i + 1;
                    }
                }

                if (c == delimiter[delimiterCharsMatched] && !escapeNext) {
                    delimiterCharsMatched++;
                    if (delimiterCharsMatched == delimiter.length) {
                        context.markColumnEnd(i - delimiter.length + 1, true);
                        return i + 1;
                    }
                    continue;
                }

                if (afterQuote) {
                    checkAfterQuote(c);
                }

                if (escapeNext) {
                    escapeNext = false;
                    delimiterCharsMatched = 0;
                    seenCR = false;
                    continue;
                }

                if (c == escapeChar) {
                    if (i + 1 >= to) {
                        return CONTINUE;
                    }

                    context.markColumnEscape();
                    escapeNext = true;
                    delimiterCharsMatched = 0;
                    seenCR = false;
                    continue;
                }

                delimiterCharsMatched = 0;
                seenCR = false;
            }

            return CONTINUE;
        }
    }

    static class DelimiterOrAnyEndOfLineEscaped extends DelimiterBaseRule {
        private final char[] delimiter;
        private final char escapeChar;
        private final boolean[] special;

        DelimiterOrAnyEndOfLineEscaped(char[] delimiter, char escapeChar) {
            this.delimiter = requireDelimiter(delimiter);
            this.escapeChar = escapeChar;
            this.special = specialChars(delimiter, escapeChar);
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int delimiterCharsMatched = 0;
            boolean escapeNext = false;
            boolean afterQuote = context.isLastSliceQuoted();
            boolean[] special = this.special;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c >= special.length || !special[c]) {
                    if (afterQuote) {
                        checkAfterQuote(c);
                    }
                    escapeNext = false;
                    delimiterCharsMatched = 0;
                    continue;
                }

                if (c == '\n') {
                    context.markRowEnd(i);
                    return i + 1;
                }

                if (c == '\r') {
                    if (i + 1 < to && data[i + 1] == '\n') {
                        context.markRowEnd(i);
                        return i + 2;
                    }

                    context.markRowEnd(i);
                    return i + 1;
                }

                if (c == delimiter[delimiterCharsMatched] && !escapeNext) {
                    delimiterCharsMatched++;
                    if (delimiterCharsMatched == delimiter.length) {
                        context.markColumnEnd(i - delimiter.length + 1, true);
                        return i + 1;
                    }
                    continue;
                }

                if (afterQuote) {
                    checkAfterQuote(c);
                }

                if (escapeNext) {
                    escapeNext = false;
                    continue;
                }

                if (c == escapeChar) {
                    if (i + 1 >= to) {
                        return CONTINUE;
                    }

                    context.markColumnEscape();
                    escapeNext = true;
                    delimiterCharsMatched = 0;
                    continue;
                }

                delimiterCharsMatched = 0;
            }

            return CONTINUE;
        }
    }

}
