package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DataFrame_GetTest {

    @Test
    public void byColName() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                        1, "x",
                        2, "y",
                        3, "z");

        assertEquals(2, df.get("a", 1));
        assertEquals("z", df.get("b", 2));
    }

    @Test
    public void byColPos() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                3, "z");

        assertEquals(2, df.get(0, 1));
        assertEquals("z", df.get(1, 2));
    }

    @Test
    public void byColPos_OutOfBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                3, "z");

        assertThrows(IllegalArgumentException.class, () -> df.get(2, 1));
    }

    @Test
    public void byColName_Absent() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                3, "z");

        assertThrows(IllegalArgumentException.class, () -> df.get("c", 1));
    }

    @Test
    public void byRowPos_OutOfBounds() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, "x",
                2, "y",
                3, "z");

        // TODO: all the other out of bounds flavors throw IllegalArgumentException... We should unify the exceptions
        assertThrows(ArrayIndexOutOfBoundsException.class, () -> df.get(0, 3));
    }
}
