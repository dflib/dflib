package com.nhl.dflib.groupby;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.row.ArrayRowBuilder;
import com.nhl.dflib.row.RowProxy;

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

        ArrayRowBuilder rowBuilder = new ArrayRowBuilder(columns);

        for (RowProxy r : df) {
            Object key = hasher.map(r);

            // Internally "RowBuilder.bulkSet" will avoid value copy, and use the original array reference. So this
            // operation should be fast and take no extra memory
            r.copyAll(rowBuilder, 0);

            ((List<Object[]>) groups.computeIfAbsent(key, k -> new ArrayList<>())).add(rowBuilder.reset());
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(DataFrame.fromRowsList(columns, (List<Object[]>) e.getValue()));
        }

        return new GroupBy(columns, (Map<Object, DataFrame>) groups);
    }
}
