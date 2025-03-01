package org.dflib.print;

import org.dflib.Index;
import org.dflib.Series;
import org.dflib.series.EmptySeries;

class SeriesTruncator<T> {

    public static SeriesTruncator<String> create(Index index, int maxSize) {
        return create(index.toSeries(), maxSize);
    }

    public static <T> SeriesTruncator<T> create(Series<T> series, int maxSize) {

        if (maxSize <= 0) {
            maxSize = 1;
        }

        int h = series.size();
        if (h <= maxSize) {
            return new SeriesTruncator(series, series, new EmptySeries(), false);
        }

        int head = maxSize / 2 + maxSize % 2;
        int tail = maxSize - head;
        return new SeriesTruncator(series, series.head(head), series.tail(tail), true);
    }

    final Series<T> series;
    final Series<T> head;
    final Series<T> tail;
    final boolean truncated;

    private SeriesTruncator(Series<T> series, Series<T> head, Series<T> tail, boolean truncated) {
        this.series = series;
        this.head = head;
        this.tail = tail;
        this.truncated = truncated;
    }
}
