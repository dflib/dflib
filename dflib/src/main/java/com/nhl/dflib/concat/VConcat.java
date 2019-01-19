package com.nhl.dflib.concat;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IndexPosition;
import com.nhl.dflib.join.JoinType;

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
                return DataFrame.fromSequence(Index.withNames());
            case 1:
                return dfs[0];
            default:
                Index[] indices = new Index[dfs.length];
                for (int i = 0; i < indices.length; i++) {
                    indices[i] = dfs[i].getColumns();
                }

                Index combinedColumns = getInstance(how).zipIndex(indices);
                return new VConcatDataFrame(combinedColumns, dfs);
        }
    }

    public static VConcat getInstance(JoinType how) {
        return JOINERS.get(Objects.requireNonNull(how, "Null 'how' (join semantics)"));
    }

    private static Index innerJoin(Index[] indices) {

        Set<String> columns = new LinkedHashSet<>();

        for (IndexPosition p : indices[0]) {
            columns.add(p.name());
        }

        for (int i = 1; i < indices.length; i++) {
            innerJoin(columns, indices[i]);
        }

        return Index.withNames(columns.toArray(new String[columns.size()]));
    }

    private static void innerJoin(Set<String> columns, Index index) {

        Iterator<String> it = columns.iterator();
        while (it.hasNext()) {
            String c = it.next();
            if (!index.hasName(c)) {
                it.remove();
            }
        }
    }

    private static Index fullJoin(Index[] indices) {
        Set<String> columns = new LinkedHashSet<>();

        for (Index i : indices) {
            for (IndexPosition p : i) {
                columns.add(p.name());
            }
        }

        return Index.withNames(columns.toArray(new String[columns.size()]));
    }

    private static Index leftJoin(Index[] indices) {
        return indices[0];
    }

    private static Index rightJoin(Index[] indices) {
        return indices[indices.length - 1];
    }

    public Index zipIndex(Index... indices) {
        return zipper.apply(indices);
    }
}
