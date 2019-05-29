package com.nhl.dflib.aggregate;

import com.nhl.dflib.RowToValueMapper;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collector;

public class AggregatorFunctions {

    public static RowToValueMapper<String> toString(String column) {
        RowToValueMapper<?> reader = RowToValueMapper.columnReader(column);
        return r -> String.valueOf(reader.map(r));
    }

    public static RowToValueMapper<String> toString(int column) {
        RowToValueMapper<?> reader = RowToValueMapper.columnReader(column);
        return r -> String.valueOf(reader.map(r));
    }

    public static Collector<Number, List<Double>, Double> medianCollector() {
        return new AggregationCollector<>(
                ArrayList::new,
                (list, d) -> {
                    if (d != null) {
                        list.add(d.doubleValue());
                    }
                },
                medianWithSideEffects());
    }

    private static Function<List<Double>, Double> medianWithSideEffects() {
        return list -> {

            if (list.isEmpty()) {
                return 0.;
            }

            switch (list.size()) {
                case 0:
                    return 0.;
                case 1:
                    return list.get(0);
                default:
                    // side effect - resorting the list; sicne the list is not exposed outside of "medianCollector", this should
                    // not cause any issues
                    Collections.sort(list);

                    int m = list.size() / 2;

                    int odd = list.size() % 2;
                    if (odd == 1) {
                        return list.get(m);
                    }

                    double d1 = list.get(m - 1);
                    double d2 = list.get(m);
                    return d1 + (d2 - d1) / 2.;
            }
        };
    }
}
