package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_CompactFloatMergeTest {

    @Test
    public void all_forNull() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofFloat(1, 2),
                        Series.of(null, "5"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactFloat(-1f)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectFloatColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1.f, -1.f, 1.f)
                .expectRow(1, 2.f, 5.f, 0.f);
    }

    @Test
    public void all_mapper() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of("a", "ab"),
                        Series.of("abc", "abcd")
                )
                .cols()
                .compactFloat((String o) -> o.length() + 0.1f)
                .merge();

        new DataFrameAsserts(df, "a", "b")
                .expectFloatColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, 1.1f, 3.1f)
                .expectRow(1, 2.1f, 4.1f);
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
                .compactFloat(-1f)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectFloatColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, 1.f, -1.f, 1.f, "one")
                .expectRow(1, 2.f, 5.f, 0.f, "two");
    }

    @Test
    public void some_mapper() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of("a", "ab"),
                        Series.of("one", "two"),
                        Series.of("abc", "abcd")
                )
                .cols("a", "c")
                .compactFloat((String o) -> o.length() + 0.1f)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectFloatColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, 1.1f, "one", 3.1f)
                .expectRow(1, 2.1f, "two", 4.1f);
    }

}
