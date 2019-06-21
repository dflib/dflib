package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ValuePredicateTest {

    @Test
    public void testIsIn_Array() {

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20, 30, 40)
                .filterRows("a", ValuePredicate.isIn(20, 40));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void testIsIn_Iterable() {

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20, 30, 40)
                .filterRows("a", ValuePredicate.isIn(asList(20, 40)));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void testAnd() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).and(ValuePredicate.isIn(10, 20));

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow( 10, 20, 30, 40)
                .filterRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testOr() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).or(ValuePredicate.isIn(10, 20));

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20, 30, 40)
                .filterRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 10)
                .expectRow(1, 20)
                .expectRow(2, 40);
    }

    @Test
    public void testNegate() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).negate();

        DataFrame df = DataFrame.newFrame("a")
                .foldByRow(10, 20, 30, 40)
                .filterRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }

}
