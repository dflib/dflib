package org.dflib.print;

import org.dflib.DataFrame;

class DataFrameTruncator {

    DataFrame dataFrame;
    boolean truncated;
    int top;
    int bottom;

    private DataFrameTruncator(DataFrame dataFrame, boolean truncated, int top, int bottom) {
        this.dataFrame = dataFrame;
        this.truncated = truncated;
        this.top = top;
        this.bottom = bottom;
    }

    public static DataFrameTruncator create(DataFrame dataFrame, int maxHeight) {

        if (maxHeight < 0) {
            maxHeight = 0;
        }

        int h = dataFrame.height();
        if (h <= maxHeight) {
            return new DataFrameTruncator(dataFrame, false, h, 0);
        }

        int head = maxHeight / 2 + maxHeight % 2;
        int tail = maxHeight - head;
        return new DataFrameTruncator(dataFrame, true, head, tail);
    }

    public DataFrame top() {
        return truncated ? dataFrame.head(top) : dataFrame;
    }

    public DataFrame bottom() {
        return truncated ? dataFrame.tail(bottom) : DataFrame.empty(dataFrame.getColumnsIndex());
    }

    public boolean isTruncated() {
        return truncated;
    }

    public int height() {
        return truncated ? top + bottom + 1 : dataFrame.height();
    }
}
