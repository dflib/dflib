package org.dflib.print;

import org.dflib.DataFrame;

class DataFrameTruncator {

    public static DataFrameTruncator create(DataFrame dataFrame, int maxHeight) {

        if (maxHeight <= 0) {
            maxHeight = 1;
        }

        int h = dataFrame.height();
        if (h <= maxHeight) {
            return new DataFrameTruncator(dataFrame, dataFrame, DataFrame.empty(dataFrame.getColumnsIndex()), false);
        }

        int head = maxHeight / 2 + maxHeight % 2;
        int tail = maxHeight - head;
        return new DataFrameTruncator(dataFrame, dataFrame.head(head), dataFrame.tail(tail), true);
    }

    final DataFrame dataFrame;
    final DataFrame head;
    final DataFrame tail;
    final boolean truncated;

    private DataFrameTruncator(DataFrame dataFrame, DataFrame head, DataFrame tail, boolean truncated) {
        this.tail = tail;
        this.dataFrame = dataFrame;
        this.head = head;
        this.truncated = truncated;
    }
}
