package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$val;

public class ColumnSet_SelectArraysTest {

    @Test
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("c", "b").selectArrays($val(new String[]{"one", "two"}));

        new DataFrameAsserts(df, "c", "b")
                .expectHeight(2)
                .expectRow(0, "one", "two")
                .expectRow(1, "one", "two");
    }

    @Test
    public void array_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").selectArrays($int("a").mapVal(i -> {

                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 2:
                            return new String[]{"one", "two"};
                        case 3:
                            return new String[]{"one", "two", "three"};
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
    public void array_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").selectArrays($int("a").mapVal(i -> {

                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 3:
                            return new String[]{"one", "two", "three"};
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
    public void array_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols().selectArrays($int("a").mapVal(i -> {
                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 2:
                            return new String[]{"one", "two"};
                        case 3:
                            return new String[]{"one", "two", "three"};
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
