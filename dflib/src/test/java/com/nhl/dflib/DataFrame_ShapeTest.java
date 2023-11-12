package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class DataFrame_ShapeTest {

    @Test
    public void width() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                1,
                2
        );

        assertEquals(1, df.width());
    }

    @Test
    public void height() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                1,
                2
        );

        assertEquals(2, df.height());
    }
}
