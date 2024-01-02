package org.dflib.f;

/**
 * @since 1.0.0-M19
 */
@FunctionalInterface
public interface Function3<One, Two, Three, R> {

    R apply(One one, Two two, Three three);
}
