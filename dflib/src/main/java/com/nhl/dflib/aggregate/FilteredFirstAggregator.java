package com.nhl.dflib.aggregate;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Series;
import com.nhl.dflib.Condition;
import com.nhl.dflib.Exp;

/**
 * @since 0.7
 * @deprecated since 0.11 replaced by the {@link com.nhl.dflib.Exp} based aggregation API
 */
@Deprecated
public class FilteredFirstAggregator<T> implements Exp<T> {

    private final Condition rowFilter;
    private final Exp<T> extractor;

    public FilteredFirstAggregator(
            Condition rowFilter,
            Exp<T> extractor) {

        this.rowFilter = rowFilter;
        this.extractor = extractor;
    }

    @Override
    public String toString() {
        return toQL();
    }

    @Override
    public Class<T> getType() {
        return extractor.getType();
    }

    @Override
    public String toQL() {
        return extractor.toQL();
    }

    @Override
    public String toQL(DataFrame df) {
        return extractor.toQL(df);
    }

    @Override
    public Series<T> eval(DataFrame df) {
        int index = rowFilter.firstMatch(df);
        return index < 0 ? null : extractor.eval(df.selectRows(index));
    }

    @Override
    public Series<T> eval(Series<?> s) {
        // do not expect to be called on this deprecated class
        throw new UnsupportedOperationException("Unsupported eval with Series... The class is deprecated, consider switching to Exp API");
    }
}
