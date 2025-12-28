package org.dflib.echarts.render.option.visualmap;

/**
 * @since 2.0.0
 */
public class VisualChannelsModel {

    private final String symbol;
    private final Integer symbolSize;
    private final String color;
    private final Double opacity;

    public VisualChannelsModel(String symbol, Integer symbolSize, String color, Double opacity) {
        this.symbol = symbol;
        this.symbolSize = symbolSize;
        this.color = color;
        this.opacity = opacity;
    }

    public String getSymbol() {
        return symbol;
    }

    public Integer getSymbolSize() {
        return symbolSize;
    }

    public String getColor() {
        return color;
    }

    public Double getOpacity() {
        return opacity;
    }
}
