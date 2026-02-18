package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnStartAnyFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new ColumnStartAny(format.quote().quoteChar());
    }

    static class ColumnStartAny implements ParserRule {
        private final char quote;

        ColumnStartAny(char quote) {
            this.quote = quote;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int from = slice.from();
            if (slice.data(from) == quote) {
                context.overrideState(ParserState.START_QUOTED_OPTIONAL);
            } else {
                context.markColumnStart(from);
            }
            // should reprocess this char, as it could be a delimiter if the column value is empty
            return from;
        }
    }
}
