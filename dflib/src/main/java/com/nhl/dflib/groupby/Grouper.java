package com.nhl.dflib.groupby;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Series;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.ListSeries;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
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
            ((List<Integer>) groups.computeIfAbsent(key, k -> new ArrayList<>())).add(Integer.valueOf(i));
            i++;
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(new ListSeries<>((List<Integer>) e.getValue()));
        }

        return new GroupBy(df, (Map<Object, Series<Integer>>) groups);
    }
}
