package org.dflib;


import static org.dflib.Exp.$col;

/**
 * A user-defined function that produces an {@link Exp} based on a single columnar argument. The argument can be either
 * itself an expression or a DataFrame column reference.
 *
 * @since 1.0.0-M20
 */
@FunctionalInterface
public interface Udf1<A, R> {

    Exp<R> call(Exp<A> exp);

    default Exp<R> call(String column) {
        return call($col(column));
    }

    default Exp<R> call(int columnPos) {
        return call($col(columnPos));
    }
}
