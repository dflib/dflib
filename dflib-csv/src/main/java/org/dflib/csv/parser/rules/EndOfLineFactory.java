package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class EndOfLineFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return switch (format.lineBreak()) {
            case AUTO -> new AnyLineEnd();
            case LF -> new LfLineEnd();
            case CR -> new CrLineEnd();
            case CRLF -> new CrLfLineEnd();
        };
    }

    private static class AnyLineEnd implements ParserRule {
        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
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
                } else if (context.isLastSliceQuoted()) {
                    throw new IllegalStateException("Unexpected data after closing quote");
                }
            }
            return CONTINUE;
        }
    }

    private static class LfLineEnd implements ParserRule {
        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if ('\n' == data[i]) {
                    context.markRowEnd(i);
                    return i + 1;
                } else if (context.isLastSliceQuoted()) {
                    throw new IllegalStateException("Unexpected data after closing quote");
                }
            }
            return CONTINUE;
        }
    }

    private static class CrLineEnd implements ParserRule {
        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if ('\r' == data[i]) {
                    context.markRowEnd(i);
                    return i + 1;
                } else if (context.isLastSliceQuoted()) {
                    throw new IllegalStateException("Unexpected data after closing quote");
                }
            }
            return CONTINUE;
        }
    }

    private static class CrLfLineEnd implements ParserRule {
        @Override
        public int consume(RuleContext context, DataSlice slice) {
            boolean seenCR = false;
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if (seenCR) {
                    if (data[i] == '\n') {
                        context.markRowEnd(i - 1);
                        return i + 1;
                    } else if (context.isLastSliceQuoted()) {
                        throw new IllegalStateException("Unexpected data after closing quote");
                    }
                } else {
                    if (data[i] == '\r') {
                        seenCR = true;
                    } else if (context.isLastSliceQuoted()) {
                        throw new IllegalStateException("Unexpected data after closing quote");
                    }
                }
            }
            return CONTINUE;
        }
    }
}
