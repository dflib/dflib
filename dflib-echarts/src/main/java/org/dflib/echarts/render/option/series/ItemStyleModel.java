package org.dflib.echarts.render.option.series;

/**
 * @since 1.1.0
 */
public class ItemStyleModel {

    private final String color;
    private final String color0;
    private final String borderColor;
    private final String borderColor0;
    private final String borderColorDoji;
    private final Integer borderWidth;
    private final String borderRadius;
    private final Double opacity;

    public ItemStyleModel(
            String color,
            String color0,
            String borderColor,
            String borderColor0,
            String borderColorDoji,
            Integer borderWidth,
            String borderRadius,
            Double opacity) {

        this.borderColor0 = borderColor0;
        this.color = color;
        this.color0 = color0;
        this.borderColor = borderColor;
        this.borderColorDoji = borderColorDoji;
        this.borderWidth = borderWidth;
        this.borderRadius = borderRadius;

        this.opacity = opacity;
    }

    public String getColor() {
        return color;
    }

    public String getBorderColor0() {
        return borderColor0;
    }

    public String getBorderColor() {
        return borderColor;
    }

    public String getBorderColorDoji() {
        return borderColorDoji;
    }

    public Integer getBorderWidth() {
        return borderWidth;
    }

    public String getColor0() {
        return color0;
    }

    public String getBorderRadius() {
        return borderRadius;
    }

    public Double getOpacity() {
        return opacity;
    }
}
