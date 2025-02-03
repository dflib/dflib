package org.dflib.op;

import org.dflib.BooleanSeries;
import org.dflib.builder.BoolBuilder;

public class BooleanSeriesOps {

    public static BooleanSeries andAll(BooleanSeries... series) {

        int w = series.length;
        if (w == 0) {
            throw new IllegalArgumentException("No series to 'and'");
        } else if (w == 1) {
            return series[0];
        }

        int h = series[0].size();

        for (int j = 1; j < w; j++) {
            if (h != series[j].size()) {
                throw new IllegalArgumentException("Can't 'and' Series with different sizes: " + h + " vs " + series[j].size());
            }
        }

        return BoolBuilder.buildSeries(i -> {
            boolean b = series[0].getBool(i);
            for (int j = 1; j < w; j++) {
                b = b && series[j].getBool(i);
            }
            return b;
        }, h);
    }

    public static BooleanSeries orAll(BooleanSeries... series) {

        int w = series.length;
        if (w == 0) {
            throw new IllegalArgumentException("No series to 'or'");
        } else if (w == 1) {
            return series[0];
        }

        int h = series[0].size();

        for (int j = 1; j < w; j++) {
            if (h != series[j].size()) {
                throw new IllegalArgumentException("Can't 'or' Series with different sizes: " + h + " vs " + series[j].size());
            }
        }

        return BoolBuilder.buildSeries(i -> {
            boolean b = series[0].getBool(i);
            for (int j = 1; j < w; j++) {
                b = b || series[j].getBool(i);
            }
            return b;
        }, h);
    }
}
