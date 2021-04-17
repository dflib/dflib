package com.nhl.dflib.benchmark;

import com.nhl.dflib.Series;
import com.nhl.dflib.accumulator.ObjectAccumulator;

import java.util.Random;

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
            ml.add(get());
        }

        return ml.toSeries().materialize();
    }
}
