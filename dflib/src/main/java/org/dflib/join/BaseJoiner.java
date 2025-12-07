package org.dflib.join;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.JoinType;

import java.util.Objects;

public abstract class BaseJoiner {

    private final JoinType semantics;

    public BaseJoiner(JoinType semantics) {
        this.semantics = Objects.requireNonNull(semantics);
    }


    public IntSeries[] rowSelectors(DataFrame lf, DataFrame rf) {

        return switch (semantics) {
            case inner -> innerJoin(lf, rf);
            case left -> leftJoin(lf, rf);
            case right -> rightJoin(lf, rf);
            case full -> fullJoin(lf, rf);
        };
    }

    protected abstract IntSeries[] innerJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] leftJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] rightJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] fullJoin(DataFrame lf, DataFrame rf);
}
