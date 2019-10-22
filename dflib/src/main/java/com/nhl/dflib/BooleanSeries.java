package com.nhl.dflib;

import com.nhl.dflib.op.BooleanSeriesOps;
import com.nhl.dflib.series.BooleanArraySeries;

import java.util.Random;

/**
 * @since 0.6
 */
public interface BooleanSeries extends Series<Boolean> {

    static BooleanSeries forBooleans(boolean... bools) {
        return new BooleanArraySeries(bools);
    }

    static BooleanSeries andAll(BooleanSeries... series) {
        return BooleanSeriesOps.andAll(series);
    }

    static BooleanSeries orAll(BooleanSeries... series) {
        return BooleanSeriesOps.orAll(series);
    }


    @Override
    default Class<Boolean> getType() {
        return Boolean.TYPE;
    }

    boolean getBoolean(int index);

    void copyToBoolean(boolean[] to, int fromOffset, int toOffset, int len);

    BooleanSeries materializeBoolean();

    BooleanSeries concatBoolean(BooleanSeries... other);

    BooleanSeries rangeOpenClosedBoolean(int fromInclusive, int toExclusive);

    BooleanSeries headBoolean(int len);

    BooleanSeries tailBoolean(int len);

    BooleanSeries filterBoolean(BooleanSeries positions);

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

    /**
     * @return a BooleanSeries that contains non-repeating values from this Series.
     */
    BooleanSeries uniqueBoolean();

    BooleanSeries and(BooleanSeries another);

    BooleanSeries or(BooleanSeries another);

    BooleanSeries not();

    /**
     * @since 0.7
     */
    @Override
    BooleanSeries sample(int size);

    /**
     * @since 0.7
     */
    @Override
    BooleanSeries sample(int size, Random random);

    /**
     * @since 0.7
     */
    default boolean[] toBooleanArray() {
        int len = size();
        boolean[] copy = new boolean[len];
        copyToBoolean(copy, 0, 0, len);
        return copy;
    }
}
