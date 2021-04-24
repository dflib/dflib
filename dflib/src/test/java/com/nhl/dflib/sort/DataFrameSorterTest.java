package com.nhl.dflib.sort;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

public class DataFrameSorterTest {

    @Test
    public void testSortIndex() {
        DataFrame df = DataFrame.newFrame("a", "b").foldByRow(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        IntSeries sortIndex = new DataFrameSorter(df).sortedPositions(Comparators.of(df.getColumn(1), true));
        new IntSeriesAsserts(sortIndex).expectData(3, 0, 4, 1, 2);
    }
}
