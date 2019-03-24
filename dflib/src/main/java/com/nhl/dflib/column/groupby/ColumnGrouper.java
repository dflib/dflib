package com.nhl.dflib.column.groupby;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.GroupBy;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.column.ColumnDataFrame;
import com.nhl.dflib.map.Hasher;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.IndexedSeries;
import com.nhl.dflib.series.ListSeries;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class ColumnGrouper {

    private Hasher hasher;

    public ColumnGrouper(Hasher hasher) {
        this.hasher = hasher;
    }

    @SuppressWarnings("unchecked")
    public GroupBy group(DataFrame df) {

        // Intentionally using generics-free map to be able to reset the internal object and avoid copying the map
        Map groups = new LinkedHashMap();

        Index columns = df.getColumns();

        int i = 0;
        for (RowProxy r : df) {
            Object key = hasher.map(r);
            ((List<Integer>) groups.computeIfAbsent(key, k -> new ArrayList<>())).add(Integer.valueOf(i));
            i++;
        }

        int w = columns.size();
        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;

            Series<Integer> index = new ListSeries<>((List<Integer>) e.getValue());
            Series[] data = new Series[w];

            Iterator<Series<?>> ls = df.getDataColumns().iterator();
            for (int j = 0; j < w; j++) {
                data[j] = new IndexedSeries(ls.next(), index);
            }

            e.setValue(new ColumnDataFrame(columns, data));
        }

        return new GroupBy(columns, (Map<Object, DataFrame>) groups);
    }
}
