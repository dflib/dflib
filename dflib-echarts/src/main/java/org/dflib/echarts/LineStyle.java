package org.dflib.echarts;

import org.dflib.echarts.render.option.series.LineStyleModel;

/**
 * @since 1.1.0
 */
public class LineStyle {

    private String color;
    private Integer width;
    private Double opacity;

    public static LineStyle of() {
        return new LineStyle();
    }

    public LineStyle color(String color) {
        this.color = color;
        return this;
    }

    public LineStyle width(int width) {
        this.width = width;
        return this;
    }

    public LineStyle opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    LineStyleModel resolve() {
        return new LineStyleModel(
                color,
                width,
                opacity);
    }
}
