package com.nhl.dflib.groupby;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.Hasher;

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

        Index columns = df.getColumns();

        for (Object[] r : df) {
            Object key = hasher.map(columns, r);
            ((List<Object[]>) groups.computeIfAbsent(key, k -> new ArrayList<>())).add(r);
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(DataFrame.fromRows(columns, (List<Object[]>) e.getValue()));
        }

        return new GroupBy(df.getColumns(), (Map<Object, DataFrame>) groups);
    }
}
