package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * @since 1.1.0
 */
public class BarItemStyle {

    private String color;
    private String borderColor;
    private Integer borderWidth;
    private LineType borderType;
    private Integer singleBorderRadius;
    private int[] fourBorderRadius;
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
        this.singleBorderRadius = borderRadius;
        this.fourBorderRadius = null;
        return this;
    }

    public BarItemStyle borderRadius(int upperLeft, int upperRight, int bottomRight, int bottomLeft) {
        this.singleBorderRadius = null;
        this.fourBorderRadius = new int[]{upperLeft, upperRight, bottomRight, bottomLeft};
        return this;
    }

    public BarItemStyle borderType(LineType borderType) {
        this.borderType = borderType;
        return this;
    }

    public BarItemStyle opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    ItemStyleModel resolve() {

        String borderRadius = singleBorderRadius != null
                ? String.valueOf(singleBorderRadius)
                : (fourBorderRadius != null ? IntStream.of(fourBorderRadius).mapToObj(String::valueOf).collect(Collectors.joining(",", "[", "]")) : null);

        return new ItemStyleModel(
                color,
                null,
                borderColor,
                null,
                null,
                borderWidth,
                borderRadius,
                borderType != null ? borderType.name() : null,
                opacity);
    }
}
