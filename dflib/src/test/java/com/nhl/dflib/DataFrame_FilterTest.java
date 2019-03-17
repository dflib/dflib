package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;

@RunWith(Parameterized.class)
public class DataFrame_FilterTest extends BaseDataFrameTest {

    public DataFrame_FilterTest(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testFilterByColumn_Name() {

        Index i1 = Index.withNames("a");
        DataFrame df = createDf(i1, 10, 20)
                .filterByColumn("a", (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }

    @Test
    public void testFilterByColumn_Pos() {

        Index i1 = Index.withNames("a");
        DataFrame df = createDf(i1, 10, 20)
                .filterByColumn(0, (Integer v) -> v > 15);

        new DFAsserts(df, "a")
                .expectHeight(1)
                .expectRow(0, 20);
    }
}
