package com.nhl.dflib.aggregate;

import com.nhl.dflib.*;

/**
 * @since 0.7
 * @deprecated since 0.11 replaced by the {@link com.nhl.dflib.Exp} based aggregation API
 */
@Deprecated
public class FilteredAggregator<T> implements SeriesExp<T> {

    private SeriesCondition rowFilter;
    private SeriesExp<T> aggregator;

    public FilteredAggregator(SeriesCondition rowFilter, SeriesExp<T> aggregator) {
        this.rowFilter = rowFilter;
        this.aggregator = aggregator;
    }

    @Override
    public String getName(DataFrame df) {
        return aggregator.getName(df);
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
