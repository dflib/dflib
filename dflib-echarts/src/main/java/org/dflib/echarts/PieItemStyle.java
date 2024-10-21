package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class PieItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private Double opacity;

    public static PieItemStyle of() {
        return new PieItemStyle();
    }

    public PieItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public PieItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public PieItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public PieItemStyle opacity(double opacity) {
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

                // TODO: "borderRadius" is supported by Pie, but it has different structure compared to "bar"
                null,
                opacity);
    }
}
