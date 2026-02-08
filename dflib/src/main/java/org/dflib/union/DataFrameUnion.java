package org.dflib.union;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.JoinType;
import org.dflib.Series;

import java.util.EnumMap;
import java.util.LinkedHashSet;
import java.util.Objects;
import java.util.Set;
import java.util.function.Function;

/**
 * @since 2.0.0
 */
public class DataFrameUnion {
    private static final EnumMap<JoinType, DataFrameUnion> JOINERS = new EnumMap<>(JoinType.class);

    static {
        for (JoinType s : JoinType.values()) {
            Function<Index[], Index> joiner = switch (s) {
                case inner -> DataFrameUnion::innerJoin;
                case left -> DataFrameUnion::leftJoin;
                case right -> DataFrameUnion::rightJoin;
                case full -> DataFrameUnion::fullJoin;
            };

            JOINERS.put(s, new DataFrameUnion(joiner));
        }
    }

    public static DataFrameUnion of(JoinType semantics) {
        return JOINERS.get(Objects.requireNonNull(semantics, "Null 'how' (join semantics)"));
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

    private final Function<Index[], Index> zipper;

    protected DataFrameUnion(Function<Index[], Index> zipper) {
        this.zipper = zipper;
    }

    public DataFrame union(DataFrame... dfs) {

        return switch (dfs.length) {
            case 0 -> DataFrame.empty();
            case 1 -> dfs[0];
            default -> {
                int len = dfs.length;
                Index[] indices = new Index[len];
                for (int i = 0; i < len; i++) {
                    indices[i] = dfs[i].getColumnsIndex();
                }

                Index concatIndex = zipper.apply(indices);
                int w = concatIndex.size();
                Series<?>[] concatCols = new Series[w];

                for (int i = 0; i < w; i++) {

                    String col = concatIndex.get(i);
                    Series<?>[] cols = new Series[len];

                    for (int j = 0; j < len; j++) {
                        cols[j] = unionColumn(dfs[j], col);
                    }

                    concatCols[i] = SeriesUnion.of(cols);
                }

                yield new ColumnDataFrame(null, concatIndex, concatCols);
            }
        };
    }

    private Series<?> unionColumn(DataFrame df, String col) {
        return df.getColumnsIndex().contains(col)
                ? df.getColumn(col)
                : Series.ofVal(null, df.height());
    }
}
