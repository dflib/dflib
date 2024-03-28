package org.dflib;

import org.dflib.unit.IntSeriesAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class GroupBy_WindowFuncsTest {

    @Test
    public void rowNumbers_Emtpy() {
        GroupBy gb = DataFrame.empty("a", "b", "c").group("a");
        IntSeries rn = gb.rowNumber();
        new IntSeriesAsserts(rn).expectData();
    }

    @Test
    public void rowNumbers0() {
        GroupBy gb = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                1, "x", "s").group("a");

        IntSeries rn = gb.rowNumber();
        new IntSeriesAsserts(rn).expectData(1, 1, 2, 1, 3);
    }

    @Test
    public void rowNumbers1() {
        GroupBy gb = DataFrame.foldByRow("a", "b", "c").of(
                3, "x", "m",
                2, "y", "n",
                1, "z", "k",
                0, "a", "f",
                -1, "x", "s").group("a");

        IntSeries rn = gb.rowNumber();
        new IntSeriesAsserts(rn).expectData(1, 1, 1, 1, 1);
    }

    @Test
    public void rowNumbers2() {
        GroupBy gb =  DataFrame.foldByRow("a", "b", "c").of(
                3, "x", "m",
                0, "y", "n",
                3, "z", "k",
                3, "a", "f",
                1, "x", "s").group("a");

        IntSeries rn = gb.rowNumber();
        new IntSeriesAsserts(rn).expectData(1, 1, 2, 3, 1);
    }

    @Test
    public void rowNumbers_Sort() {
        GroupBy gb = DataFrame.foldByRow("a", "b", "c").of(
                3, "x", "m",
                0, "y", "n",
                3, "z", "k",
                3, "a", "f",
                1, "x", "s").group("a");

        IntSeries rn = gb.sort("b", true).rowNumber();
        new IntSeriesAsserts(rn).expectData(2, 1, 3, 1, 1);
    }
}
