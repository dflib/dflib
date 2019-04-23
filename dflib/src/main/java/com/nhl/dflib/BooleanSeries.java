package com.nhl.dflib;

import com.nhl.dflib.filter.ValuePredicate;
import com.nhl.dflib.series.BooleanArraySeries;
import com.nhl.dflib.series.IntArraySeries;

/**
 * @since 0.6
 */
public interface BooleanSeries extends Series<Boolean> {

    static BooleanSeries forBooleans(boolean... bools) {
        return new BooleanArraySeries(bools);
    }

    boolean getBoolean(int index);

    void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len);

    BooleanSeries materializeBoolean();

    BooleanSeries concatBoolean(BooleanSeries... other);

    BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive);

    BooleanSeries headBoolean(int len);

    BooleanSeries tailBoolean(int len);

    default BooleanSeries selectBoolean(int... positions) {
        return selectBoolean(new IntArraySeries(positions));
    }

    BooleanSeries selectBoolean(IntSeries positions);

    /**
     * Returns an IntSeries that represents positions in the Series that contain true values. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @return an IntSeries that represents positions in the Series that contain "true" values
     */
    IntSeries indexTrue();

    /**
     * Returns an IntSeries that represents positions in the Series that contain false values. The returned value can be
     * used to "select" data from this Series or from DataFrame containing this Series. Same as {@link #index(ValuePredicate)},
     * only usually much faster.
     *
     * @return an IntSeries that represents positions in the Series that contain "false" values
     */
    IntSeries indexFalse();

    /**
     * @return true if all the elements in the series are "true"
     */
    boolean isTrue();

    /**
     * @return true if all the elements in the series are "false"
     */
    boolean isFalse();
}
