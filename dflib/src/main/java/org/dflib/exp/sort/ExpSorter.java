package org.dflib.exp.sort;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.Series;
import org.dflib.Sorter;
import org.dflib.sort.IntComparator;

import java.util.Objects;

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
        return IntComparator.of(column, ascending);
    }

    @Override
    public IntComparator eval(Series<?> s) {
        Series<?> column = exp.eval(s);
        return IntComparator.of(column, ascending);
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        ExpSorter expSorter = (ExpSorter) o;
        return ascending == expSorter.ascending && exp.equals(expSorter.exp);
    }

    @Override
    public int hashCode() {
        int result = exp.hashCode();
        result = 31 * result + Boolean.hashCode(ascending);
        return result;
    }
}
