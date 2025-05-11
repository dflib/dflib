package org.dflib.series;

import org.dflib.BooleanSeries;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class BooleanBitsetSeriesTest {

    static final long LONG_BITS_MASK = 0xFFFFFFFF_FFFFFFFFL;

    @Test
    public void create() {
        long[] data = {
                0b0100_0000_0000_0000_0000_0000_0000_0001L,
                0b0000_0100_0000_0000_0000_0000_0000_0001L
        };
        BooleanBitsetSeries set = new BooleanBitsetSeries(data, 100);
        assertEquals(100, set.size());

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
    public void getBoolean() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
        assertTrue(s.getBool(0));
        assertFalse(s.getBool(1));
        assertFalse(s.getBool(2));
        assertTrue(s.getBool(3));
    }

    @Test
    public void copyToBool() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
        boolean[] copy = new boolean[4];
        s.copyToBool(copy, 0, 0, copy.length);

        assertTrue(copy[0]);
        assertFalse(copy[1]);
        assertFalse(copy[2]);
        assertTrue(copy[3]);
    }

    @Test
    public void copyToObject() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
        Object[] copy = new Object[4];
        s.copyTo(copy, 0, 0, copy.length);

        assertEquals(Boolean.TRUE, copy[0]);
        assertEquals(Boolean.FALSE, copy[1]);
        assertEquals(Boolean.FALSE, copy[2]);
        assertEquals(Boolean.TRUE, copy[3]);
    }

    @Test
    public void rangeBool() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);

        BooleanSeries range = s.rangeBool(2, 4);

        assertEquals(2, range.size());
        assertInstanceOf(BooleanBitsetSeries.class, range);
        assertFalse(range.getBool(0));
        assertTrue(range.getBool(1));
    }

    @Test
    public void selectBoolean() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
        BooleanBitsetSeries positions = new BooleanBitsetSeries(new long[]{0b0101L}, 4);

        BooleanSeries select = s.select(positions);

        assertEquals(2, select.size());
        assertTrue(select.get(0));
        assertFalse(select.get(1));
    }

    @Test
    public void firstTrue() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b0100L}, 4);
        assertEquals(2, s1.firstTrue());

        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b0000L, 0b1000_0000_0000_0000}, 100);
        assertEquals(79, s2.firstTrue());
    }

    @Test
    public void countTrue() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b0100L}, 4);
        assertEquals(1, s1.countTrue());

        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b1111L, 0b1001_0000_0010_0001}, 100);
        assertEquals(8, s2.countTrue());
    }

    @Test
    public void isTrue() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{
                0x00000000_000000FFL
        }, 4);
        assertTrue(s1.isTrue());

        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{
                0xFFFFFFFF_FFFFFFFFL,
                0x0000FFFF_FFFFFFFFL
        }, 100);
        assertTrue(s2.isTrue());

        BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{
                0xFFFFFFFF_FFFFFFFFL,
                0x0000FFFF_FFFF0FFFL
        }, 100);
        assertFalse(s3.isTrue());
    }

    @Test
    public void isFalse() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{
                0x00000000_00000000L
        }, 4);
        assertTrue(s1.isFalse());

        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{
                0x00000000_00000000L,
                0xFFFF0000_00000000L
        }, 100);
        assertTrue(s2.isFalse());

        BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{
                0x00000000_00000000L,
                0xFFFFF000_00000F00L
        }, 100);
        assertFalse(s3.isFalse());
    }

    @Test
    public void countFalse() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b0100L}, 4);
        assertEquals(3, s1.countFalse());

        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b1111L, 0b1001_0000_0010_0001}, 100);
        assertEquals(100 - 8, s2.countFalse());
    }

    @Test
    public void not() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1011L}, 4);
        BooleanBitsetSeries not = s.not();

        assertFalse(not.get(0));
        assertFalse(not.get(1));
        assertTrue(not.get(2));
        assertFalse(not.get(3));
        assertEquals(0b1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_1111_0100L,
                not.data[0]);
    }

    @Test
    public void and() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b1011L}, 4);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b0011L}, 4);
        BooleanSeries s3 = s1.and(s2);

        assertTrue(s3.get(0));
        assertTrue(s3.get(1));
        assertFalse(s3.get(2));
        assertFalse(s3.get(3));
    }

    @Test
    public void or() {
        BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b1011L}, 4);
        BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b0011L}, 4);
        BooleanSeries s3 = s1.or(s2);

        assertTrue(s3.get(0));
        assertTrue(s3.get(1));
        assertFalse(s3.get(2));
        assertTrue(s3.get(3));
    }

    @Test
    public void cumSum() {
        BooleanBitsetSeries s = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
        new SeriesAsserts(s.cumSum()).expectData(1, 1, 1, 2);
    }

    @Test
    public void concatBool() {
        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{0b1001_1001_1001L}, 12);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(12, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {

            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);
            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001_1001_1001L}, 76);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(76, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{0b1001L}, 4);

            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L,
                    0b00001001_10011111L
            }, 140);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(140, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, 0b1001}, 68);

            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_10011111L,
                    0b00001001_11111111L
            }, 204);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(68+68+68, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK, 0b1001}, 132);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK, 0b1001}, 132);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK, 0b1001}, 132);

            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_10011111L,
                    0b11111111_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b00001001_11111111L
            }, 396);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(132+132+132, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{0b00001111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L}, 60);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{0b00001111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L}, 60);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{0b00001111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L}, 60);

            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{
                    0b10011111_11111111_11111111_11111111_11111111_11111111_11111111_11111001L,
                    0b11111001_11111111_11111111_11111111_11111111_11111111_11111111_11111111L,
                    0b00000000_00001111_11111111_11111111_11111111_11111111_11111111_11111111L,
            }, 180);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(180, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }

        {
            BooleanBitsetSeries s1 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK}, 128);
            BooleanBitsetSeries s2 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK}, 128);
            BooleanBitsetSeries s3 = new BooleanBitsetSeries(new long[]{LONG_BITS_MASK, LONG_BITS_MASK}, 128);

            BooleanBitsetSeries concat = new BooleanBitsetSeries(new long[]{
                    LONG_BITS_MASK,
                    LONG_BITS_MASK,
                    LONG_BITS_MASK,
                    LONG_BITS_MASK,
                    LONG_BITS_MASK,
                    LONG_BITS_MASK
            }, 384);

            BooleanSeries booleans = s1.concatBool(s2, s3);
            assertEquals(384, booleans.size());
            assertTrue(concat.eq(booleans).isTrue());
        }
    }
}