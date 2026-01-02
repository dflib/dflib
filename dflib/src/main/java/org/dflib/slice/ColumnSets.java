package org.dflib.slice;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class ColumnSets {

    static Series<?>[] mapIterables(DataFrame source, Exp<? extends Iterable<?>> mapper) {
        Series<? extends Iterable<?>> ranges = mapper.eval(source);

        int h = source.height();
        List<Object[]> data = new ArrayList<>();

        for (int i = 0; i < h; i++) {

            Iterable<?> r = ranges.get(i);
            if (r == null) {
                continue;
            }

            Iterator<?> rit = r.iterator();
            for (int j = 0; rit.hasNext(); j++) {

                if (j >= data.size()) {
                    data.add(new Object[h]);
                }

                data.get(j)[i] = rit.next();
            }
        }

        int w = data.size();
        Series<?>[] columns = new Series[w];

        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data.get(i));
        }

        return columns;
    }

    static Series<?>[] mapArrays(DataFrame source, Exp<? extends Object[]> mapper) {
        Series<? extends Object[]> ranges = mapper.eval(source);

        int h = source.height();
        List<Object[]> data = new ArrayList<>();

        for (int i = 0; i < h; i++) {

            Object[] r = ranges.get(i);
            if (r == null) {
                continue;
            }

            int rw = r.length;
            int dw = data.size();
            if (dw < rw) {
                for (int j = dw; j < rw; j++) {
                    data.add(new Object[h]);
                }
            }

            for (int j = 0; j < rw; j++) {
                data.get(j)[i] = r[j];
            }
        }

        int w = data.size();
        Series<?>[] columns = new Series[w];
        for (int i = 0; i < w; i++) {
            columns[i] = Series.of(data.get(i));
        }

        return columns;
    }
}
