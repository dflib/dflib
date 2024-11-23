package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.dflib.Exp.*;

public class ColumnSet_Merge_MapValTest {

    @Test
    public void test() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, "y")
                .cols("a").merge($col("a").mapVal(v -> ((int) v) * 10));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void withNulls() {
        DataFrame df = DataFrame
                .foldByRow("a", "b")
                .of(1, "x", 2, null)
                .cols(1).merge($col(1).mapVal(v -> v != null ? "not null" : "null", false));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 1, "not null")
                .expectRow(1, 2, "null");
    }

    @Test
    public void valueMapperToDate() {
        DataFrame df = DataFrame
                .foldByRow("a")
                .of(
                        "2018-01-05",
                        "2019-02-28",
                        null)
                .cols("a").merge($str("a").mapVal(LocalDate::parse));

        new DataFrameAsserts(df, "a")
                .expectHeight(3)
                .expectRow(0, LocalDate.of(2018, 1, 5))
                .expectRow(1, LocalDate.of(2019, 2, 28))
                .expectRow(2, (Object) null);
    }

}
