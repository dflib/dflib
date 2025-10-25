package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class PieItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private Distance borderRadius;
    private LineType borderType;
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

    // TODO: there are more borderRadius options availble for Pie:
    //  https://echarts.apache.org/en/option.html#series-pie.itemStyle.borderRadius

    public PieItemStyle borderRadiusPx(int radius) {
        this.borderRadius = Distance.ofPx(radius);
        return this;
    }

    public PieItemStyle borderRadiusPct(double percent) {
        this.borderRadius = Distance.ofPct(percent);
        return this;
    }

    public PieItemStyle borderType(LineType borderType) {
        this.borderType = borderType;
        return this;
    }

    public PieItemStyle opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    ItemStyleModel resolve() {
        return new ItemStyleModel(
                color != null ? "'" + color + "'" : null,
                null,
                borderColor,
                null,
                null,
                borderWidth,
                borderRadius != null ? borderRadius.asString() : null,
                borderType != null ? borderType.name() : null,
                opacity);
    }
}
