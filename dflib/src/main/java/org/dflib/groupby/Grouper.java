package org.dflib.groupby;

import org.dflib.DataFrame;
import org.dflib.GroupBy;
import org.dflib.IntSeries;
import org.dflib.builder.IntAccum;
import org.dflib.Hasher;
import org.dflib.row.RowProxy;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;

public class Grouper {

    private Hasher hasher;

    public Grouper(Hasher hasher) {
        this.hasher = Objects.requireNonNull(hasher, "Null 'hasher'");
    }

    @SuppressWarnings("unchecked")
    public GroupBy group(DataFrame df) {

        // Intentionally using generics-free map to be able to reset the internal object and avoid copying the map
        Map groups = new LinkedHashMap();

        int i = 0;
        for (RowProxy r : df) {
            Object key = hasher.map(r);

            // skipping null keys kinda like pandas... The problem with nulls in DFLib is that Java Map.computeIfAbsent
            // would allow to store a null key, but would blow up when trying to "get" it, so we kind of go with the flow
            // here
            if(key != null) {
                ((IntAccum) groups.computeIfAbsent(key, k -> new IntAccum())).pushInt(i);
            }

            i++;
        }

        for (Object o : groups.entrySet()) {
            Map.Entry<?, Object> e = (Map.Entry) o;
            e.setValue(((IntAccum) e.getValue()).toSeries());
        }

        return new GroupBy(df, (Map<Object, IntSeries>) groups, null);
    }
}
