package com.nhl.dflib.benchmark;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.*;

import java.util.Random;

/**
 * Series data generator for DFLib benchmarks.
 *
 * @param <T>
 */
@FunctionalInterface
public interface ValueMaker<T> {

    static ValueMaker<Integer> nullSeq() {
        return () -> null;
    }

    static ValueMaker<Integer> intSeq() {
        int[] val = new int[1];
        return () -> val[0]++;
    }

    static ValueMaker<Double> doubleSeq() {
        double[] val = new double[1];
        val[0] = 0.01;
        return () -> val[0]++;
    }

    static ValueMaker<Double> randomDoubleSeq() {
        Random random = new Random();
        return () -> random.nextDouble();
    }

    static ValueMaker<Long> longSeq() {
        long[] val = new long[1];
        return () -> val[0]++;
    }

    static ValueMaker<Boolean> booleanSeq() {
        int[] val = new int[1];
        return () -> val[0]++ % 2 == 0;
    }

    static ValueMaker<Integer> randomIntSeq(int max) {
        Random random = new Random();
        return () -> random.nextInt(max);
    }

    static ValueMaker<Integer> intSeq(int from, int to) {
        int[] val = new int[1];
        val[0] = from;
        return () -> {
            int result = val[0];
            // cyclic function
            val[0] = result < to ? result + 1 : from;
            return result;
        };
    }

    static ValueMaker<String> stringSeq() {
        int[] val = new int[1];
        return () -> "data_" + val[0]++;
    }

    static ValueMaker<String> constStringSeq(String string) {
        return () -> string;
    }

    static ValueMaker<String> semiRandomStringSeq(String prefix, int max) {
        Random random = new Random();
        return () -> prefix + random.nextInt(max);
    }

    static <T extends Enum<T>> ValueMaker<T> enumSeq(Class<T> type) {
        ValueMaker<Integer> intSeq = intSeq();
        T[] allValues = type.getEnumConstants();
        int len = allValues.length;
        return () -> allValues[intSeq.get() % len];
    }

    static <T extends Enum<T>> ValueMaker<Integer> enumOrdinalsSeq(Class<T> type) {
        ValueMaker<Integer> intSeq = intSeq();
        int len = type.getEnumConstants().length;
        return () -> intSeq.get() % len;
    }

    T get();

    default Series<T> series(int len) {
        ObjectAccumulator ml = new ObjectAccumulator<>(len);

        for (int j = 0; j < len; j++) {
            ml.push(get());
        }

        return ml.toSeries().materialize();
    }

    default BooleanSeries booleanSeries(int len) {

        BooleanAccumulator vals = new BooleanAccumulator(len);

        for (int i = 0; i < len; i++) {
            vals.push((Boolean) get());
        }

        return vals.toSeries();
    }

    default IntSeries intSeries(int len) {

        IntAccumulator ints = new IntAccumulator(len);

        for (int i = 0; i < len; i++) {
            ints.push((Integer) get());
        }

        return ints.toSeries();
    }

    default LongSeries longSeries(int len) {

        LongAccumulator vals = new LongAccumulator(len);

        for (int i = 0; i < len; i++) {
            vals.push((Long) get());
        }

        return vals.toSeries();
    }

    default DoubleSeries doubleSeries(int len) {

        DoubleAccumulator ds = new DoubleAccumulator(len);

        for (int i = 0; i < len; i++) {
            ds.push((Double) get());
        }

        return ds.toSeries();
    }
}
