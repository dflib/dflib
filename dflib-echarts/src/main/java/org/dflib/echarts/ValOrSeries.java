package org.dflib.echarts;

/**
 * Represents either a fixed value on a graph or a reference to Series (DataFrame column).
 */
class ValOrSeries<T> {

    public static <T> ValOrSeries<T> ofVal(T val) {
        return new ValOrSeries<>(val, null);
    }

    /**
     * Binds a value to a DataFrame column, so the values are produced dynamically, providing an extra visual dimension
     * on the graph.
     */
    public static <T> ValOrSeries<T> ofSeries(String seriesName) {
        return new ValOrSeries<>(null, seriesName);
    }

    public static String jsFunctionWithArrayParam(Integer dimension) {
        return "function (vals) { return vals[" + dimension + "]; }";
    }

    public static String jsFunctionWithObjectParam(Integer dimension) {
        return "function (o) { return o.data[" + dimension + "]; }";
    }

    private T val;
    String seriesName;

    protected ValOrSeries(T val, String seriesName) {
        this.val = val;
        this.seriesName = seriesName;
    }

    public boolean isSeries() {
        return seriesName != null;
    }

    public boolean isVal() {
        return val != null;
    }

    public String valString() {
        return val != null ? String.valueOf(val) : null;
    }

    public String valQuotedString() {
        return val != null ? "'" + val + "'" : null;
    }
}
