package org.dflib.f;

/**
 * @since 1.0.0-M19
 */
@FunctionalInterface
public interface IntObjectFunction2<Two, R> {

    R apply(int one, Two two);
}
