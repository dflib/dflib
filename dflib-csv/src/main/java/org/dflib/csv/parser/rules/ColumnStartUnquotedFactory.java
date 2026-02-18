package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnStartUnquotedFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new ColumnStartUnquoted();
    }

    static class ColumnStartUnquoted implements ParserRule {
        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int from = slice.from();
            context.markColumnStart(from);
            return from;
        }
    }
}
