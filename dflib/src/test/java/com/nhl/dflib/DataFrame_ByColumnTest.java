package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

public class DataFrame_ByColumnTest {

    @Test
    public void testArray() {

        DataFrame df = DataFrame
                .byColumn("a", "b")
                .of(Series.of("a", "b", "c"), Series.ofInt(1, 2, 3));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)

                .expectIntColumns("b")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }

    @Test
    public void testIterable() {

        DataFrame df = DataFrame
                .byColumn("a", "b")
                .ofIterable(List.of(Series.of("a", "b", "c"), Series.ofInt(1, 2, 3)));

        new DataFrameAsserts(df, "a", "b").expectHeight(3)

                .expectIntColumns("b")

                .expectRow(0, "a", 1)
                .expectRow(1, "b", 2)
                .expectRow(2, "c", 3);
    }
}
