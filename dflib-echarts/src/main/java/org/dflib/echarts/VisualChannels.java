package org.dflib.echarts;

import org.dflib.echarts.render.option.visualmap.VisualChannelsModel;

/**
 * @since 2.0.0
 */
public class VisualChannels {

    private Symbol symbol;
    private Integer symbolSize;
    private String color;
    private Double opacity;

    public static VisualChannels of() {
        return new VisualChannels();
    }

    protected VisualChannels() {
    }

    public VisualChannels symbol(Symbol symbol) {
        this.symbol = symbol;
        return this;
    }

    public VisualChannels symbolSize(int symbolSize) {
        this.symbolSize = symbolSize;
        return this;
    }

    public VisualChannels color(String color) {
        this.color = color;
        return this;
    }

    public VisualChannels opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    protected VisualChannelsModel resolve() {
        return new VisualChannelsModel(
                symbol != null ? symbol.name() : null,
                symbolSize,
                color,
                opacity
        );
    }
}
