package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class CandlestickItemStyle {

    private String color;
    private String color0;
    private String borderColor;
    private String borderColor0;
    private String borderColorDoji;
    private Integer borderWidth;

    public static CandlestickItemStyle of() {
        return new CandlestickItemStyle();
    }

    public CandlestickItemStyle color(String color) {
        this.color = color;
        return this;
    }

    public CandlestickItemStyle color0(String color0) {
        this.color0 = color0;
        return this;
    }

    public CandlestickItemStyle borderColor(String borderColor) {
        this.borderColor = borderColor;
        return this;
    }

    public CandlestickItemStyle borderColor0(String borderColor0) {
        this.borderColor0 = borderColor0;
        return this;
    }

    public CandlestickItemStyle borderColorDoji(String borderColorDoji) {
        this.borderColorDoji = borderColorDoji;
        return this;
    }

    public CandlestickItemStyle borderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
        return this;
    }

    ItemStyleModel resolve() {
        return new ItemStyleModel(
                color,
                color0,
                borderColor,
                borderColor0,
                borderColorDoji,
                borderWidth,
                null);
    }
}
