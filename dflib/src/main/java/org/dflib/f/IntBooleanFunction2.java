package org.dflib.f;

/**
 * @since 2.0.0
 */
@FunctionalInterface
public interface IntBooleanFunction2<R> {

    R apply(int one, boolean two);
}
