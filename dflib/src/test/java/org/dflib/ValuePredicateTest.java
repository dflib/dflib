package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static java.util.Arrays.asList;

public class ValuePredicateTest {

    @Test
    public void isIn_Array() {

        DataFrame df = DataFrame.foldByRow("a")
                .of(10, 20, 30, 40)
                .selectRows("a", ValuePredicate.isIn(20, 40));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void isIn_Iterable() {

        DataFrame df = DataFrame.foldByRow("a")
                .of(10, 20, 30, 40)
                .selectRows("a", ValuePredicate.isIn(asList(20, 40)));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 20)
                .expectRow(1, 40);
    }

    @Test
    public void and() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).and(ValuePredicate.isIn(10, 20));

        DataFrame df = DataFrame.foldByRow("a")
                .of( 10, 20, 30, 40)
                .selectRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void or() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).or(ValuePredicate.isIn(10, 20));

        DataFrame df = DataFrame.foldByRow("a")
                .of(10, 20, 30, 40)
                .selectRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, 10)
                .expectRow(1, 20)
                .expectRow(2, 40);
    }

    @Test
    public void negate() {

        ValuePredicate<Integer> p = ValuePredicate.isIn(20, 40).negate();

        DataFrame df = DataFrame.foldByRow("a")
                .of(10, 20, 30, 40)
                .selectRows("a", p);

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 10)
                .expectRow(1, 30);
    }

}
