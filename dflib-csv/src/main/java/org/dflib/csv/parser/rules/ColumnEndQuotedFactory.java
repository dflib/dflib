package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnEndQuotedFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        char quote = format.quote().quoteChar();
        return switch (format.escape()) {
            case DOUBLE -> new ColumnEndQuotedDouble(quote);
            case BACKSLASH -> new ColumnEndQuotedEscaped(quote, '\\');
            case CUSTOM -> new ColumnEndQuotedEscaped(quote, format.escapeChar());
            case NONE -> new ColumnEndQuotedNone(quote);
        };
    }

    static final class ColumnEndQuotedDouble implements ParserRule {
        private final char quote;
        private int trailingQuoteOffsetAtEof;

        ColumnEndQuotedDouble(char quote) {
            this.quote = quote;
            this.trailingQuoteOffsetAtEof = -1;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            boolean pendingQuote = false;
            trailingQuoteOffsetAtEof = -1;
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if (data[i] == quote) {
                    if (pendingQuote) {
                        pendingQuote = false;
                        continue;
                    }
                    pendingQuote = true;
                    if (i + 1 >= to) {
                        trailingQuoteOffsetAtEof = i - from;
                        return CONTINUE;
                    }
                    if (data[i + 1] == quote) {
                        context.markColumnEscape();
                        continue;
                    }
                    context.markColumnEnd(i, false);
                    return i + 1;
                } else {
                    pendingQuote = false;
                }
            }
            return CONTINUE;
        }

        @Override
        public void validateAtEof(RuleContext context) {
            if (trailingQuoteOffsetAtEof >= 0) {
                int quotePosition = context.activeSlice().from() + trailingQuoteOffsetAtEof;
                context.markColumnEnd(quotePosition, false);
                return;
            }
            throw new IllegalStateException("No closing quote found");
        }
    }

    /**
     * Handles quoted columns with an escape char distinct from the quote. Within a quoted value, the escape char
     * escapes the quote char and itself, any other char following the escape is taken literally (along with the
     * escape).
     */
    static final class ColumnEndQuotedEscaped implements ParserRule {
        private final char quote;
        private final char escapeChar;

        ColumnEndQuotedEscaped(char quote, char escapeChar) {
            this.quote = quote;
            this.escapeChar = escapeChar;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c == escapeChar) {
                    if (i + 1 >= to) {
                        // need the next char to decide; the column will be rescanned when more data is available
                        return CONTINUE;
                    }

                    char next = data[i + 1];
                    if (next == quote || next == escapeChar) {
                        context.markColumnEscape();
                    }
                    i++;
                } else if (c == quote) {
                    context.markColumnEnd(i, false);
                    return i + 1;
                }
            }
            return CONTINUE;
        }

        @Override
        public void validateAtEof(RuleContext context) {
            throw new IllegalStateException("No closing quote found");
        }
    }

    static final class ColumnEndQuotedNone implements ParserRule {
        private final char quote;

        ColumnEndQuotedNone(char quote) {
            this.quote = quote;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                if (data[i] == quote) {
                    context.markColumnEnd(i, false);
                    return i + 1;
                }
            }
            return CONTINUE;
        }

        @Override
        public void validateAtEof(RuleContext context) {
            throw new IllegalStateException("No closing quote found");
        }
    }
}
