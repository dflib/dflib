package org.dflib;


import static org.dflib.Exp.$col;

/**
 * A user-defined function that produces an {@link Exp} based on a variable number of columnar arguments. The arguments
 * can be either expressions or DataFrame column references.
 *
 * @since 1.0.0-M20
 */
@FunctionalInterface
public interface UdfN<R> {

    /**
     * Produces an expression based on the provided expressions. This should be the preferred (though also most verbose)
     * way to resolve a UDF if the type of any argument columns is of significance. I.e.
     * <code>call($int("a"), $col("b"))</code> will be faster than <code>call("a", "b")</code>, if the UDF converts
     * the contents of "a" to ints internally.
     */
    Exp<R> call(Exp<?>... exps);

    default Exp<R> call(String... columns) {
        int len = columns.length;
        Exp<?>[] exps = new Exp[len];
        for (int i = 0; i < len; i++) {
            exps[i] = $col(columns[i]);
        }
        return call(exps);
    }

    default Exp<R> call(int... columnPos) {
        int len = columnPos.length;
        Exp<?>[] exps = new Exp[len];
        for (int i = 0; i < len; i++) {
            exps[i] = $col(columnPos[i]);
        }
        return call(exps);
    }
}
