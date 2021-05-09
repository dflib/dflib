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
    private final SeriesExp<T> extractor;

    public FilteredFirstAggregator(
            SeriesCondition rowFilter,
            SeriesExp<T> extractor) {

        this.rowFilter = rowFilter;
        this.extractor = extractor;
    }

    @Override
    public Series<T> eval(DataFrame df) {
        int index = rowFilter.firstMatch(df);
        return index < 0 ? null : extractor.eval(df.selectRows(index));
    }

    @Override
    public String getName(DataFrame df) {
        return extractor.getName(df);
    }

    @Override
    public Class<T> getType() {
        return extractor.getType();
    }
}
