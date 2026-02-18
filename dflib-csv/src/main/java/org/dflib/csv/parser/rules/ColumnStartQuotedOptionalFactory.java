package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnStartQuotedOptionalFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new ColumnStartQuotedOptional(format.quote().quoteChar());
    }

    static class ColumnStartQuotedOptional implements ParserRule {
        private final char quote;

        ColumnStartQuotedOptional(char quote) {
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
                    context.overrideState(ParserState.END_QUOTED);
                    return i + 1;
                }
            }
            return CONTINUE;
        }
    }
}
