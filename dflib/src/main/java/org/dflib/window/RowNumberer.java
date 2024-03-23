package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.concat.SeriesConcat;
import org.dflib.series.IntSequenceSeries;
import org.dflib.sort.IntComparator;
import org.dflib.sort.DataFrameSorter;

import java.util.Collection;

/**
 * @since 0.8
 */
public class RowNumberer {

    static final int START_NUMBER = 1;

    public static IntSeries sequence(int size) {
        return new IntSequenceSeries(START_NUMBER, START_NUMBER + size);
    }

    public static void fillSequence(int[] buffer, int offset, int size) {
        for (int i = 0; i < size; i++) {
            buffer[i + offset] = START_NUMBER + i;
        }
    }

    /**
     * @since 0.11
     */
    public static IntSeries rowNumber(DataFrame dataFrame, IntComparator sorter) {

        // note how we are calling "sortIndex(sortIndex(..))"
        // 1. the first call produces a Series where Series positions correspond to the new positions of the old rows
        //   in the sorted Series, while values are old positions before sorting.
        // 2. the second call inverts this mapping, producing a Series where Series positions correspond to the original
        //   value positions, and values are their positions in the imaginary sorted data set.

        // (1) is good for producing a sorted Series or DataFrame, (2) is good for producing row numbers that follow
        // the sorting.. We have case (2) here.

        IntSeries rowPositions = DataFrameSorter.sort(sorter, dataFrame.height()).sortIndexInt();

        // since we control select indices, and don't expect negative values, we can safely cast to IntSeries
        return (IntSeries) RowNumberer.sequence(dataFrame.height()).select(rowPositions);
    }

    public static IntSeries rowNumber(DataFrame dataFrame, Collection<IntSeries> partitionsIndex) {

        int[] rowNumbers = new int[dataFrame.height()];

        int offset = 0;
        for (IntSeries s : partitionsIndex) {
            int len = s.size();
            RowNumberer.fillSequence(rowNumbers, offset, len);
            offset += len;
        }

        IntSeries groupsIndexGlued = SeriesConcat.intConcat(partitionsIndex);

        // since we control select indices, and don't expect negative values, we can safely cast to IntSeries
        return (IntSeries) Series.ofInt(rowNumbers).select(groupsIndexGlued.sortIndexInt());
    }
}
