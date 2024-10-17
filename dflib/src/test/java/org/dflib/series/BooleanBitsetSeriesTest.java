package org.dflib.series;

import org.dflib.BooleanSeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanBitsetSeriesTest {

    @Test
    public void getBoolean() {
        FixedSizeBitSet bitSet = new FixedSizeBitSet(new long[]{0b1001L}, 4);
        BooleanBitsetSeries s = new BooleanBitsetSeries(bitSet);
        assertTrue(s.getBool(0));
        assertFalse(s.getBool(1));
        assertFalse(s.getBool(2));
        assertTrue(s.getBool(3));
    }

    @Test
    public void copyToBool() {
        FixedSizeBitSet bitSet = new FixedSizeBitSet(new long[]{0b1001L}, 4);
        BooleanBitsetSeries s = new BooleanBitsetSeries(bitSet);
        boolean[] copy = new boolean[4];
        s.copyToBool(copy, 0, 0, copy.length);

        assertTrue(copy[0]);
        assertFalse(copy[1]);
        assertFalse(copy[2]);
        assertTrue(copy[3]);
    }

    @Test
    public void copyToObject() {
        FixedSizeBitSet bitSet = new FixedSizeBitSet(new long[]{0b1001L}, 4);
        BooleanBitsetSeries s = new BooleanBitsetSeries(bitSet);
        Object[] copy = new Object[4];
        s.copyTo(copy, 0, 0, copy.length);

        assertEquals(Boolean.TRUE, copy[0]);
        assertEquals(Boolean.FALSE, copy[1]);
        assertEquals(Boolean.FALSE, copy[2]);
        assertEquals(Boolean.TRUE, copy[3]);
    }

    @Test
    public void rangeBool() {
        FixedSizeBitSet bitSet = new FixedSizeBitSet(new long[]{0b1001L}, 4);
        BooleanBitsetSeries s = new BooleanBitsetSeries(bitSet);

        BooleanSeries range = s.rangeBool(2, 4);

        assertEquals(2, range.size());
        assertInstanceOf(BooleanBitsetSeries.class, range);
        assertFalse(range.getBool(0));
        assertTrue(range.getBool(1));
    }

    @Test
    public void firstTrue() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{0b0100L}, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        assertEquals(2, s1.firstTrue());

        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{0b0000L, 0b1000_0000_0000_0000}, 100);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        assertEquals(79, s2.firstTrue());
    }

    @Test
    public void countTrue() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{0b0100L}, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        assertEquals(1, s1.countTrue());

        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{0b1111L, 0b1001_0000_0010_0001}, 100);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        assertEquals(8, s2.countTrue());
    }

    @Test
    public void isTrue() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{
                0x00000000_000000FFL
        }, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        assertTrue(s1.isTrue());

        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{
                0xFFFFFFFF_FFFFFFFFL,
                0x0000FFFF_FFFFFFFFL
        }, 100);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        assertTrue(s2.isTrue());

        FixedSizeBitSet bitSet3 = new FixedSizeBitSet(new long[]{
                0xFFFFFFFF_FFFFFFFFL,
                0x0000FFFF_FFFF0FFFL
        }, 100);
        BooleanBitsetSeries s3 = new BooleanBitsetSeries(bitSet3);
        assertFalse(s3.isTrue());
    }

    @Test
    public void isFalse() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{
                0x00000000_00000000L
        }, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        assertTrue(s1.isFalse());

        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{
                0x00000000_00000000L,
                0xFFFF0000_00000000L
        }, 100);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        assertTrue(s2.isFalse());

        FixedSizeBitSet bitSet3 = new FixedSizeBitSet(new long[]{
                0x00000000_00000000L,
                0xFFFFF000_00000F00L
        }, 100);
        BooleanBitsetSeries s3 = new BooleanBitsetSeries(bitSet3);
        assertFalse(s3.isFalse());
    }

    @Test
    public void countFalse() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{0b0100L}, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        assertEquals(3, s1.countFalse());

        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{0b1111L, 0b1001_0000_0010_0001}, 100);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        assertEquals(100 - 8, s2.countFalse());
    }

    @Test
    public void not() {
        FixedSizeBitSet bitSet = new FixedSizeBitSet(new long[]{0b1011L}, 4);
        BooleanBitsetSeries s = new BooleanBitsetSeries(bitSet);
        BooleanBitsetSeries not = s.not();

        assertFalse(not.get(0));
        assertFalse(not.get(1));
        assertTrue(not.get(2));
        assertFalse(not.get(3));
    }

    @Test
    public void and() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{0b1011L}, 4);
        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{0b0011L}, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        BooleanSeries s3 = s1.and(s2);

        assertTrue(s3.get(0));
        assertTrue(s3.get(1));
        assertFalse(s3.get(2));
        assertFalse(s3.get(3));
    }

    @Test
    public void or() {
        FixedSizeBitSet bitSet1 = new FixedSizeBitSet(new long[]{0b1011L}, 4);
        FixedSizeBitSet bitSet2 = new FixedSizeBitSet(new long[]{0b0011L}, 4);
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(bitSet1);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(bitSet2);
        BooleanSeries s3 = s1.or(s2);

        assertTrue(s3.get(0));
        assertTrue(s3.get(1));
        assertFalse(s3.get(2));
        assertTrue(s3.get(3));
    }
}