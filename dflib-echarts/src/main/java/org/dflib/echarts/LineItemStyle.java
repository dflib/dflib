package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class LineItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private Double opacity;

    public static LineItemStyle of() {
        return new LineItemStyle();
    }

    public LineItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public LineItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public LineItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
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
                borderColor,
                null,
                null,
                borderWidth,
                null,
                opacity);
    }
}
