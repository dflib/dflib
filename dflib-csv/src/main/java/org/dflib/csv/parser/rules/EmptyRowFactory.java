package org.dflib.csv.parser.rules;

import org.dflib.csv.parser.ParserState;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.RuleContext;
import org.dflib.csv.parser.format.CsvFormat;
import org.dflib.csv.parser.format.LineBreak;

class EmptyRowFactory implements RuleFactory {
    @Override
    public ParserRule create(CsvFormat format) {
        if(format.skipEmptyRows()) {
            return new EmptyRow(format.lineBreak());
        } else {
            return (RuleContext context, DataSlice slice) -> slice.from();
        }
    }

    static class EmptyRow implements ParserRule {
        private final LineBreak lineBreak;

        EmptyRow(LineBreak lineBreak) {
            this.lineBreak = lineBreak;
        }

        @Override
        public int consume(RuleContext context, DataSlice slice) {
            int from = slice.from();
            char[] data = slice.data();
            char c = data[from];
            switch (lineBreak) {
                case AUTO -> {
                    if (c == '\n' || c == '\r') {
                        context.skipRow(from);
                        context.overrideState(ParserState.END_OF_LINE);
                    }
                }
                case LF -> {
                    if (c == '\n') {
                        context.skipRow(from);
                        context.overrideState(ParserState.END_OF_LINE);
                    }
                }
                case CR -> {
                    if (c == '\r') {
                        context.skipRow(from);
                        context.overrideState(ParserState.END_OF_LINE);
                    }
                }
                case CRLF -> {
                    if (c == '\r') {
                        int next = from + 1;
                        if (next < slice.to() && data[next] == '\n') {
                            context.skipRow(from);
                            context.overrideState(ParserState.END_OF_LINE);
                        }
                    }
                }
            }

            return from;
        }
    }
}
