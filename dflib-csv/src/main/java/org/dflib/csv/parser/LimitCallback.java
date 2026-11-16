package org.dflib.csv.parser;

import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;
import org.dflib.csv.parser.context.ParserContext;

/**
 * DataCallback that checks limit borders.
 *
 * @param context parser context
 * @param dfBuilder to write data to
 * @param limit of the rows to read
 */
record LimitCallback(ParserContext context,
                     DataFrameBuilder dfBuilder,
                     int limit) implements DataCallback {

    @Override
    public void onNewRow(DataSlice[] slice) {
        if (context.checkLimit(limit)) {
            dfBuilder.onNewRow(slice);
        }
    }
}
