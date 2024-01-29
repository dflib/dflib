package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$val;

public class ColumnSet_ExplodeArrayTest {

    @Test
    public void array() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("c", "b").explodeArray($val(new String[]{"one", "two"}));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, "two", "one")
                .expectRow(1, 2, "two", "one");
    }

    @Test
    public void array_VaryingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").explodeArray($int("a").mapVal(i -> {

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

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, "one", "two")
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void array_WithNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("b", "c").explodeArray($int("a").mapVal(i -> {

                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 3:
                            return new String[]{"one", "two", "three"};
                        default:
                            return null;
                    }
                }));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, null, null)
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void array_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols().explodeArray($int("a").mapVal(i -> {
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

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }
}
