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

    public ItemStyleModel(
            String color,
            String color0,
            String borderColor,
            String borderColor0,
            String borderColorDoji,
            Integer borderWidth) {

        this.borderColor0 = borderColor0;
        this.color = color;
        this.color0 = color0;
        this.borderColor = borderColor;
        this.borderColorDoji = borderColorDoji;
        this.borderWidth = borderWidth;
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
}
