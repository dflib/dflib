package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.*;

@Deprecated
public class ColumnSet_SelectExpandTest {

    @Test
    public void list() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("c", "b").selectExpand($val(List.of("one", "two")));

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(2)
                .expectRow(0, "one", "two")
                .expectRow(1, "one", "two");
    }

    @Test
    public void list_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").selectExpand($int("a").mapVal(i -> {
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
                }));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(3)
                .expectRow(0, "one", null)
                .expectRow(1, "one", "two")
                .expectRow(2, "one", "two");
    }

    @Test
    public void list_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").selectExpand($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return List.of("one");
                        case 3:
                            return List.of("one", "two", "three");
                        default:
                            return null;
                    }
                }));

        new DataFrameAsserts(df, "b", "c")
                .expectHeight(3)
                .expectRow(0, "one", null)
                .expectRow(1, null, null)
                .expectRow(2, "one", "two");
    }

    @Test
    public void list_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols().selectExpand($int("a").mapVal(i -> {
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
                }));

        new DataFrameAsserts(df, "0", "1", "2")
                .expectHeight(3)
                .expectRow(0, "one", null, null)
                .expectRow(1, "one", "two", null)
                .expectRow(2, "one", "two", "three");
    }
}
