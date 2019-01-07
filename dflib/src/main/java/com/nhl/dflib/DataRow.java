package com.nhl.dflib;

public interface DataRow {

    /**
     * Syntactic sugar for array construction. Equivalent to "new Object[] {x, y, z}".
     *
     * @param values a varargs array of values
     * @return "values" vararg unchanged
     */
    static Object[] row(Object... values) {
        return values;
    }
}
