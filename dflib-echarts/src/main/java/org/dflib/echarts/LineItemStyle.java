package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class LineItemStyle {

    private String color;
    private Double opacity;

    public static LineItemStyle of() {
        return new LineItemStyle();
    }

    public LineItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public LineItemStyle opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    ItemStyleModel resolve() {

        return new ItemStyleModel(
                color,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                opacity);
    }
}
