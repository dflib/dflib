package com.nhl.dflib.filter;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

import static java.util.Arrays.asList;

public class ValuePredicateTest {

    @Test
    public void testIsIn_Array() {

        Index i1 = Index.withNames("a");
        DataFrame df = DataFrame
                .fromSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filterByColumn("a", ValuePredicate.isIn(20, 40));

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void testIsIn_Iterable() {

        Index i1 = Index.withNames("a");
        DataFrame df = DataFrame
                .fromSequenceFoldByRow(i1, 10, 20, 30, 40)
                .filterByColumn("a", ValuePredicate.isIn(asList(20, 40)));

        new DFAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

}
