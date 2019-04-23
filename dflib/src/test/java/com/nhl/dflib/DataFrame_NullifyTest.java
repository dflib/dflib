package com.nhl.dflib;

import com.nhl.dflib.unit.DFAsserts;
import org.junit.Test;

public class DataFrame_NullifyTest extends BaseDataFrameTest {

    @Test
    public void testNullify() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame cond = DataFrame.forColumns(i1,
                BooleanSeries.forBooleans(true, false),
                BooleanSeries.forBooleans(true, false));

        DataFrame df = createDf(i1,
                1, "x",
                2, "y").nullify(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, null, null)
                .expectRow(1, 2, "y");
    }

    @Test
    public void testNullifyNoMatch() {
        Index i1 = Index.forLabels("a", "b");

        DataFrame cond = DataFrame.forColumns(i1,
                BooleanSeries.forBooleans(true, false),
                BooleanSeries.forBooleans(true, false));

        DataFrame df = createDf(i1,
                1, "x",
                2, "y").nullifyNoMatch(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "x")
                .expectRow(1, null, null);
    }

    @Test
    public void testNullifyByColumn() {
        Index i0 = Index.forLabels("c", "b");
        Index i1 = Index.forLabels("a", "b");

        DataFrame cond = DataFrame.forColumns(i0,
                BooleanSeries.forBooleans(true, false),
                BooleanSeries.forBooleans(true, false));

        DataFrame df = createDf(i1,
                1, "x",
                2, "y").nullify(cond);

        new DFAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, null)
                .expectRow(1, 2, "y");
    }


}
