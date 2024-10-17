package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedSizeBitSetTest {

    @Test
    public void testCreate() {
        FixedSizeBitSet set1 = new FixedSizeBitSet(new long[1], 1);
        assertEquals(1, set1.getSize());
        assertEquals(1, set1.data.length);

        FixedSizeBitSet set10 = new FixedSizeBitSet(new long[1], 10);
        assertEquals(10, set10.getSize());
        assertEquals(1, set10.data.length);

        FixedSizeBitSet set64 = new FixedSizeBitSet(new long[1], 64);
        assertEquals(64, set64.getSize());
        assertEquals(1, set64.data.length);

        FixedSizeBitSet set65 = new FixedSizeBitSet(new long[2], 65);
        assertEquals(65, set65.getSize());
        assertEquals(2, set65.data.length);

        FixedSizeBitSet set100 = new FixedSizeBitSet(new long[2], 100);
        assertEquals(100, set100.getSize());
        assertEquals(2, set100.data.length);

        FixedSizeBitSet set1000 = new FixedSizeBitSet(new long[16], 1000);
        assertEquals(1000, set1000.getSize());
        assertEquals(16, set1000.data.length);
    }

    @Test
    public void testSet10() {
        long[] data = {0b0010_0000_1001L};
        FixedSizeBitSet set = new FixedSizeBitSet(data, 10);
        assertEquals(10, set.getSize());
        assertEquals(1, set.data.length);
        //      bits set:         9  ...  3  0
        assertEquals(0b0010_0000_1001L, set.data[0]);

        assertTrue(set.get(0));
        assertFalse(set.get(1));
        assertFalse(set.get(2));
        assertTrue(set.get(3));
        assertFalse(set.get(4));
        assertFalse(set.get(5));
        assertFalse(set.get(6));
        assertFalse(set.get(7));
        assertFalse(set.get(8));
        assertTrue(set.get(9));
    }

    @Test
    public void testSet100() {
        long[] data = {
                0b0100_0000_0000_0000_0000_0000_0000_0001L,
                0b0000_0100_0000_0000_0000_0000_0000_0001L
        };
        FixedSizeBitSet set = new FixedSizeBitSet(data, 100);
        assertEquals(100, set.getSize());
        assertEquals(2, set.data.length);

        assertTrue(set.get(0));
        assertFalse(set.get(1));
        assertTrue(set.get(30));
        assertFalse(set.get(31));
        assertFalse(set.get(63));
        assertTrue(set.get(64));
        assertFalse(set.get(65));
        assertTrue(set.get(90));
        assertFalse(set.get(99));
    }

    @Test
    public void testCountTrue() {
        long[] data = {
                0b0100_0000_0000_0000_0000_0000_0000_0001L,
                0b0000_0100_0000_0000_0000_0000_0000_0001L
        };
        FixedSizeBitSet set = new FixedSizeBitSet(data, 100);
        assertEquals(4, set.countTrue());
    }

    @Test
    public void testCountFalse() {
        long[] data = {
                0b0100_0000_0000_0000_0000_0000_0000_0001L,
                0b0000_0100_0000_0000_0000_0000_0000_0001L
        };
        FixedSizeBitSet set = new FixedSizeBitSet(data, 100);
        assertEquals(100 - 4, set.countFalse());
    }

    @Test
    public void testFirstTrue() {
        long[] data = {
                0b0100_0000_0000_0000_0000_0000_0001_0000L,
                0b0000_0100_0000_0000_0000_0000_0000_0001L
        };
        FixedSizeBitSet set = new FixedSizeBitSet(data, 100);
        assertEquals(4, set.firstTrue());
    }

    @Test
    public void testNot() {
        long[] data = {0b1100};
        FixedSizeBitSet set = new FixedSizeBitSet(data,4);

        FixedSizeBitSet not1 = set.not();
        assertEquals(0b1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_0011L,
                not1.data[0]);
    }

}