package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnStartQuotedFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new ColumnStartQuoted(format.quote().quoteChar());
    }

    static class ColumnStartQuoted implements ParserRule {
        private final char quote;

        ColumnStartQuoted(char quote) {
            this.quote = quote;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            char[] data = slice.data();
            int from = slice.from();
            int to = slice.to();
            for (int i = from; i < to; i++) {
                char c = data[i];
                if (c == quote) {
                    // `+ 1` is to trim a quote char
                    context.markColumnStartQuoted(i + 1);
                    return i + 1;
                }
            }
            return CONTINUE;
        }
    }
}
