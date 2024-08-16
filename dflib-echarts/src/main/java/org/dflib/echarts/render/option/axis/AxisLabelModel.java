package org.dflib.echarts.render.option.axis;

/**
 * @since 1.0.0-M21
 */
public class AxisLabelModel {

    private final String formatter;
    private final Integer rotate;
    private final Integer fontSize;
    private final String fontStyle;
    private final String fontWeight;
    private final String fontFamily;

    public AxisLabelModel(
            String formatter,
            Integer rotate,
            Integer fontSize,
            String fontStyle,
            String fontWeight,
            String fontFamily) {

        this.formatter = formatter;
        this.rotate = rotate;
        this.fontSize = fontSize;
        this.fontStyle = fontStyle;
        this.fontWeight = fontWeight;
        this.fontFamily = fontFamily;
    }

    public String getFormatter() {
        return formatter;
    }

    public Integer getRotate() {
        return rotate;
    }

    public Integer getFontSize() {
        return fontSize;
    }

    public String getFontStyle() {
        return fontStyle;
    }

    public String getFontWeight() {
        // note that the value can be an unquoted number or an already-quoted String
        return fontWeight;
    }

    public String getFontFamily() {
        return fontFamily;
    }
}
