package com.nhl.dflib.join;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.JoinType;

import java.util.Objects;

/**
 * @since 0.6
 */
public abstract class BaseJoiner {

    private final JoinType semantics;

    public BaseJoiner(JoinType semantics) {
        this.semantics = Objects.requireNonNull(semantics);
    }

    /**
     * @since 1.0.0-M19
     */
    public IntSeries[] rowSelectors(DataFrame lf, DataFrame rf) {

        switch (semantics) {
            case inner:
                return innerJoin(lf, rf);
            case left:
                return leftJoin(lf, rf);
            case right:
                return rightJoin(lf, rf);
            case full:
                return fullJoin(lf, rf);
            default:
                throw new IllegalStateException("Unsupported join semantics: " + semantics);
        }
    }

    protected abstract IntSeries[] innerJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] leftJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] rightJoin(DataFrame lf, DataFrame rf);

    protected abstract IntSeries[] fullJoin(DataFrame lf, DataFrame rf);
}
