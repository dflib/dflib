package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class ColumnSet_CompactBoolMergeTest {

    @Test
    public void all_compactBool() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofBool(true, false),
                        Series.of(null, "true"),
                        Series.of(Boolean.TRUE, Boolean.FALSE)
                )
                .cols()
                .compactBool()
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectBooleanColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, true, false, true)
                .expectRow(1, false, true, false);
    }

    @Test
    public void all_compactBoolConverter() {
        DataFrame df = DataFrame.byColumn("a", "b").of(
                        Series.of(5, 6),
                        Series.ofInt(8, 9)
                )
                .cols()
                .compactBool((Integer o) -> o % 2 == 0)
                .merge();

        new DataFrameAsserts(df, "a", "b")
                .expectBooleanColumns("a", "b")
                .expectHeight(2)
                .expectRow(0, false, true)
                .expectRow(1, true, false);
    }

    @Test
    public void compactBool() {
        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofBool(true, false),
                        Series.of(null, "true"),
                        Series.of(Boolean.TRUE, Boolean.FALSE),
                        Series.of("one", "two")
                )
                .cols("a", "b", "c")
                .compactBool()
                .merge();

        new DataFrameAsserts(df, "a", "b", "c", "d")
                .expectBooleanColumns("a", "b", "c")
                .expectHeight(2)
                .expectRow(0, true, false, true, "one")
                .expectRow(1, false, true, false, "two");
    }

    @Test
    public void compactBoolConverter() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.of(5, 6),
                        Series.of("one", "two"),
                        Series.ofInt(8, 9)
                )
                .cols("a", "c")
                .compactBool((Integer o) -> o % 2 == 0)
                .merge();

        new DataFrameAsserts(df, "a", "b", "c")
                .expectBooleanColumns("a", "c")
                .expectHeight(2)
                .expectRow(0, false, "one", true)
                .expectRow(1, true, "two", false);
    }

}
