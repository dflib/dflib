package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$val;

public class ColumnSet_Select_ExpandTest {

    @Test
    public void cols_StrSplitExp_Array() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, "x/y",
                        2, "a/b")
                .cols("a", "3", "2")
                .expand("split(str(b), '/')")
                .select();

        new DataFrameAsserts(df, "a", "3", "2")
                .expectHeight(2)
                .expectRow(0, 1, "y", "x")
                .expectRow(1, 2, "b", "a");
    }

    @Test
    public void cols_StrSplitExp_Val() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(
                        1, "x/y",
                        2, "a/b")
                .cols("a", "2")
                .expand("'A'")
                .select();

        new DataFrameAsserts(df, "a", "2")
                .expectHeight(2)
                .expectRow(0, 1, "A")
                .expectRow(1, 2, "A");
    }

    @Test
    public void fixedSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("a", "3", "2").expand($val(List.of("one", "two"))).select();

        new DataFrameAsserts(df, "a", "3", "2")
                .expectHeight(2)
                .expectRow(0, 1, "two", "one")
                .expectRow(1, 2, "two", "one");
    }

    @Test
    public void list_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3").expand($int("a").mapVal(i -> {
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
                })).select();

        new DataFrameAsserts(df, "a", "2", "3")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, "one", "two")
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void list_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3").expand($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return List.of("one");
                        case 3:
                            return List.of("one", "two", "three");
                        default:
                            return null;
                    }
                })).select();

        new DataFrameAsserts(df, "a", "2", "3")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, null, null)
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void list_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols()
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
                .select();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }
}
