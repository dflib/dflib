package org.dflib.op;

import org.dflib.Series;
import org.dflib.series.ArraySeries;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class SetOp {

    public static <T> Series<T> set(Series<T> src, int index, T newVal) {
        if (Objects.equals(src.get(index), newVal)) {
            return src;
        }

        int len = src.size();
        Object[] copy = new Object[len];
        src.copyTo(copy, 0, 0, len);
        copy[index] = newVal;

        return new ArraySeries(copy);
    }
}
