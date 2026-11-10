package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class ColumnStartQuotedFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        return new ColumnStartQuoted(format.quote().quoteChar(), format.delimiter().asArray()[0]);
    }

    /**
     * Starts a column with a mandatory quote. The quote may be preceded by spaces or tabs. A column that doesn't
     * start with a quote is still accepted and is read as an unquoted one. This way the unquoted nulls (that are
     * either empty or equal to the null string) can be read, as a quoted value is never null.
     */
    static class ColumnStartQuoted implements ParserRule {
        private final char quote;
        private final char delimiterFirstChar;

        ColumnStartQuoted(char quote, char delimiterFirstChar) {
            this.quote = quote;
            this.delimiterFirstChar = delimiterFirstChar;
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

                if ((c != ' ' && c != '\t') || c == delimiterFirstChar) {
                    // an unquoted column, the ColumnEndQuoted* rule that follows will be a noop. Should reprocess the
                    // chars, as they are a part of the column value, or a delimiter if the column value is empty
                    context.markColumnStart(from);
                    return from;
                }
            }

            if (from < to) {
                // only whitespace so far, and a quote may still follow. Starting an unquoted column for now, so that
                // the whitespace is not lost if this is the end of the file. It will be restarted as a quoted one
                // if a quote is found in the next chunk of data
                context.markColumnStart(from);
            }
            return CONTINUE;
        }
    }
}
