package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class ScatterItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private Double opacity;

    public static ScatterItemStyle of() {
        return new ScatterItemStyle();
    }

    public ScatterItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public ScatterItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public ScatterItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public ScatterItemStyle opacity(double opacity) {
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
                null,
                opacity);
    }
}
