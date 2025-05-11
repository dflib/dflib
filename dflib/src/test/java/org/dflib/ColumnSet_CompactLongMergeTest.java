package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_CompactLongMergeTest {

    @Test
    public void all_compactLong() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofLong(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactLong(-1)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectLongColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1L, -1L, 1L)
                .expectRow(1, 2L, 5L, 0L);
    }

    @Test
    public void all_compactLongConverter() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of("a", "ab"),
                        Series.of("abc", "abcd")
                )
                .cols()
                .compactLong((String o) -> o.length())
                .merge();

        new DataFrameAsserts(df, "a", "b")
                .expectLongColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, 1L, 3L)
                .expectRow(1, 2L, 4L);
    }

    @Test
    public void compactLong() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofLong(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactLong(-1)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectLongColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1L, -1L, 1L, "one")
                .expectRow(1, 2L, 5L, 0L, "two");
    }

    @Test
    public void compactLongConverter() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of("a", "ab"),
                        Series.of("one", "two"),
                        Series.of("abc", "abcd")
                )
                .cols("a", "c")
                .compactLong((String o) -> o.length())
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectLongColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, 1L, "one", 3L)
                .expectRow(1, 2L, "two", 4L);
    }

}
