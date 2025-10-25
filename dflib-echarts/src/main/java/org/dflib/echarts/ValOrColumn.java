package org.dflib.echarts;

/**
 * Represents either a fixed value on a graph or a value based on a column position.
 *
 * @since 2.0.0
 */
public class ValOrColumn<T> {

    public static <T> ValOrColumn<T> ofVal(T val) {
        return new ValOrColumn<>(val, null);
    }

    /**
     * Binds a value to a data column, so the values are produced dynamically, essentially providing an
     * extra visual dimension on the graph.
     */
    public static <T> ValOrColumn<T> ofDataColumn(String dataColumn) {
        return new ValOrColumn<>(null, dataColumn);
    }

    static String resolveAsFunction(Integer dimension) {
        return "function (val) { return val[" + dimension + "]; }";
    }

    static <T> String resolveAsVal(T val) {
        return val != null ? String.valueOf(val) : null;
    }

    T val;
    String column;

    protected ValOrColumn(T val, String column) {
        this.val = val;
        this.column = column;
    }
}
