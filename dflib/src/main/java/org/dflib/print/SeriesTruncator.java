package org.dflib.print;

import org.dflib.Series;
import org.dflib.series.EmptySeries;

/**
 * @since 0.6
 */
public class SeriesTruncator<T> {

    Series<T> series;
    boolean truncated;
    int head;
    int tail;

    private SeriesTruncator(Series<T> series, boolean truncated, int head, int tail) {
        this.series = series;
        this.truncated = truncated;
        this.head = head;
        this.tail = tail;
    }

    public static <T> SeriesTruncator<T> create(Series<T> series, int maxSize) {

        if (maxSize < 0) {
            maxSize = 0;
        }

        int h = series.size();
        if (h <= maxSize) {
            return new SeriesTruncator<>(series, false, h, 0);
        }

        int head = maxSize / 2 + maxSize % 2;
        int tail = maxSize - head;
        return new SeriesTruncator<>(series, true, head, tail);
    }

    public Series<T> head() {
        return truncated ? series.head(head) : series;
    }

    public Series<T> tail() {
        return truncated ? series.tail(tail) : new EmptySeries();
    }

    public boolean isTruncated() {
        return truncated;
    }

    public int size() {
        return truncated ? head + tail + 1 : series.size();
    }
}
