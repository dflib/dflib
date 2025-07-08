package org.dflib.echarts;

/**
 * Represents either a fixed or dynamic symbol size on a graph.
 *
 * @since 2.0.0
 */
public class SymbolSize {

    public static SymbolSize of(int size) {
        return new SymbolSize(size, null);
    }

    /**
     * Binds symbol size to a data column, so the symbols are sized dynamically, essentially providing an
     * extra visual dimension on the graph.
     */
    public static SymbolSize ofDataColumn(String dataColumn) {
        return new SymbolSize(null, dataColumn);
    }

    static String resolveAsFunction(Integer symbolSizeDimension) {
        return "function (val) { return val[" + symbolSizeDimension + "]; }";
    }

    static String resolveAsFixedSize(Integer symbolSize) {
        return symbolSize != null ? String.valueOf(symbolSize) : null;
    }

    Integer symbolSize;
    String symbolSizeColumn;

    protected SymbolSize(Integer symbolSize, String symbolSizeColumn) {
        this.symbolSize = symbolSize;
        this.symbolSizeColumn = symbolSizeColumn;
    }
}
