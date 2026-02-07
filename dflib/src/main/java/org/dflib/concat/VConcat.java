package org.dflib.concat;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.JoinType;
import org.dflib.Series;
import org.dflib.union.SeriesUnion;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

public class VConcat {
    private static final EnumMap<JoinType, VConcat> JOINERS = new EnumMap<>(JoinType.class);

    static {
        for (JoinType s : JoinType.values()) {
            Function<Index[], Index> joiner = switch (s) {
                case inner -> VConcat::innerJoin;
                case left -> VConcat::leftJoin;
                case right -> VConcat::rightJoin;
                case full -> VConcat::fullJoin;
            };

            JOINERS.put(s, new VConcat(joiner));
        }
    }

    private final Function<Index[], Index> zipper;

    protected VConcat(Function<Index[], Index> zipper) {
        this.zipper = zipper;
    }

    public static DataFrame concat(JoinType how, DataFrame... dfs) {

        return switch (dfs.length) {
            case 0 -> DataFrame.empty();
            case 1 -> dfs[0];
            default -> getInstance(how).concat(dfs);
        };
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

        return Index.of(columns.toArray(new String[0]));
    }

    private static void innerJoin(Set<String> columns, Index index) {
        columns.removeIf(c -> !index.contains(c));
    }

    private static Index fullJoin(Index[] indices) {
        Set<String> columns = new LinkedHashSet<>();

        for (Index i : indices) {
            for (String label : i) {
                columns.add(label);
            }
        }

        return Index.of(columns.toArray(new String[0]));
    }

    private static Index leftJoin(Index[] indices) {
        return indices[0];
    }

    private static Index rightJoin(Index[] indices) {
        return indices[indices.length - 1];
    }

    public DataFrame concat(DataFrame... concat) {

        int clen = concat.length;
        if (clen == 0) {
            return DataFrame.empty();
        } else if (clen == 1) {
            return concat[0];
        }

        Index[] indices = new Index[clen];
        for (int i = 0; i < clen; i++) {
            indices[i] = concat[i].getColumnsIndex();
        }

        Index concatIndex = zipper.apply(indices);
        int w = concatIndex.size();
        Series[] concatCols = new Series[w];

        for (int i = 0; i < w; i++) {

            String col = concatIndex.get(i);
            Series[] cols = new Series[clen];

            for (int j = 0; j < clen; j++) {
                cols[j] = concatColumn(concat[j], col);
            }

            concatCols[i] = SeriesUnion.of(cols);
        }

        return new ColumnDataFrame(null, concatIndex, concatCols);
    }

    private Series<?> concatColumn(DataFrame df, String col) {
        return df.getColumnsIndex().contains(col)
                ? df.getColumn(col)
                : Series.ofVal(null, df.height());
    }
}
