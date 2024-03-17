package org.dflib;


import static org.dflib.Exp.$col;

/**
 * A user-defined function that produces an {@link Exp} based on three columnar arguments. The arguments can be either
 * expressions or DataFrame column references.
 *
 * @since 1.0.0-M20
 */
@FunctionalInterface
public interface Udf3<A1, A2, A3, R> {

    /**
     * Produces an expression based on the provided expressions. This should be the preferred (though also most verbose)
     * way to resolve a UDF if the type of any argument columns is of significance. I.e.
     * <code>call($int("a"), $col("b"), $col("c"))</code> will be faster than <code>call("a", "b", "c")</code>, if the
     * UDF converts the contents of "a" to ints internally.
     */
    Exp<R> call(Exp<A1> exp1, Exp<A2> exp2, Exp<A3> exp3);

    default Exp<R> call(String column1, String column2, String column3) {
        return call($col(column1), $col(column2), $col(column3));
    }

    default Exp<R> call(int columnPos1, int columnPos2, int columnPos3) {
        return call($col(columnPos1), $col(columnPos2), $col(columnPos3));
    }
}
