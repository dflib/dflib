package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertThrows;

public class RowColumnSet_Expand_MergeRowMapperTest {

    static final DataFrame EMPTY_TEST_DF = DataFrame.foldByRow("a", "b", "c").of();

    static final DataFrame TEST_DF = DataFrame.byColumn("a", "b", "c")
            .of(
                    Series.ofInt(1, 2, 4, 0, 1, 5),
                    Series.of(List.of("x1", "x2"), List.of("y1", "y2"), List.of("e1", "e2"), List.of("f1", "f2"), List.of("m1", "m2"), null),
                    Series.of("a", "b", "k", "g", "n", "x")
            );

    @Test
    public void empty() {
        DataFrame df = EMPTY_TEST_DF
                .rows().expand("b")
                .cols()
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c").expectHeight(0);
    }

    @Test
    public void all() {
        assertThrows(UnsupportedOperationException.class, () -> TEST_DF
                .rows().expand("b")
                .cols()
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3)
                ));
    }

    @Test
    public void rowsAll_colsByName() {
        DataFrame df = TEST_DF
                .rows().expand("b")
                .cols("b", "a")
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(11)
                .expectRow(0, 3, "x1a", "a")
                .expectRow(1, 3, "x2a", "a")
                .expectRow(2, 6, "y1b", "b")
                .expectRow(3, 6, "y2b", "b")
                .expectRow(4, 12, "e1k", "k")
                .expectRow(5, 12, "e2k", "k")
                .expectRow(6, 0, "f1g", "g")
                .expectRow(7, 0, "f2g", "g")
                .expectRow(8, 3, "m1n", "n")
                .expectRow(9, 3, "m2n", "n")
                .expectRow(10, 15, "nullx", "x");
    }

    @Test
    public void rowByIndex_colsByName() {
        DataFrame df = TEST_DF
                .rows(0, 3, 4, 5).expand("b")
                .cols("b", "a")
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 3, "x1a", "a")
                .expectRow(1, 3, "x2a", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 0, "f1g", "g")
                .expectRow(5, 0, "f2g", "g")
                .expectRow(6, 3, "m1n", "n")
                .expectRow(7, 3, "m2n", "n")
                .expectRow(8, 15, "nullx", "x");
    }

    @Test
    public void rowsByIndex_colsByName_AddRows_AddCols() {
        AtomicInteger rowNum = new AtomicInteger(0);
        DataFrame df = TEST_DF
                .rows(0, 2, 2).expand("b")
                .cols("b", "a", "x")
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3)
                        .set(2, rowNum.incrementAndGet()));

        new DataFrameAsserts(df, "a", "b", "c", "x")
                .expectHeight(10)
                .expectRow(0, 3, "x1a", "a", 1)
                .expectRow(1, 3, "x2a", "a", 2)
                .expectRow(2, 2, List.of("y1", "y2"), "b", null)
                .expectRow(3, 12, "e1k", "k", 3)
                .expectRow(4, 12, "e2k", "k", 4)
                .expectRow(5, 0, List.of("f1", "f2"), "g", null)
                .expectRow(6, 1, List.of("m1", "m2"), "n", null)
                .expectRow(7, 5, null, "x", null)

                // TODO: is this logically correct? These rows was added by duplicating and expanding row 2, yet column
                //  "c" is not in the column set, so should it be "null" instead of "b"?
                .expectRow(8, 12, "e1k", "k", 5)
                .expectRow(9, 12, "e2k", "k", 6);
    }

    @Test
    public void rowByRange_colsByName() {
        DataFrame df = TEST_DF
                .rowsRange(2, 5).expand("b")
                .cols("b", "a")
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 1, List.of("x1", "x2"), "a")
                .expectRow(1, 2, List.of("y1", "y2"), "b")
                .expectRow(2, 12, "e1k", "k")
                .expectRow(3, 12, "e2k", "k")
                .expectRow(4, 0, "f1g", "g")
                .expectRow(5, 0, "f2g", "g")
                .expectRow(6, 3, "m1n", "n")
                .expectRow(7, 3, "m2n", "n")
                .expectRow(8, 5, null, "x");
    }

    @Test
    public void rowsByCondition_colsByName() {
        DataFrame df = TEST_DF
                .rows(Series.ofBool(true, false, false, true, true, true)).expand("b")
                .cols("b", "a")
                .merge((f, t) -> t
                        .set(0, f.get(1, String.class) + f.get(2))
                        .set(1, f.getInt(0) * 3));

        new DataFrameAsserts(df, "a", "b", "c")
                .expectHeight(9)
                .expectRow(0, 3, "x1a", "a")
                .expectRow(1, 3, "x2a", "a")
                .expectRow(2, 2, List.of("y1", "y2"), "b")
                .expectRow(3, 4, List.of("e1", "e2"), "k")
                .expectRow(4, 0, "f1g", "g")
                .expectRow(5, 0, "f2g", "g")
                .expectRow(6, 3, "m1n", "n")
                .expectRow(7, 3, "m2n", "n")
                .expectRow(8, 15, "nullx", "x");
    }
}
