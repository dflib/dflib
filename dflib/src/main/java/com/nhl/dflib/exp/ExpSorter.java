package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Exp;
import com.nhl.dflib.Sorter;
import com.nhl.dflib.sort.Comparators;
import com.nhl.dflib.sort.IntComparator;

/**
 * A Sorter based on an expression that should evaluate to Comparable.
 *
 * @since 0.11
 */
public class ExpSorter implements Sorter {

    private final Exp<?> exp;
    private final boolean ascending;

    public ExpSorter(Exp<?> exp, boolean ascending) {
        this.exp = exp;
        this.ascending = ascending;
    }

    @Override
    public IntComparator eval(DataFrame df) {
        Series<?> column = exp.eval(df);
        return Comparators.of(column, ascending);
    }
}
