package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class BoxplotItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private LineType borderType;
    private Double opacity;

    public static BoxplotItemStyle of() {
        return new BoxplotItemStyle();
    }

    public BoxplotItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public BoxplotItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public BoxplotItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    public BoxplotItemStyle borderType(LineType borderType) {
        this.borderType = borderType;
        return this;
    }

    public BoxplotItemStyle opacity(double opacity) {
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
                borderType != null ? borderType.name() : null,
                opacity);
    }
}
