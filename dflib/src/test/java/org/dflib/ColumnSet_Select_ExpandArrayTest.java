package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;
import static org.dflib.Exp.$str;

public class ColumnSet_Select_ExpandArrayTest {

    @Test
    public void cols() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("a", "3", "2")
                .expandArray($val(new String[]{"one", "two"}))
                .select();

        new DataFrameAsserts(df, "a", "3", "2")
                .expectHeight(2)
                .expectRow(0, 1, "two", "one")
                .expectRow(1, 2, "two", "one");
    }

    @Test
    public void colsExcept() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsExcept("3", "b")
                .expandArray($val(new String[]{"one", "two", "three"}))
                .select();

        new DataFrameAsserts(df, "a", "2", "4")
                .expectHeight(2)
                .expectRow(0, 1, "one", "three")
                .expectRow(1, 2, "one", "three");
    }

    @Test
    public void colsPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(c -> Character.isLetter(c.charAt(0)) || (Character.isDigit(c.charAt(0)) && Integer.parseInt(c) != 2))
                .expandArray($val(new String[]{"one", "two", "three"}))
                .select();

        new DataFrameAsserts(df, "a", "b", "3", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "two", "three")
                .expectRow(1, 2, "y", "two", "three");
    }

    @Test
    public void colsPredicate_Transform() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(c -> Character.isLetter(c.charAt(0)) || (Character.isDigit(c.charAt(0)) && Integer.parseInt(c) != 2))
                .expandArray($val(new String[]{"one", "two", "three"}))
                .select(
                        $int("a").mul(2),
                        concat($str("b"), "|"),
                        concat($str("3"), ">"),
                        concat($str("4"), "<")
                );

        new DataFrameAsserts(df, "a", "b", "3", "4")
                .expectHeight(2)
                .expectRow(0, 2, "x|", "two>", "three<")
                .expectRow(1, 4, "y|", "two>", "three<");
    }

    @Test
    public void colsExceptPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsExcept(c -> Character.isDigit(c.charAt(0)) && Integer.parseInt(c) == 2)
                .expandArray($val(new String[]{"one", "two", "three"}))
                .select();

        new DataFrameAsserts(df, "a", "b", "3", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "two", "three")
                .expectRow(1, 2, "y", "two", "three");
    }

    @Test
    public void varyingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expandArray($int("a").mapVal(i -> {

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
                }))
                .select();

        new DataFrameAsserts(df, "a", "2", "3")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, "one", "two")
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void withNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expandArray($int("a").mapVal(i -> {

                    switch (i) {
                        case 1:
                            return new String[]{"one"};
                        case 3:
                            return new String[]{"one", "two", "three"};
                        default:
                            return null;
                    }
                }))
                .select();

        new DataFrameAsserts(df, "a", "2", "3")
                .expectHeight(3)
                .expectRow(0, 1, "one", null)
                .expectRow(1, 2, null, null)
                .expectRow(2, 3, "one", "two");
    }

    @Test
    public void dynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols()
                .expandArray($int("a").mapVal(i -> {
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
                }))
                .select();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }


}
