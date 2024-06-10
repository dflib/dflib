package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.dflib.window.WindowRange;
import org.junit.jupiter.api.Test;
import static org.dflib.Exp.*;

@Deprecated
public class Window_MapColumnTest {

    @Test
    public void empty() {
        DataFrame df = DataFrame.empty("a", "b", "c");
        Series<?> r = df.over().mapColumn($int("a").sum());
        new SeriesAsserts(r).expectData();
    }

    @Test
    public void test() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().mapColumn($int("a").sum());
        new SeriesAsserts(r).expectData(5, 5, 5, 5, 5);
    }

    @Test
    public void partitioned() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().partitioned("a").mapColumn($int("a").sum());
        new SeriesAsserts(r).expectData(3, 2, 3, 0, 3);
    }

    @Test
    public void sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().sorted($col("b").asc()).mapColumn($int("a").sum());
        new SeriesAsserts(r).expectData(5, 5, 5, 5, 5);
    }

    @Test
    public void range_Preceding() {
        DataFrame df = DataFrame.foldByRow("value").of(
                1,
                22,
                15,
                2);

        Series<?> r = df.over()
                .range(WindowRange.allPreceding)
                .mapColumn($int("value").sum());

        new SeriesAsserts(r).expectData(1, 23, 38, 40);
    }

    @Test
    public void sorted_Range_Preceding() {
        DataFrame df = DataFrame.foldByRow("order", "value").of(
                2, 1,
                4, 22,
                1, 15,
                3, 2);

        Series<?> r = df.over()
                .sorted($col("order").asc())
                .range(WindowRange.allPreceding)
                .mapColumn($int("value").sum());

        new SeriesAsserts(r).expectData(15, 16, 18, 40);
    }

    @Test
    public void sorted_Range_Following() {
        DataFrame df = DataFrame.foldByRow("order", "value").of(
                2, 1,
                4, 22,
                1, 15,
                3, 2);

        Series<?> r = df.over()
                .sorted($col("order").asc())
                .range(WindowRange.allFollowing)
                .mapColumn($int("value").sum());

        new SeriesAsserts(r).expectData(40, 25, 24, 22);
    }

    @Test
    public void partitioned_Sorted() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                1, "z",
                0, "a",
                1, "x");

        Series<?> r = df.over().partitioned("a").sorted($col("b").asc()).mapColumn($int("a").sum());
        new SeriesAsserts(r).expectData(3, 2, 3, 0, 3);
    }

    @Test
    public void partitioned_Sorted_Range_Preceding() {
        DataFrame df = DataFrame.foldByRow("label", "order", "value").of(
                "a", 2, 1,
                "a", 3, 22,
                "b", 1, 15,
                "a", 1, 2,
                "c", 1, 77,
                "b", 2, 12);

        Series<?> r = df.over()
                .partitioned("label")
                .sorted($col("order").asc())
                .range(WindowRange.allPreceding)
                .mapColumn($int("value").sum());

        new SeriesAsserts(r).expectData(3, 25, 15, 2, 77, 27);
    }

}
