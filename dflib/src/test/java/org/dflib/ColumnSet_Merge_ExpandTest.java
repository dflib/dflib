package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.*;

public class ColumnSet_Merge_ExpandTest {

    @Test
    public void all_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols().expand($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return List.of("one");
                        case 2:
                            return List.of("one", "two");
                        case 3:
                            return List.of("one", "two", "three");
                        default:
                            return null;
                    }
                })).merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")

                // columns spec is ignored since merge() is empty
                .cols("a", "3", "2")
                .expand($val(List.of("one", "two")))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two")
                .expectRow(1, 2, "y", "one", "two");
    }

    @Test
    public void cols_ql_listCol() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, List.of("x", "y"),
                        2, List.of("a", "b"))
                .cols("2", "3")
                .expand("b")
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3")
                .expectHeight(2)
                .expectRow(0, 1, List.of("x", "y"), "x", "y")
                .expectRow(1, 2, List.of("a", "b"), "a", "b");
    }

    @Test
    public void cols_Transform() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("a", "3")
                .expand($val(List.of("one", "two")))
                .merge(
                        $int("a").mul(2),
                        concat($str("3"), "->")
                );

        new DataFrameAsserts(df, "a", "b", "2", "3")
                .expectHeight(2)
                .expectRow(0, 2, "x", "one", "two->")
                .expectRow(1, 4, "y", "one", "two->");
    }

    @Test
    public void list_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expand($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return List.of("one");
                        case 2:
                            return List.of("one", "two");
                        case 3:
                            return List.of("one", "two", "three");
                        default:
                            return null;
                    }
                }))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }

    @Test
    public void list_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expand($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return List.of("one");
                        case 3:
                            return List.of("one", "two", "three");
                        default:
                            return null;
                    }
                }))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", null, null, null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }

    @Test
    public void nameUniqueness() {
        DataFrame df = DataFrame.foldByRow("a", "b", "4", "5")
                .of(
                        1, "x", "TWO", "THREE",
                        2, "y", "TWO", "THREE"
                )
                .cols("5", "5_")
                .expand($val(List.of("one", "two")))
                .merge(
                        concat($str("5"), ">"),
                        concat($str("5_"), "<")
                );

        new DataFrameAsserts(df, "a", "b", "4", "5", "4_", "5_")
                .expectHeight(2)
                .expectRow(0, 1, "x", "TWO", "THREE>", "one", "two<")
                .expectRow(1, 2, "y", "TWO", "THREE>", "one", "two<");
    }

    @Test
    public void multiExpand() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, "x",
                        2, "y"
                )
                .cols("3", "5")
                .expand($val(List.of("one", "two")))
                .expand($val(List.of("three", "four")))
                .merge(
                        concat($str("3"), ">"),
                        concat($str("5"), "<")
                );

        new DataFrameAsserts(df, "a", "b", "2", "3", "4", "5")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two>", "three", "four<")
                .expectRow(1, 2, "y", "one", "two>", "three", "four<");
    }
}
