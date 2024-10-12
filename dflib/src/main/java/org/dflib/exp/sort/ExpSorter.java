package org.dflib.exp.sort;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.dflib.Exp;
import org.dflib.Sorter;
import org.dflib.sort.Comparators;
import org.dflib.sort.IntComparator;

/**
 * A Sorter based on an expression that should evaluate to Comparable.
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

    @Override
    public IntComparator eval(Series<?> s) {
        Series<?> column = exp.eval(s);
        return Comparators.of(column, ascending);
    }
}
