package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class DelimiterFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        boolean handleEscapes = RuleFactorySupport.shouldHandleEscapes(format);
        char escapeChar = RuleFactorySupport.getEscapeChar(format);

        if (format.delimiter().isSingleChar()) {
            return handleEscapes
                    ? new DelimiterSingleCharEscaped(format.delimiter().singleChar(), escapeChar)
                    : new DelimiterSingleChar(format.delimiter().singleChar());
        } else {
            return handleEscapes
                    ? new DelimiterMultiCharEscaped(format.delimiter().asArray(), escapeChar)
                    : new DelimiterMultiChar(format.delimiter().asArray());
        }
    }

    private static class DelimiterSingleChar extends DelimiterBaseRule {
        private final char delimiter;

        DelimiterSingleChar(char delimiter) {
            this.delimiter = delimiter;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if (data[i] == delimiter) {
                    context.markColumnEnd(i, true);
                    return i + 1;
                } else if (context.isLastSliceQuoted()) {
                    throwUnexpectedDataAfterQuote();
                }
            }
            return CONTINUE;
        }
    }

    private static class DelimiterSingleCharEscaped extends DelimiterBaseRule {
        private final char delimiter;
        private final char escapeChar;

        DelimiterSingleCharEscaped(char delimiter, char escapeChar) {
            this.delimiter = delimiter;
            this.escapeChar = escapeChar;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            boolean escapeNext = false;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c == delimiter && !escapeNext) {
                    context.markColumnEnd(i, true);
                    return i + 1;
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
                }
            }

            return CONTINUE;
        }
    }

    private static class DelimiterMultiChar extends DelimiterBaseRule {
        private final char[] delimiter;

        DelimiterMultiChar(char[] delimiter) {
            this.delimiter = requireDelimiter(delimiter);
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int charsMatched = 0;
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if (delimiter[charsMatched] == data[i]) {
                    charsMatched++;
                } else {
                    if (context.isLastSliceQuoted()) {
                        throwUnexpectedDataAfterQuote();
                    }
                    charsMatched = 0;
                    continue;
                }

                if (charsMatched == delimiter.length) {
                    // found full delimiter
                    context.markColumnEnd(i - delimiter.length + 1, true);
                    return i + 1;
                }
            }

            return CONTINUE;
        }
    }

    private static class DelimiterMultiCharEscaped extends DelimiterBaseRule {
        private final char[] delimiter;
        private final char escapeChar;

        DelimiterMultiCharEscaped(char[] delimiter, char escapeChar) {
            this.delimiter = requireDelimiter(delimiter);
            this.escapeChar = escapeChar;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int charsMatched = 0;
            boolean escapeNext = false;

            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];

                if (c == delimiter[charsMatched] && !escapeNext) {
                    charsMatched++;
                    if (charsMatched == delimiter.length) {
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
                    charsMatched = 0;
                    continue;
                }

                charsMatched = 0;
            }

            return CONTINUE;
        }
    }

}
