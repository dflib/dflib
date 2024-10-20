package org.dflib.benchmark;

import org.dflib.BooleanSeries;
import org.dflib.DoubleSeries;
import org.dflib.FloatSeries;
import org.dflib.IntSeries;
import org.dflib.LongSeries;
import org.dflib.Series;
import org.dflib.builder.BoolAccum;
import org.dflib.builder.DoubleAccum;
import org.dflib.builder.FloatAccum;
import org.dflib.builder.IntAccum;
import org.dflib.builder.LongAccum;
import org.dflib.builder.ObjectAccum;

import java.util.Random;
import java.util.function.UnaryOperator;

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

    static ValueMaker<Integer> reverseIntSeq() {
        int[] val = new int[]{Integer.MAX_VALUE};
        return () -> val[0]--;
    }

    static ValueMaker<Float> floatSeq() {
        float[] val = new float[1];
        val[0] = 0.01f;
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

    static ValueMaker<Long> randomLongSeq() {
        Random random = new Random();
        return () -> random.nextLong();
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

    static <T> ValueMaker<T> constSeq(T t) {
        return constSeq(t, lt -> lt);
    }

    static <T> ValueMaker<T> constSeq(T t, UnaryOperator<T> cloneFunction) {
        return () -> cloneFunction.apply(t);
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
        ObjectAccum ml = new ObjectAccum<>(len);

        for (int j = 0; j < len; j++) {
            ml.push(get());
        }

        return ml.toSeries().materialize();
    }

    default BooleanSeries booleanSeries(int len) {

        BoolAccum vals = new BoolAccum(len);

        for (int i = 0; i < len; i++) {
            vals.push((Boolean) get());
        }

        return vals.toSeries();
    }

    default IntSeries intSeries(int len) {

        IntAccum ints = new IntAccum(len);

        for (int i = 0; i < len; i++) {
            ints.push((Integer) get());
        }

        return ints.toSeries();
    }

    default LongSeries longSeries(int len) {

        LongAccum vals = new LongAccum(len);

        for (int i = 0; i < len; i++) {
            vals.push((Long) get());
        }

        return vals.toSeries();
    }

    default FloatSeries floatSeries(int len) {

        FloatAccum ds = new FloatAccum(len);

        for (int i = 0; i < len; i++) {
            ds.push((Float) get());
        }

        return ds.toSeries();
    }

    default DoubleSeries doubleSeries(int len) {

        DoubleAccum ds = new DoubleAccum(len);

        for (int i = 0; i < len; i++) {
            ds.push((Double) get());
        }

        return ds.toSeries();
    }
}
