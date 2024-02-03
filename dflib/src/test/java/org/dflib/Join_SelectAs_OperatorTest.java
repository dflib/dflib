package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class Join_SelectAs_OperatorTest {

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

        DataFrame df = df1.join(df2)
                .on(0, 1)
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C", "D")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C", "A_", "B_", "D")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "DF1.A", "DF1.B", "DF1.C", "DF2.A", "DF2.B", "DF2.D")
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
                .selectAs(String::toUpperCase);

        new DataFrameAsserts(df, "A", "B", "C", "A_", "B_", "D", "IND")
                .expectHeight(4)
                .expectRow(0, 1, "x", "X", null, null, null, JoinIndicator.left_only)
                .expectRow(1, 2, "y", "Y", "a", 2, 20, JoinIndicator.both)
                .expectRow(2, 4, "z", "Z", "x", 4, 40, JoinIndicator.both)
                .expectRow(3, null, null, null, "c", 3, 30, JoinIndicator.right_only);
    }
}
