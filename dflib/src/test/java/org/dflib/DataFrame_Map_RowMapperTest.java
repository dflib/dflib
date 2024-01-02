package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

@Deprecated(since = "0.19", forRemoval = true)
public class DataFrame_Map_RowMapperTest {

    @Test
    public void map_RowMapper_Get() {
        Index i1 = Index.of("a", "b");
        DataFrame df = DataFrame
                .foldByRow(i1).of(1, "x", 2, "y")
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
    public void map_RowMapper_GetWithCast() {
        Index index = Index.of("a", "b");
        DataFrame df = DataFrame
                .foldByRow(index).of(1, "x1", 2, "y2")
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
    public void map_RowMapper_GetWithCast_ByName() {
        Index index = Index.of("a", "b");
        DataFrame df = DataFrame
                .foldByRow(index).of(1, "x1", 2, "y2")
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
    public void map_RowMapper_GetPrimitive() {
        Index index = Index.of("i", "l", "d", "b");
        DataFrame df = DataFrame
                .byColumn(index).of(
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
