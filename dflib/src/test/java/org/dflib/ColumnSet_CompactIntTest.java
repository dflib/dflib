package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_CompactIntTest {

    @Test
    public void all_compactInt() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofInt(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactInt(-1);

        new DataFrameAsserts(df, "a", "b", "c")
                .expectIntColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, -1, 1)
                .expectRow(1, 2, 5, 0);
    }

    @Test
    public void all_compactIntConverter() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of("a", "ab"),
                        Series.of("abc", "abcd")
                )
                .cols()
                .compactInt((String o) -> o.length());

        new DataFrameAsserts(df, "a", "b")
                .expectIntColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, 1, 3)
                .expectRow(1, 2, 4);
    }

    @Test
    public void compactInt() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofInt(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactInt(-1);

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectIntColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1, -1, 1, "one")
                .expectRow(1, 2, 5, 0, "two");
    }

    @Test
    public void compactIntConverter() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of("a", "ab"),
                        Series.of("one", "two"),
                        Series.of("abc", "abcd")
                )
                .cols("a", "c")
                .compactInt((String o) -> o.length());

        new DataFrameAsserts(df, "a", "b", "c")
                .expectIntColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, 1, "one", 3)
                .expectRow(1, 2, "two", 4);
    }

}
