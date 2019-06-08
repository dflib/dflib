package com.nhl.dflib.print;

import com.nhl.dflib.DataFrame;

/**
 * @since 0.6
 */
public class DataFrameTruncator {

    DataFrame dataFrame;
    boolean truncated;
    int head;
    int tail;

    private DataFrameTruncator(DataFrame dataFrame, boolean truncated, int head, int tail) {
        this.dataFrame = dataFrame;
        this.truncated = truncated;
        this.head = head;
        this.tail = tail;
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

    public DataFrame head() {
        return truncated ? dataFrame.head(head) : dataFrame;
    }

    public DataFrame tail() {
        return truncated ? dataFrame.tail(tail) : DataFrame.newFrame(dataFrame.getColumnsIndex()).empty();
    }

    public boolean isTruncated() {
        return truncated;
    }

    public int height() {
        return truncated ? head + tail + 1 : dataFrame.height();
    }
}
