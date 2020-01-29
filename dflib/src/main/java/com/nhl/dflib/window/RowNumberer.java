package com.nhl.dflib.window;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.row.RowProxy;
import com.nhl.dflib.series.IntSequenceSeries;
import com.nhl.dflib.sort.IndexSorter;

import java.util.Comparator;
import java.util.Objects;

/**
 * @since 0.8
 */
public class RowNumberer {

    static final int START_NUMBER = 1;

    private Comparator<RowProxy> sorter;

    public RowNumberer(Comparator<RowProxy> sorter) {
        this.sorter = Objects.requireNonNull(sorter);
    }

    public static IntSeries sequence(int size) {
        return new IntSequenceSeries(START_NUMBER, START_NUMBER + size);
    }

    public static void fillSequence(int[] buffer, int offset, int size) {
        for(int i = 0; i < size; i++) {
            buffer[i + offset] = START_NUMBER + i;
        }
    }

    public IntSeries rowNumber(DataFrame dataFrame) {

        // note how we are calling "sortIndex(sortIndex(..))"
        // 1. the first call produces a Series where Series positions correspond to the new positions of the old rows
        //   in the sorted Series, while values are old positions before sorting.
        // 2. the second call inverts this mapping, producing a Series where Series positions correspond to the original
        //   value positions, and values are their positions in the imaginary sorted data set.

        // (1) is good for producing a sorted Series or DataFrame, (2) is good for producing row numbers that follow
        // the sorting.. We have case (2) here.

        IntSeries rowPositions = new IndexSorter(dataFrame).sortIndex(sorter).sortIndexInt();

        // since we control select indices, and don't expect negative values, we can safely cast to IntSeries
        return (IntSeries) RowNumberer.sequence(dataFrame.height()).select(rowPositions);
    }
}
