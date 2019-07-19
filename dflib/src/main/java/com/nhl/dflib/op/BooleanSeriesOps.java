package com.nhl.dflib.op;

import com.nhl.dflib.BooleanSeries;
import com.nhl.dflib.series.BooleanArraySeries;

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

        boolean[] and = new boolean[h];
        for (int i = 0; i < h; i++) {

            boolean b = series[0].getBoolean(i);
            for (int j = 1; j < w; j++) {
                b = b && series[j].getBoolean(i);
            }

            and[i] = b;
        }

        return new BooleanArraySeries(and);
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

        boolean[] or = new boolean[h];
        for (int i = 0; i < h; i++) {

            boolean b = series[0].getBoolean(i);
            for (int j = 1; j < w; j++) {
                b = b || series[j].getBoolean(i);
            }

            or[i] = b;
        }

        return new BooleanArraySeries(or);
    }
}
