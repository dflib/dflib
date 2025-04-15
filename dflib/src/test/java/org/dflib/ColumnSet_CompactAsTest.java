package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnSet_CompactAsTest {

    @Test
    public void all() {
        DataFrame df = DataFrame.byColumn("a", "b", "c").of(
                        Series.ofInt(1, 2, 2, 2),
                        Series.of(null, new String("A"), new String("A"), new String("a")),
                        Series.of(new Boolean(false), new Boolean(false), new Boolean(false), new Boolean(true))
                )
                .cols()
                .compact()
                .as("A", "B", "C");

        new DataFrameAsserts(df, "A", "B", "C")
                .expectHeight(4)
                .expectIntColumns("A")
                .expectRow(0, 1, null, false)
                .expectRow(1, 2, "A", false)
                .expectRow(2, 2, "A", false)
                .expectRow(3, 2, "a", true);

        assertEquals(3, df.getColumn("B").map(System::identityHashCode).unique().size());
        assertEquals(2, df.getColumn("C").map(System::identityHashCode).unique().size());
    }

    @Test
    public void cols() {

        DataFrame df = DataFrame.byColumn("a", "b", "c", "d").of(
                        Series.ofInt(1, 2, 2, 2),
                        Series.of(null, new String("A"), new String("A"), new String("a")),
                        Series.of(new Boolean(false), new Boolean(false), new Boolean(false), new Boolean(true)),
                        Series.of(null, new String("B"), new String("B"), new String("b")))
                .cols("a", "b", "c")
                .compact()
                .as("A", "B", "C");

        new DataFrameAsserts(df, "A", "B", "C", "d")
                .expectHeight(4)
                .expectIntColumns("A")
                .expectRow(0, 1, null, false, null)
                .expectRow(1, 2, "A", false, "B")
                .expectRow(2, 2, "A", false, "B")
                .expectRow(3, 2, "a", true, "b");

        assertEquals(3, df.getColumn("B").map(System::identityHashCode).unique().size());
        assertEquals(2, df.getColumn("C").map(System::identityHashCode).unique().size());
        assertEquals(4, df.getColumn("d").map(System::identityHashCode).unique().size());
    }
}
