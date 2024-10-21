package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class BarItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private int[] borderRadius;
    private Double opacity;

    public static BarItemStyle of() {
        return new BarItemStyle();
    }

    public BarItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public BarItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public BarItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public BarItemStyle borderRadius(int borderRadius) {
        this.borderRadius = new int[]{borderRadius, borderRadius, borderRadius, borderRadius};
        return this;
    }

    public BarItemStyle borderRadius(int upperLeft, int upperRight, int bottomRight, int bottomLeft) {
        this.borderRadius = new int[]{upperLeft, upperRight, bottomRight, bottomLeft};
        return this;
    }

    public BarItemStyle opacity(double opacity) {
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
                borderRadius,
                opacity);
    }
}
