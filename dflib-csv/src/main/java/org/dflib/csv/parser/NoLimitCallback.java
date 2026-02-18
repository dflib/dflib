package org.dflib.csv.parser;

import org.dflib.builder.DataFrameAppender;
import org.dflib.csv.parser.context.DataCallback;
import org.dflib.csv.parser.context.DataSlice;

record NoLimitCallback(DataFrameAppender<DataSlice[]> appender) implements DataCallback {

    @Override
    public void onNewRow(DataSlice[] rowData) {
        appender.append(rowData);
    }

}
