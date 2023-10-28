package com.nhl.dflib;

import com.nhl.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

public class DataFrame_MapTest {

    @Test
    public void testMap_UnaryOp() {
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(p -> p.dropColumns("b"));

        new DataFrameAsserts(df, "a")
                .expectHeight(2)
                .expectRow(0, 1)
                .expectRow(1, 2);
    }

    @Test
    public void testMap_Exp() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(Exp.$int(0).mul(10).as("a"), Exp.$col(1).as("b"));

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMap_RowMapper_Get() {
        Index i1 = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame("a", "b")
                .foldByRow(
                        1, "x",
                        2, "y")
                .map(i1, (f, t) -> {
                    t.set(0, ((Integer) f.get(0)) * 10);
                    t.set(1, f.get(1));
                });

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, "x")
                .expectRow(1, 20, "y");
    }

    @Test
    public void testMap_RowMapper_GetWithCast() {
        Index index = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame(index)
                .foldByRow(
                        1, "x1",
                        2, "y2")
                .map(index, (f, t) -> {
                    t.set(0, f.get(0, Integer.class) * 10);
                    t.set(1, f.get(1, String.class).charAt(0));
                });

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, 'x')
                .expectRow(1, 20, 'y');
    }

    @Test
    public void testMap_RowMapper_GetWithCast_ByName() {
        Index index = Index.forLabels("a", "b");
        DataFrame df = DataFrame
                .newFrame(index)
                .foldByRow(
                        1, "x1",
                        2, "y2")
                .map(index, (f, t) -> {
                    t.set(0, f.get("a", Integer.class) * 10);
                    t.set(1, f.get("b", String.class).charAt(0));
                });

        new DataFrameAsserts(df, "a", "b")
                .expectHeight(2)
                .expectRow(0, 10, 'x')
                .expectRow(1, 20, 'y');
    }

    @Test
    public void testMap_RowMapper_GetPrimitive() {
        Index index = Index.forLabels("i", "l", "d", "b");
        DataFrame df = DataFrame
                .byColumn(index)
                .of(
                        Series.ofInt(1, 2, -4),
                        Series.ofLong(10L, 20L, -30L),
                        Series.ofDouble(1.1, 2.2, -4.4),
                        Series.ofBool(true, false, true)
                )
                .map(index, (f, t) -> {
                    t.set(0, f.getInt(0) * 10);
                    t.set(1, f.getLong(1) * 10L);
                    t.set(2, f.getDouble(2) * 10.);
                    t.set(3, !f.getBool(3));
                });

        new DataFrameAsserts(df, "i", "l", "d", "b")
                .expectHeight(3)
                .expectRow(0, 10, 100L, 11., false)
                .expectRow(1, 20, 200L, 22., true)
                .expectRow(2, -40, -300L, -44., false);
    }
}
