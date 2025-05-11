package org.dflib;

import org.dflib.series.ObjectSeries;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertInstanceOf;


public class DataFrame_InsertRowTest {

    @Test
    public void insertRow() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df1 = df.insertRow(0, Map.of("a", 3, "b", "z"));
        new DataFrameAsserts(df1, "a", "b")
                .expectHeight(3)
                .expectRow(0, 3, "z")
                .expectRow(1, 1, "x")
                .expectRow(2, 2, "y");

        DataFrame df2 = df1.insertRow(2, Map.of("a", 4, "b", "Z"));
        new DataFrameAsserts(df2, "a", "b")
                .expectHeight(4)
                .expectRow(0, 3, "z")
                .expectRow(1, 1, "x")
                .expectRow(2, 4, "Z")
                .expectRow(3, 2, "y");

    }

    @Test
    public void insertRowMissingOrExtraValues() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df1 = df.insertRow(2, Map.of("c", 3, "b", "z"));
        new DataFrameAsserts(df1, "a", "b")
                .expectHeight(3)
                .expectRow(0, 1, "x")
                .expectRow(1, 2, "y")
                .expectRow(2, null, "z");
    }

    @Test
    public void insertRow_PrimitiveColumns() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                Series.ofLong(5L, 6L),
                Series.ofInt(1, 2))
                .insertRow(2, Map.of("a", 3L, "b", "str"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 5L, 1)
                .expectRow(1, 6L, 2)
                .expectRow(2, 3L, "str");

        assertInstanceOf(LongSeries.class, df.getColumn("a").unsafeCastAs(Long.class));
        assertInstanceOf(ObjectSeries.class, df.getColumn("b"));
    }
}
