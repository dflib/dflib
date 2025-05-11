package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_CompactDoubleMergeTest {

    @Test
    public void all_forNull() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofDouble(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactDouble(-1)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectDoubleColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1., -1., 1.)
                .expectRow(1, 2., 5., 0.);
    }

    @Test
    public void all_mapper() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of("a", "ab"),
                        Series.of("abc", "abcd")
                )
                .cols()
                .compactDouble((String o) -> o.length() + 0.1)
                .merge();

        new DataFrameAsserts(df, "a", "b")
                .expectDoubleColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, 1.1, 3.1)
                .expectRow(1, 2.1, 4.1);
    }

    @Test
    public void some_forNull() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofDouble(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactDouble(-1)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectDoubleColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1., -1., 1., "one")
                .expectRow(1, 2., 5., 0., "two");
    }

    @Test
    public void some_mapper() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of("a", "ab"),
                        Series.of("one", "two"),
                        Series.of("abc", "abcd")
                )
                .cols("a", "c")
                .compactDouble((String o) -> o.length() + 0.1)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectDoubleColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, 1.1, "one", 3.1)
                .expectRow(1, 2.1, "two", 4.1);
    }

}
