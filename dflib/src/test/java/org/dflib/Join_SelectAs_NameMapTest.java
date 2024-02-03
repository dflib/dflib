package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.Map;

public class Join_SelectAs_NameMapTest {

    @Test
    public void noOverlap_NoAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(0, 1)
                .selectAs(Map.of("a", "A"));

        new DataFrameAsserts(df, "A", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 4, "z", "x", 4);
    }

    @Test
    public void overlap_NoAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.join(df2)
                .on(0, 1)
                .selectAs(Map.of("a", "A"));

        new DataFrameAsserts(df, "A", "b", "c", "a_", "b_", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", "Y", "a", 2, 20)
                .expectRow(1, 4, "z", "Z", "x", 4, 40);
    }

    @Test
    public void overlap_BothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.as("df1").join(df2.as("df2"))
                .on(0, 1)
                .selectAs(Map.of(
                        "b", "B", // this one should have no effect
                        "df1.a", "DF1A",
                        "df2.d", "DF2D"));

        new DataFrameAsserts(df, "DF1A", "df1.b", "df1.c", "df2.a", "df2.b", "DF2D")
                .expectHeight(2)
                .expectRow(0, 2, "y", "Y", "a", 2, 20)
                .expectRow(1, 4, "z", "Z", "x", 4, 40);
    }

    @Test
    public void indicator() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "X",
                2, "y", "Y",
                4, "z", "Z");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.fullJoin(df2)
                .on(0, 1)
                .indicatorColumn("ind")
                .selectAs(Map.of(
                        "b", "B",
                        "a_", "AA",
                        "ind", "IND"));

        new DataFrameAsserts(df, "a", "B", "c", "AA", "b_", "d", "IND")
                .expectHeight(4)
                .expectRow(0, 1, "x", "X", null, null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", "Y", "a", 2, 20, JoinIndicator.both)
                .expectRow(2, 4, "z", "Z", "x", 4, 40, JoinIndicator.both)
                .expectRow(3, null, null, null, "c", 3, 30, JoinIndicator.right_only);
    }
}
