package org.dflib;

import org.dflib.join.JoinIndicator;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class Join_SelectTest {

    @Test
    public void cols_Implicit() {

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
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 4, "z", "x", 4);
    }

    @Test
    public void cols_ByName_All() {

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
                .cols("a", "b", "c", "d")
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(2)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 4, "z", "x", 4);
    }

    @Test
    public void cols_ByName_Some() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(0, 1)
                .cols("a", "c")
                .select();

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b")
                .expectRow(2, 4, "x");
    }

    @Disabled
    @Test
    public void cols_ByName_SomeNew() {

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
                .cols("a", "b", "X")
                .select();

        new DataFrameAsserts(df, "a", "b", "X")
                .expectHeight(2)
                .expectRow(0, 2, "y", null)
                .expectRow(1, 4, "z", null);
    }

    @Test
    public void cols_ByPredicate() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(0, 1)
                .cols(c -> !c.endsWith("_"))
                .select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, 2, "y")
                .expectRow(1, 2, "y")
                .expectRow(2, 4, "z");
    }

    @Test
    public void colsExcept_ByPredicate() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                "a", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(0, 1)
                .colsExcept(c -> c.endsWith("_"))
                .select();

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 2, "y")
                .expectRow(1, 4, "z");
    }

    @Test
    public void colsExcept_ByPredicate_BothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "n",
                2, "y", "o",
                4, "z", "q");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "c").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        Set<String> except = Set.of("df1.a", "a_");

        DataFrame df = df1.as("df1").join(df2.as("df2"))
                .on(0, 1)
                .colsExcept(s -> except.contains(s))
                .select();

        new DataFrameAsserts(df, "df1.b", "df1.c", "df2.b", "df2.c")
                .expectHeight(2)
                .expectRow(0, "y", "o", 2, 20)
                .expectRow(1, "z", "q", 4, 40);
    }

    @Test
    public void colsExcept_ByName() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "n",
                2, "y", "o",
                4, "z", "q");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "c").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.join(df2)
                .on(0, 1)
                .colsExcept("a", "c", "b_")
                .select();

        new DataFrameAsserts(df, "b", "a_", "c_")
                .expectHeight(2)
                .expectRow(0, "y", "a", 20)
                .expectRow(1, "z", "x", 40);
    }

    @Test
    public void cols_ByName_RightAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "d").of(
                2, "a", "A",
                2, "b", "B",
                3, "c", "C");

        DataFrame df = df1.innerJoin(df2.as("df2"))
                .on(0)
                // select all possible valid aliases
                .cols("df2.a", "df2.b", "a", "a_", "b", "b_", "d")
                .select();

        new DataFrameAsserts(df, "df2.a", "df2.b", "a", "a_", "b", "b_", "d")
                .expectHeight(2)
                .expectRow(0, 2, "a", 2, 2, "y", "a", "A")
                .expectRow(1, 2, "b", 2, 2, "y", "b", "B");
    }

    @Test
    public void cols_ByName_LeftAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").join(df2)
                .on(0)
                // select all possible valid aliases
                .cols("df1.a", "df1.b", "a", "a_", "b", "b_")
                .select();

        new DataFrameAsserts(df, "df1.a", "df1.b", "a", "a_", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, 2, "y", "a")
                .expectRow(1, 2, "y", 2, 2, "y", "b");
    }

    @Test
    public void cols_ByName_BothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").join(df2.as("df2"))
                .on(0)
                // select all possible valid aliases
                .cols("df1.a", "df1.b", "df2.a", "df2.b", "a", "a_", "b", "b_")
                .select();

        new DataFrameAsserts(df, "df1.a", "df1.b", "df2.a", "df2.b", "a", "a_", "b", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "y", 2, "a", 2, 2, "y", "a")
                .expectRow(1, 2, "y", 2, "b", 2, 2, "y", "b");
    }

    @Test
    public void colsExcept_ByName_BothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b", "c").of(
                1, "x", "n",
                2, "y", "o",
                4, "z", "q");

        DataFrame df2 = DataFrame.foldByRow("a", "b", "c").of(
                "a", 2, 20,
                "x", 4, 40,
                "c", 3, 30);

        DataFrame df = df1.as("df1").join(df2.as("df2"))
                .on(0, 1)
                .colsExcept("df1.a", "df2.a", "b", "b_")
                .select();

        new DataFrameAsserts(df, "df1.c", "df2.c")
                .expectHeight(2)
                .expectRow(0, "o", 20)
                .expectRow(1, "q", 40);
    }

    @Test
    public void cols_ByName_AutoNaming() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2)
                .on(0)
                .cols("a", "b_")
                .select();

        new DataFrameAsserts(df, "a", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void cols_ByName_InvalidNames() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        assertThrows(IllegalArgumentException.class, () -> df1.innerJoin(df2.as("df2"))
                .on(0)
                .cols("a", "df2.c", "X")
                .select());
    }


    @Test
    public void cols_ByName_Indicator() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin(df2)
                .on(0)
                .indicatorColumn("ind")
                .cols("a", "ind", "d")
                .select();

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(4)
                .expectRow(0, 1, JoinIndicator.left_only, null)
                .expectRow(1, 2, JoinIndicator.both, "a")
                .expectRow(2, 2, JoinIndicator.both, "b")
                .expectRow(3, null, JoinIndicator.right_only, "c");
    }

    @Test
    public void cols_ByPos_All() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(Hasher.of(0), Hasher.of(1))
                .cols(0, 1, 2, 3)
                .select();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectHeight(3)
                .expectRow(0, 2, "y", "a", 2)
                .expectRow(1, 2, "y", "b", 2)
                .expectRow(2, 4, "z", "x", 4);
    }

    @Test
    public void cols_ByPos_Some() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                4, "z");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                "a", 2,
                "b", 2,
                "x", 4,
                "c", 3);

        DataFrame df = df1.innerJoin(df2)
                .on(Hasher.of(0), Hasher.of(1))
                .cols(0, 2)
                .select();

        new DataFrameAsserts(df, "a", "c")
                .expectHeight(3)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b")
                .expectRow(2, 4, "x");
    }


    @Test
    public void cols_ByPos_RightAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.innerJoin(df2.as("df2"))
                .on(0)
                .cols(0, 3)
                .select();

        new DataFrameAsserts(df, "a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void cols_ByPos_LeftAlias() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").innerJoin(df2)
                .on(0)
                .cols(0, 3)
                .select();

        new DataFrameAsserts(df, "df1.a", "b_")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void cols_ByPos_BothAliases() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("a", "b").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.as("df1").innerJoin(df2.as("df2"))
                .on(0)
                .cols(0, 3)
                .select();

        new DataFrameAsserts(df, "df1.a", "df2.b")
                .expectHeight(2)
                .expectRow(0, 2, "a")
                .expectRow(1, 2, "b");
    }

    @Test
    public void colsExcept_ByPos() {

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
                .colsExcept(0, 2)
                .select();

        new DataFrameAsserts(df, "b", "d")
                .expectHeight(2)
                .expectRow(0, "y", 2)
                .expectRow(1, "z", 4);
    }

    @Test
    public void cols_ByPos_Indicator() {

        DataFrame df1 = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y");

        DataFrame df2 = DataFrame.foldByRow("c", "d").of(
                2, "a",
                2, "b",
                3, "c");

        DataFrame df = df1.fullJoin(df2)
                .on(0)
                .indicatorColumn("ind")
                .cols(0, 4, 3)
                .select();

        new DataFrameAsserts(df, "a", "ind", "d")
                .expectHeight(4)
                .expectRow(0, 1, JoinIndicator.left_only, null)
                .expectRow(1, 2, JoinIndicator.both, "a")
                .expectRow(2, 2, JoinIndicator.both, "b")
                .expectRow(3, null, JoinIndicator.right_only, "c");
    }
}
