package org.dflib.series;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class FixedSizeBitSetTest {

    @Test
    public void testCreate() {
        FixedSizeBitSet set1 = new FixedSizeBitSet(1);
        assertEquals(1, set1.getSize());
        assertEquals(1, set1.data.length);

        FixedSizeBitSet set10 = new FixedSizeBitSet(10);
        assertEquals(10, set10.getSize());
        assertEquals(1, set10.data.length);

        FixedSizeBitSet set64 = new FixedSizeBitSet(64);
        assertEquals(64, set64.getSize());
        assertEquals(1, set64.data.length);

        FixedSizeBitSet set65 = new FixedSizeBitSet(65);
        assertEquals(65, set65.getSize());
        assertEquals(2, set65.data.length);

        FixedSizeBitSet set100 = new FixedSizeBitSet(100);
        assertEquals(100, set100.getSize());
        assertEquals(2, set100.data.length);

        FixedSizeBitSet set1000 = new FixedSizeBitSet(1000);
        assertEquals(1000, set1000.getSize());
        assertEquals(16, set1000.data.length);
    }

    @Test
    public void testSet1() {
        FixedSizeBitSet set = new FixedSizeBitSet(1);
        assertEquals(1, set.getSize());
        assertEquals(1, set.data.length);
        assertEquals(0L, set.data[0]);

        set.set(0, true);

        assertEquals(1, set.getSize());
        assertEquals(1, set.data.length);
        assertEquals(1L, set.data[0]);

        assertTrue(set.get(0));
    }

    @Test
    public void testSet10() {
        FixedSizeBitSet set = new FixedSizeBitSet(10);
        assertEquals(10, set.getSize());
        assertEquals(1, set.data.length);
        assertEquals(0L, set.data[0]);

        set.set(0, true);
        set.set(3, true);
        set.set(9, true);

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
        FixedSizeBitSet set = new FixedSizeBitSet(100);
        assertEquals(100, set.getSize());
        assertEquals(2, set.data.length);
        assertEquals(0L, set.data[0]);
        assertEquals(0L, set.data[1]);

        set.set(0, true);
        set.set(30, true);
        set.set(64, true);
        set.set(90, true);

        assertEquals(100, set.getSize());
        assertEquals(2, set.data.length);
        //           bits set:   30                ...                0
        assertEquals(0b0100_0000_0000_0000_0000_0000_0000_0001L, set.data[0]);
        //           bits set:        90           ...                64
        assertEquals(0b0000_0100_0000_0000_0000_0000_0000_0001L, set.data[1]);

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
        FixedSizeBitSet set = new FixedSizeBitSet(100);

        assertEquals(0, set.countTrue());

        set.set(10);
        set.set(20);
        set.set(30);
        set.set(40);
        set.set(50);
        set.set(60);
        set.set(70);
        set.set(80);
        set.set(90);

        assertEquals(9, set.countTrue());

        set.clear(20);
        set.clear(40);
        set.clear(60);
        set.clear(80);

        assertEquals(5, set.countTrue());
    }

    @Test
    public void testCountFalse() {
        FixedSizeBitSet set = new FixedSizeBitSet(100);

        assertEquals(100, set.countFalse());

        set.set(10);
        set.set(20);
        set.set(30);
        set.set(40);
        set.set(50);
        set.set(60);
        set.set(70);
        set.set(80);
        set.set(90);

        assertEquals(91, set.countFalse());

        set.clear(20);
        set.clear(40);
        set.clear(60);
        set.clear(80);

        assertEquals(95, set.countFalse());
    }

    @Test
    public void testFirstTrue() {
        FixedSizeBitSet set = new FixedSizeBitSet(100);
        assertEquals(-1, set.firstTrue());

        set.set(0);
        assertEquals(0, set.firstTrue());

        set.clear(0);
        assertEquals(-1, set.firstTrue());

        set.set(99);
        assertEquals(99, set.firstTrue());

        set.clear(99);
        assertEquals(-1, set.firstTrue());

        set.set(99);
        assertEquals(99, set.firstTrue());

        set.set(70);
        assertEquals(70, set.firstTrue());

        set.set(50);
        assertEquals(50, set.firstTrue());

        set.set(0);
        assertEquals(0, set.firstTrue());
    }

    @Test
    public void testNot() {
        FixedSizeBitSet set = new FixedSizeBitSet(4);

        FixedSizeBitSet not1 = set.not();
        assertEquals(0b1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111L,
                not1.data[0]);

        set.set(1);
        set.set(3);

        FixedSizeBitSet not2 = set.not();
        assertEquals(0b1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_0101L,
                not2.data[0]);
    }

}