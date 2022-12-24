package com.nhl.dflib.groupby;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesGroupBy;
import com.nhl.dflib.ValueMapper;
import com.nhl.dflib.builder.IntAccum;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @since 0.6
 */
public class SeriesGrouper<T> {

    private ValueMapper<T, ?> hasher;

    public SeriesGrouper(ValueMapper<T, ?> hasher) {
        this.hasher = hasher;
    }

    public SeriesGroupBy<T> group(Series<T> s) {

        // Intentionally using generics-free map to be able to reset the internal object and avoid copying the map
        Map groups = new LinkedHashMap();

        int len = s.size();
        for (int i = 0; i < len; i++) {

            Object key = hasher.map(s.get(i));

            // skipping null keys kinda like pandas... The problem with nulls in DFLib is that Java Map.computeIfAbsent
            // would allow to store a null key, but would blow up when trying to "get" it, so we kind of go with the flow
            // here

            if (key != null) {
                ((IntAccum) groups.computeIfAbsent(key, k -> new IntAccum())).pushInt(i);
            }
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(((IntAccum) e.getValue()).toSeries());
        }

        return new SeriesGroupBy<>(s, (Map<Object, IntSeries>) groups);
    }
}
