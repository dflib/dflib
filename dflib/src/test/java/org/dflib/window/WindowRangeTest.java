package org.dflib.window;

import org.dflib.DataFrame;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class WindowRangeTest {

    @Test
    public void alwaysInRange() {
        WindowRange range = WindowRange.of(2, 3);
        assertTrue(range.alwaysInRange(0));
        assertTrue(range.alwaysInRange(1));
        assertTrue(range.alwaysInRange(2));
        assertTrue(range.alwaysInRange(3));
        assertFalse(range.alwaysInRange(4));
    }

    @Test
    public void alwaysInRange_All() {
        assertTrue(WindowRange.all.alwaysInRange(0));
        assertTrue(WindowRange.all.alwaysInRange(1));
        assertTrue(WindowRange.all.alwaysInRange(2));
        assertTrue(WindowRange.all.alwaysInRange(10_000));
    }

    @Test
    public void alwaysInRange_AllPreceding() {
        assertTrue(WindowRange.allPreceding.alwaysInRange(0));
        assertTrue(WindowRange.allPreceding.alwaysInRange(1));
        assertFalse(WindowRange.allPreceding.alwaysInRange(2));
        assertFalse(WindowRange.allPreceding.alwaysInRange(10_000));
    }

    @Test
    public void alwaysInRange_AllFollowing() {
        assertTrue(WindowRange.allFollowing.alwaysInRange(0));
        assertTrue(WindowRange.allFollowing.alwaysInRange(1));
        assertFalse(WindowRange.allFollowing.alwaysInRange(2));
        assertFalse(WindowRange.allFollowing.alwaysInRange(10_000));
    }

    @Test
    public void selectRows() {

        WindowRange range = WindowRange.of(2, 2);

        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3, 4);

        new DataFrameAsserts(range.selectRows(df, 0), "a").expectHeight(3).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3);
        new DataFrameAsserts(range.selectRows(df, 1), "a").expectHeight(4).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3).expectRow(3, 4);
        new DataFrameAsserts(range.selectRows(df, 2), "a").expectHeight(4).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3).expectRow(3, 4);
        new DataFrameAsserts(range.selectRows(df, 3), "a").expectHeight(3).expectRow(0, 2).expectRow(1, 3).expectRow(2, 4);
    }

    @Test
    public void selectRows_AllPreceding() {

        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3, 4);

        new DataFrameAsserts(WindowRange.allPreceding.selectRows(df, 0), "a").expectHeight(1).expectRow(0, 1);
        new DataFrameAsserts(WindowRange.allPreceding.selectRows(df, 1), "a").expectHeight(2).expectRow(0, 1).expectRow(1, 2);
        new DataFrameAsserts(WindowRange.allPreceding.selectRows(df, 2), "a").expectHeight(3).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3);
        new DataFrameAsserts(WindowRange.allPreceding.selectRows(df, 3), "a").expectHeight(4).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3).expectRow(3, 4);
    }

    @Test
    public void selectRows_AllFollowing() {

        DataFrame df = DataFrame.foldByRow("a").of(1, 2, 3, 4);

        new DataFrameAsserts(WindowRange.allFollowing.selectRows(df, 3), "a").expectHeight(1).expectRow(0, 4);
        new DataFrameAsserts(WindowRange.allFollowing.selectRows(df, 2), "a").expectHeight(2).expectRow(0, 3).expectRow(1, 4);
        new DataFrameAsserts(WindowRange.allFollowing.selectRows(df, 1), "a").expectHeight(3).expectRow(0, 2).expectRow(1, 3).expectRow(2, 4);
        new DataFrameAsserts(WindowRange.allFollowing.selectRows(df, 0), "a").expectHeight(4).expectRow(0, 1).expectRow(1, 2).expectRow(2, 3).expectRow(3, 4);
    }
}
