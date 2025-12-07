package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.*;

public class ColumnSet_Merge_ExpandArrayTest {

    @Test
    public void all_DynamicSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols()
                .expandArray($int("a").mapVal(i -> switch (i) {
                    case 1 -> new String[]{"one"};
                    case 2 -> new String[]{"one", "two"};
                    case 3 -> new String[]{"one", "two", "three"};
                    default -> null;
                }))
                .merge();

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
                .expandArray($val(new String[]{"one", "two"}))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two")
                .expectRow(1, 2, "y", "one", "two");
    }

    @Test
    public void cols_Transform() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("a", "3")
                .expandArray($val(new String[]{"one", "two"}))
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
    public void colsExcept() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")

                // columns spec is ignored since merge() is empty
                .colsExcept("3", "b")
                .expandArray($val(new String[]{"one", "two", "three"}))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two", "three")
                .expectRow(1, 2, "y", "one", "two", "three");
    }

    @Test
    public void colsPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")

                // columns spec is ignored since merge() is empty
                .cols(c -> Character.isLetter(c.charAt(0)) || (Character.isDigit(c.charAt(0)) && Integer.parseInt(c) != 2))
                .expandArray($val(new String[]{"one", "two", "three"}))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two", "three")
                .expectRow(1, 2, "y", "one", "two", "three");
    }

    @Test
    public void colsPredicate_Transform() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols(c -> Character.isLetter(c.charAt(0)) || (Character.isDigit(c.charAt(0)) && Integer.parseInt(c) != 2))
                .expandArray($val(new String[]{"one", "two", "three"}))
                .merge(
                        $int("a").mul(2),
                        concat($str("b"), "|"),
                        concat($str("3"), ">"),
                        concat($str("4"), "<")
                );

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(2)
                .expectRow(0, 2, "x|", "one", "two>", "three<")
                .expectRow(1, 4, "y|", "one", "two>", "three<");
    }

    @Test
    public void colsExceptPredicate() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .colsExcept(c -> Character.isDigit(c.charAt(0)) && Integer.parseInt(c) == 2)
                .expandArray($val(new String[]{"one", "two", "three"}))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(2)
                .expectRow(0, 1, "x", "one", "two", "three")
                .expectRow(1, 2, "y", "one", "two", "three");
    }

    @Test
    public void varyingSize() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expandArray($int("a").mapVal(i -> switch (i) {
                    case 1 -> new String[]{"one"};
                    case 2 -> new String[]{"one", "two"};
                    case 3 -> new String[]{"one", "two", "three"};
                    default -> null;
                }))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", "one", "two", null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }

    @Test
    public void withNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b")
                .of(1, "x", 2, "y", 3, "z")
                .cols("a", "2", "3")
                .expandArray($int("a").mapVal(i -> switch (i) {
                    case 1 -> new String[]{"one"};
                    case 3 -> new String[]{"one", "two", "three"};
                    default -> null;
                }))
                .merge();

        new DataFrameAsserts(df, "a", "b", "2", "3", "4")
                .expectHeight(3)
                .expectRow(0, 1, "x", "one", null, null)
                .expectRow(1, 2, "y", null, null, null)
                .expectRow(2, 3, "z", "one", "two", "three");
    }
}
