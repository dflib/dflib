package com.nhl.dflib.filter;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ValuePredicateTest {

    @Test
    public void testIsIn_Array() {

        Index i1 = Index.forLabels("a");
        DataFrame df = DataFrame
                .forSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filter("a", ValuePredicate.isIn(20, 40));

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void testIsIn_Iterable() {

        Index i1 = Index.forLabels("a");
        DataFrame df = DataFrame
                .forSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filter("a", ValuePredicate.isIn(asList(20, 40)));

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void testAnd() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).and(ValuePredicate.isIn(10, 20));

        Index i1 = Index.forLabels("a");
        DataFrame df = DataFrame
                .forSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filter("a", p);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testOr() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).or(ValuePredicate.isIn(10, 20));

        Index i1 = Index.forLabels("a");
        DataFrame df = DataFrame
                .forSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filter("a", p);

        new DFAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 10)
                .expectRow(1, 20)
                .expectRow(2, 40);
    }

    @Test
    public void testNegate() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).negate();

        Index i1 = Index.forLabels("a");
        DataFrame df = DataFrame
                .forSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filter("a", p);

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }

}
