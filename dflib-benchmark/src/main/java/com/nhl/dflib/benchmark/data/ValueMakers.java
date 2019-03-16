package com.nhl.dflib.benchmark.data;

import java.util.Random;
import java.util.function.Supplier;

public interface ValueMakers {

    static Supplier<Object> intSequence() {
        int[] val = new int[1];
        return () -> val[0]++;
    }

    static Supplier<Object> randomIntSequence(int max) {
        Random random = new Random();
        return () -> random.nextInt(max);
    }


    static Supplier<Object> stringSequence() {
        int[] val = new int[1];
        return () -> "data_" + val[0]++;
    }

    static Supplier<Object> constStringSequence(String string) {
        return () -> string;
    }
}
