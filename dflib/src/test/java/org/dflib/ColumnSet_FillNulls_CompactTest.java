package org.dflib;

import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ColumnSet_FillNulls_CompactTest {

    @Test
    public void fillNulls() {
        DataFrame df = DataFrame.foldByRow("a", "b", "c").of(
                        1, new String("x"), null,
                        null, new String("x"), new String("a"),
                        3, null, new String("a")
                )
                .cols("b", "c", "new")
                .compact()
                .fillNulls("*");

        new DataFrameAsserts(df, "a", "b", "c", "new")
                .expectHeight(3)
                .expectRow(0, 1, "x", "*", "*")
                .expectRow(1, null, "x", "a", "*")
                .expectRow(2, 3, "*", "a", "*");


        assertEquals(2, df.getColumn("b").map(System::identityHashCode).unique().size());
        assertEquals(2, df.getColumn("c").map(System::identityHashCode).unique().size());
        assertEquals(1, df.getColumn("new").map(System::identityHashCode).unique().size());
    }
}
