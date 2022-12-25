package com.nhl.dflib.concat;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.JoinType;
import com.nhl.dflib.series.ArraySeries;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class VConcat {
    private static final EnumMap<JoinType, VConcat> JOINERS = new EnumMap<>(JoinType.class);

    static {
        for (JoinType s : JoinType.values()) {
            Function<Index[], Index> joiner;
            switch (s) {
                case inner:
                    joiner = VConcat::innerJoin;
                    break;
                case left:
                    joiner = VConcat::leftJoin;
                    break;
                case right:
                    joiner = VConcat::rightJoin;
                    break;
                case full:
                    joiner = VConcat::fullJoin;
                    break;
                default:
                    throw new IllegalStateException("Unexpected join semantics: " + s);
            }

            JOINERS.put(s, new VConcat(joiner));
        }
    }

    private Function<Index[], Index> zipper;

    protected VConcat(Function<Index[], Index> zipper) {
        this.zipper = zipper;
    }

    public static DataFrame concat(JoinType how, DataFrame... dfs) {

        switch (dfs.length) {
            case 0:
                return DataFrame.empty();
            case 1:
                return dfs[0];
            default:
                return getInstance(how).concat(dfs);
        }
    }

    public static VConcat getInstance(JoinType how) {
        return JOINERS.get(Objects.requireNonNull(how, "Null 'how' (join semantics)"));
    }

    private static Index innerJoin(Index[] indices) {

        Set<String> columns = new LinkedHashSet<>();

        for (String label : indices[0]) {
            columns.add(label);
        }

        for (int i = 1; i < indices.length; i++) {
            innerJoin(columns, indices[i]);
        }

        return Index.forLabels(columns.toArray(new String[columns.size()]));
    }

    private static void innerJoin(Set<String> columns, Index index) {

        Iterator<String> it = columns.iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (!index.hasLabel(c)) {
                it.remove();
            }
        }
    }

    private static Index fullJoin(Index[] indices) {
        Set<String> columns = new LinkedHashSet<>();

        for (Index i : indices) {
            for (String label : i) {
                columns.add(label);
            }
        }

        return Index.forLabels(columns.toArray(new String[columns.size()]));
    }

    private static Index leftJoin(Index[] indices) {
        return indices[0];
    }

    private static Index rightJoin(Index[] indices) {
        return indices[indices.length - 1];
    }

    public DataFrame concat(DataFrame... dfs) {
        Index[] indices = new Index[dfs.length];
        for (int i = 0; i < indices.length; i++) {
            indices[i] = dfs[i].getColumnsIndex();
        }


        int h = 0;
        for (int i = 0; i < dfs.length; i++) {
            h += dfs[i].height();
        }

        Index concatColumns = zipper.apply(indices);
        int w = concatColumns.size();

        Object[][] data = new Object[w][h];
        int voffset = 0;

        for (int i = 0; i < dfs.length; i++) {

            Index dfc = dfs[i].getColumnsIndex();
            int dfw = dfc.size();

            for (int j = 0; j < dfw; j++) {

                // need to rewind the iterator even if we exclude the series from copy
                Series<?> next = dfs[i].getColumn(j);
                int pos = mapSeriesPosition(concatColumns, dfc.getLabel(j));

                if (pos >= 0) {
                    next.copyTo(data[pos], 0, voffset, next.size());
                }
            }

            voffset += dfs[i].height();
        }

        return new ColumnDataFrame(concatColumns, toSeries(w, data));
    }

    private Series<?>[] toSeries(int w, Object[][] data) {
        Series[] series = new Series[w];

        for (int i = 0; i < w; i++) {
            series[i] = new ArraySeries(data[i]);
        }

        return series;
    }

    private int mapSeriesPosition(Index concatColumns, String dfColumn) {
        return concatColumns.hasLabel(dfColumn) ? concatColumns.position(dfColumn) : -1;
    }
}
