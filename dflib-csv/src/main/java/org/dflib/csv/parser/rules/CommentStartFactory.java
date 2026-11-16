package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;

class CommentStartFactory implements RuleFactory {

    @Override
    public ParserRule create(CsvFormat format) {
        char[] commentPrefix = (format.comment() == null || format.comment().isEmpty())
                ? null
                : format.comment().toCharArray();
        return new CommentStart(commentPrefix);
    }

    static class CommentStart implements ParserRule {
        private final char[] commentPrefix;

        CommentStart(char[] commentPrefix) {
            this.commentPrefix = commentPrefix;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            if (commentPrefix == null) {
                return slice.from();
            }
            int from = slice.from();
            int len = Math.min(slice.to() - from, commentPrefix.length);
            char[] data = slice.data();
            for (int i = 0; i < len; i++) {
                if (data[from + i] != commentPrefix[i]) {
                    return from;
                }
            }
            if (len < commentPrefix.length) {
                // the prefix may continue in the next buffer read
                return CONTINUE;
            }
            context.skipRow(from);
            context.overrideState(ParserState.END_OF_LINE);
            return from;
        }
    }
}
