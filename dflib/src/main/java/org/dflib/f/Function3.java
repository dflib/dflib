package org.dflib.f;

@FunctionalInterface
public interface Function3<One, Two, Three, R> {

    R apply(One one, Two two, Three three);
}
