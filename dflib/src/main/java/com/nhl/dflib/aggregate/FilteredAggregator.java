package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * @since 0.7
 * @deprecated since 0.11 replaced by the {@link com.nhl.dflib.Exp} based aggregation API
 */
@Deprecated
public class FilteredAggregator<T> implements Exp<T> {

    private Condition rowFilter;
    private Exp<T> aggregator;

    public FilteredAggregator(Condition rowFilter, Exp<T> aggregator) {
        this.rowFilter = rowFilter;
        this.aggregator = aggregator;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public String toQL() {
        return aggregator.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return aggregator.toQL(df);
    }

    @Override
    public Class<T> getType() {
        return aggregator.getType();
    }

    @Override
    public Series<T> eval(DataFrame df) {
        // TODO: we can probably gain significant performance improvements by not creating an intermediate filtered
        //  DataFrame (as most aggregators will only work with a single column)

        return aggregator.eval(df.selectRows(rowFilter));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        // TODO: we can probably gain significant performance improvements by not creating an intermediate filtered
        //  DataFrame (as most aggregators will only work with a single column)

        return aggregator.eval(s.select(rowFilter));
    }
}
