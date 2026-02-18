package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;

class DelimiterOrEndOfLineFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        boolean handleEscapes = RuleFactorySupport.shouldHandleEscapes(format);
        char escapeChar = RuleFactorySupport.getEscapeChar(format);

        if (format.lineBreak() == LineBreak.AUTO) {
            return handleEscapes
                    ? new DelimiterOrAnyEndOfLineEscaped(format.delimiter().asArray(), escapeChar)
                    : new DelimiterOrAnyEndOfLine(format.delimiter().asArray());
        }
        return handleEscapes
                ? new DelimiterOrEndOfLineEscaped(format.lineBreak(), format.delimiter().asArray(), escapeChar)
                : new DelimiterOrEndOfLine(format.lineBreak(), format.delimiter().asArray());
    }

    static class DelimiterOrEndOfLine extends DelimiterBaseRule {
        private final char[] delimiter;
        private final LineBreak lineBreak;

        DelimiterOrEndOfLine(LineBreak lineBreak, char[] delimiter) {
            this.delimiter = requireDelimiter(delimiter);
            this.lineBreak = lineBreak;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            // state, matching end-of-line and delimiter simultaneous
            int delimiterCharsMatched = 0;
            boolean seenCR = false;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
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
                    if (context.isLastSliceQuoted()) {
                        throwUnexpectedDataAfterQuote();
                    }
                    delimiterCharsMatched = 0;
                    seenCR = false;
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
            int delimiterCharsMatched = 0;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
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
                } else if (c == delimiter[delimiterCharsMatched]) {
                    if (++delimiterCharsMatched == delimiter.length) {
                        context.markColumnEnd(i - delimiter.length + 1, true);
                        return i + 1;
                    }
                } else {
                    if (context.isLastSliceQuoted()) {
                        throwUnexpectedDataAfterQuote();
                    }
                    delimiterCharsMatched = 0;
                }
            }
            return CONTINUE;
        }
    }

    static class DelimiterOrEndOfLineEscaped extends DelimiterBaseRule {
        private final char[] delimiter;
        private final LineBreak lineBreak;
        private final char escapeChar;

        DelimiterOrEndOfLineEscaped(LineBreak lineBreak, char[] delimiter, char escapeChar) {
            this.delimiter = requireDelimiter(delimiter);
            this.lineBreak = lineBreak;
            this.escapeChar = escapeChar;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int delimiterCharsMatched = 0;
            boolean seenCR = false;
            boolean escapeNext = false;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];

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

                if (context.isLastSliceQuoted()) {
                    throwUnexpectedDataAfterQuote();
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

        DelimiterOrAnyEndOfLineEscaped(char[] delimiter, char escapeChar) {
            this.delimiter = requireDelimiter(delimiter);
            this.escapeChar = escapeChar;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int delimiterCharsMatched = 0;
            boolean escapeNext = false;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];

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

                if (context.isLastSliceQuoted()) {
                    throwUnexpectedDataAfterQuote();
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
