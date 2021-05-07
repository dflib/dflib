package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.SeriesCondition;
import com.nhl.dflib.SeriesExp;

/**
 * @since 0.7
 */
public class FilteredFirstAggregator<T> implements SeriesExp<T> {

    private final SeriesCondition rowFilter;
    private final SeriesExp<T> aggregator;

    public FilteredFirstAggregator(
            SeriesCondition rowFilter,
            SeriesExp<T> aggregator) {

        this.rowFilter = rowFilter;
        this.aggregator = aggregator;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        int index = rowFilter.firstMatch(df);
        return index < 0 ? null : aggregator.eval(df.selectRows(index));
    }

    @Override
    public String getName(DataFrame df) {
        return aggregator.getName(df);
    }

    @Override
    public Class<T> getType() {
        return aggregator.getType();
    }
}
