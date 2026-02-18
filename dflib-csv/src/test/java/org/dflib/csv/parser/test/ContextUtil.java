package org.dflib.csv.parser.test;

import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;

public class ContextUtil {

    public static ParserContext newContext() {
        ParserContext parserContext = new ParserContext();
        parserContext.setCallback(new TestCallback());
        return parserContext;
    }

    private static class TestCallback implements DataCallback {
        int columns;
        int rows;

        @Override
        public void onNewColumn(DataSlice slice) {
            columns++;
        }

        @Override
        public void onNewRow(DataSlice[] row) {
            rows++;
        }
    }

}
