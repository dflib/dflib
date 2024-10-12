package org.dflib.f;

@FunctionalInterface
public interface IntObjectFunction2<Two, R> {

    R apply(int one, Two two);
}
