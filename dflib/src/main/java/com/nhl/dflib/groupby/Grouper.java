package com.nhl.dflib.groupby;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.seriesbuilder.IntAccumulator;
import com.nhl.dflib.Hasher;
import com.nhl.dflib.row.RowProxy;

import java.util.LinkedHashMap;
import java.util.Map;

public class Grouper {

    private Hasher hasher;

    public Grouper(Hasher hasher) {
        this.hasher = hasher;
    }

    @SuppressWarnings("unchecked")
    public GroupBy group(DataFrame df) {

        // Intentionally using generics-free map to be able to reset the internal object and avoid copying the map
        Map groups = new LinkedHashMap();

        int i = 0;
        for (RowProxy r : df) {
            Object key = hasher.map(r);
            ((IntAccumulator) groups.computeIfAbsent(key, k -> new IntAccumulator())).add(i);
            i++;
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(((IntAccumulator) e.getValue()).toIntSeries());
        }

        return new GroupBy(df, (Map<Object, IntSeries>) groups);
    }
}
