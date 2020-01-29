package com.nhl.dflib.window;

import com.nhl.dflib.IntSeries;
import com.nhl.dflib.series.IntSequenceSeries;

/**
 * @since 0.8
 */
public class RowNumber {

    private static final int START_NUMBER = 1;

    public static IntSeries getNumbers(int size) {
        return new IntSequenceSeries(START_NUMBER, START_NUMBER + size);
    }
}
