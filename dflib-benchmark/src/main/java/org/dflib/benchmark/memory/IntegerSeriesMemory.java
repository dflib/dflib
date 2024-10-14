package org.dflib.benchmark.memory;

import org.dflib.IntSeries;
import org.dflib.Series;
import org.dflib.benchmark.memory.benchmark.MemoryTest;
import org.dflib.series.ArraySeries;
import org.dflib.series.FixedSizeBitSet;
import org.dflib.series.IntArraySeries;
import org.dflib.series.IntegerSeries;

import java.util.Random;

public class IntegerSeriesMemory extends MemoryTest {

    private static final int ROWS = 5_000_000;

    public static void main(String[] args) {

        IntegerSeriesMemory test = new IntegerSeriesMemory();

        test.run("    int Series           ", test::getIntSeries, ROWS);
        test.run(" Object Series   0% nulls", test::getObjectSeries, ROWS);
        test.run(" Object Series  25% nulls", test::getObjectSeries25PercentNulls, ROWS);
        test.run(" Object Series  75% nulls", test::getObjectSeries75PercentNulls, ROWS);
        test.run(" Object Series 100% nulls", test::getObjectSeries100PercentNulls, ROWS);
        test.run("Integer Series   0% nulls", test::getIntegerSeries, ROWS);
        test.run("Integer Series  25% nulls", test::getIntegerSeries25PercentNulls, ROWS);
        test.run("Integer Series  75% nulls", test::getIntegerSeries75PercentNulls, ROWS);
        test.run("Integer Series 100% nulls", test::getIntegerSeries100PercentNulls, ROWS);
    }

    public IntSeries getIntSeries() {
        int[] data = new int[ROWS];
        for(int i=0; i<ROWS; i++) {
            data[i] = i;
        }
        return new IntArraySeries(data);
    }

    public Series<Integer> getObjectSeries() {
        Integer[] data = new Integer[ROWS];
        for(int i=0; i<ROWS; i++) {
            data[i] = i;
        }
        return new ArraySeries<>(data);
    }

    public IntegerSeries getIntegerSeries() {
        int[] data = new int[ROWS];
        FixedSizeBitSet nulls = new FixedSizeBitSet(ROWS);
        for(int i=0; i<ROWS; i++) {
            data[i] = i;
        }
        return new IntegerSeries(data, nulls);
    }

    public Series<Integer> getObjectSeries25PercentNulls() {
        Random random = new Random();
        Integer[] data = new Integer[ROWS];
        for(int i=0; i<ROWS; i++) {
            if(random.nextDouble() < 0.25) {
                data[i] = null;
            } else {
                data[i] = i;
            }
        }
        return new ArraySeries<>(data);
    }

    public IntegerSeries getIntegerSeries25PercentNulls() {
        Random random = new Random();
        int[] data = new int[ROWS];
        FixedSizeBitSet nulls = new FixedSizeBitSet(ROWS);
        for(int i=0; i<ROWS; i++) {
            if(random.nextDouble() < 0.25) {
                nulls.set(i);
            } else {
                data[i] = i;
            }
        }
        return new IntegerSeries(data, nulls);
    }

    public Series<Integer> getObjectSeries75PercentNulls() {
        Random random = new Random();
        Integer[] data = new Integer[ROWS];
        for(int i=0; i<ROWS; i++) {
            if(random.nextDouble() < 0.75) {
                data[i] = null;
            } else {
                data[i] = i;
            }
        }
        return new ArraySeries<>(data);
    }

    public IntegerSeries getIntegerSeries75PercentNulls() {
        Random random = new Random();
        int[] data = new int[ROWS];
        FixedSizeBitSet nulls = new FixedSizeBitSet(ROWS);
        for(int i=0; i<ROWS; i++) {
            if(random.nextDouble() < 0.75) {
                nulls.set(i);
            } else {
                data[i] = i;
            }
        }
        return new IntegerSeries(data, nulls);
    }

    public Series<Integer> getObjectSeries100PercentNulls() {
        Integer[] data = new Integer[ROWS];
        return new ArraySeries<>(data);
    }

    public IntegerSeries getIntegerSeries100PercentNulls() {
        Random random = new Random();
        int[] data = new int[ROWS];
        FixedSizeBitSet nulls = new FixedSizeBitSet(ROWS);
        for(int i=0; i<ROWS; i++) {
            if(random.nextDouble() < 0.75) {
                nulls.set(i);
            }
        }
        return new IntegerSeries(data, nulls);
    }
}

/*
  Primitive int Series     4.11

// with usage of ValueMaker
Object Series no nulls    20.20
Object Series 25% nulls   16.19
Object Series 75% nulls    8.20

Integer Series no nulls   12.58
Integer Series 25% nulls  12.58
Integer Series 75% nulls  12.58

// direct series creation
    int Series              4.11

 Object Series   0% nulls  20.20
 Object Series  25% nulls  16.23
 Object Series  75% nulls   8.27
 Object Series 100% nulls   4.19

Integer Series   0% nulls   4.32
Integer Series  25% nulls   4.32
Integer Series  75% nulls   4.32
Integer Series 100% nulls   4.32
 */