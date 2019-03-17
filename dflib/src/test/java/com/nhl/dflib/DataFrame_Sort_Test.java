package com.nhl.dflib;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;

import static java.util.Arrays.asList;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class DataFrame_Sort_Test extends BaseDataFrameTest {

    public DataFrame_Sort_Test(boolean columnar) {
        super(columnar);
    }

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(new Object[][]{{false}, {true}});
    }

    @Test
    public void testSort() {
        Index i = Index.withNames("a", "b");
        DataFrame dfi = createDf(i,
                0, 1,
                2, 3,
                -1, 2);

        DataFrame df = dfi.sort(r -> (Integer) r.get("a"));
        assertNotSame(dfi, df);

        new DFAsserts(dfi, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 1)
                .expectRow(1, 2, 3)
                .expectRow(2, -1, 2);

        new DFAsserts(df, "a", "b")
                .expectHeight(3)
                .expectRow(0, -1, 2)
                .expectRow(1, 0, 1)
                .expectRow(2, 2, 3);

    }

    @Test
    public void testSortByColumns_Names() {
        Index i = Index.withNames("a", "b");
        DataFrame dfi = createDf(i,
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sortByColumns("a", "b");
        assertNotSame(dfi, dfab);

        new DFAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sortByColumns("b", "a");
        assertNotSame(dfi, dfba);

        new DFAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);

    }

    @Test
    public void testSortByColumns_Positions() {
        Index i = Index.withNames("a", "b");
        DataFrame dfi = createDf(i,
                0, 4,
                2, 2,
                0, 2);

        DataFrame dfab = dfi.sortByColumns(0, 1);
        assertNotSame(dfi, dfab);

        new DFAsserts(dfab, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 0, 4)
                .expectRow(2, 2, 2);

        DataFrame dfba = dfi.sortByColumns(1, 0);
        assertNotSame(dfi, dfba);

        new DFAsserts(dfba, "a", "b")
                .expectHeight(3)
                .expectRow(0, 0, 2)
                .expectRow(1, 2, 2)
                .expectRow(2, 0, 4);

    }
}
