package org.dflib;

import org.dflib.DataFrame;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ShapeTest {

    @Test
    public void width() {
        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                2
        );

        assertEquals(1, df.width());
    }

    @Test
    public void height() {
        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                2
        );

        assertEquals(2, df.height());
    }
}
