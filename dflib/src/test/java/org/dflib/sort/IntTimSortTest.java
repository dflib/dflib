package org.dflib.sort;

import org.junit.jupiter.api.Test;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public class IntTimSortTest {

    private Random random = new Random();
    private IntComparator reverseSort = IntTimSortTest::reverseCompare;

    public static int reverseCompare(int x, int y) {
        return (x < y) ? 1 : ((x == y) ? 0 : -1);
    }

    private int[] randomInts(int len) {
        int[] ints = new int[len];
        for (int i = 0; i < len; i++) {
            ints[i] = random.nextInt();
        }

        return ints;
    }

    @Test
    public void sort() {
        int[] ints = {1, 2, 3, 4, 5};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{5, 4, 3, 2, 1}, ints);
    }

    @Test
    public void sort_Negative() {
        int[] ints = {1, -2, 3, 4, -5};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{4, 3, 1, -2, -5}, ints);
    }

    @Test
    public void sort_Large() {
        int[] ints = {Integer.MIN_VALUE, -2123259216, 0, 2119160956, Integer.MAX_VALUE};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{Integer.MAX_VALUE, 2119160956, 0, -2123259216, Integer.MIN_VALUE}, ints);
    }

    @Test
    public void sort_Dupes() {
        int[] ints = {5, 2, 2, 4, 5};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{5, 5, 4, 2, 2}, ints);
    }

    @Test
    public void sort_AlreadySorted() {
        int[] ints = {5, 4, 3, 2, 1};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{5, 4, 3, 2, 1}, ints);
    }

    @Test
    public void sort_AboveMergeThreshold() {
        int[] ints = {1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 18, 19, 20, 21, 22, 23, 24, 25, 26, 27, 28, 29, 30, 31, 32, 33};
        IntTimSort.sort(ints, reverseSort);
        assertArrayEquals(new int[]{33, 32, 31, 30, 29, 28, 27, 26, 25, 24, 23, 22, 21, 20, 19, 18, 17, 16, 15, 14, 13, 12, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1}, ints);
    }

    @Test
    public void sort_Random() {

        int[] ints = randomInts(15);
        IntTimSort.sort(ints, reverseSort);

        for (int i = 1; i < ints.length; i++) {
            assertTrue(ints[i - 1] >= ints[i], "Failed at " + i + ": " + ints[i - 1] + " vs " + ints[i]);
        }
    }

    @Test
    public void sort_Random_AboveMergeThreshold() {

        int[] ints = randomInts(1000);
        IntTimSort.sort(ints, reverseSort);

        for (int i = 1; i < ints.length; i++) {
            assertTrue(ints[i - 1] >= ints[i], "Failed at " + i + ": " + ints[i - 1] + " vs " + ints[i]);
        }
    }

    @Test
    public void sort_WithRefComparator() {

        String[] strings = {"x", "y", "z", "a", "x"};
        IntComparator comparator = (i1, i2) -> strings[i1 - 1].compareTo(strings[i2 - 1]);

        int[] ints = {1, 2, 3, 4, 5};
        IntTimSort.sort(ints, comparator);
        assertArrayEquals(new int[]{4, 1, 5, 2, 3}, ints);
    }

}
