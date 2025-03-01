package org.dflib.print;

import org.dflib.Series;
import org.dflib.series.EmptySeries;

class SeriesTruncator {

    final Series<?> series;
    final boolean truncated;
    final int top;
    final int bottom;

    private SeriesTruncator(Series<?> series, boolean truncated, int top, int bottom) {
        this.series = series;
        this.truncated = truncated;
        this.top = top;
        this.bottom = bottom;
    }

    public static SeriesTruncator create(Series<?> series, int maxSize) {

        if (maxSize < 0) {
            maxSize = 0;
        }

        int h = series.size();
        if (h <= maxSize) {
            return new SeriesTruncator(series, false, h, 0);
        }

        int head = maxSize / 2 + maxSize % 2;
        int tail = maxSize - head;
        return new SeriesTruncator(series, true, head, tail);
    }

    public Series<?> top() {
        return truncated ? series.head(top) : series;
    }

    public Series<?> bottom() {
        return truncated ? series.tail(bottom) : new EmptySeries();
    }

    public boolean isTruncated() {
        return truncated;
    }

    public int size() {
        return truncated ? top + bottom + 1 : series.size();
    }
}
