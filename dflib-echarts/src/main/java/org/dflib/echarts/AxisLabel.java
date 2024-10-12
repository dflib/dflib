package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisLabelModel;

public class AxisLabel {

    private String formatter;
    private Integer rotate;
    private Integer fontSize;
    private FontStyle fontStyle;
    private String fontWeight;
    private String fontFamily;

    public static AxisLabel of() {
        return new AxisLabel(null);
    }

    protected AxisLabel(String formatter) {
        this.formatter = formatter;
    }

    public AxisLabel formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    public AxisLabel rotate(int rotateDegrees) {
        // TODO: validate range -90..90
        this.rotate = rotateDegrees;
        return this;
    }

    public AxisLabel fontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    public AxisLabel fontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    public AxisLabel fontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight != null ? "'" + fontWeight + "'" : null;
        return this;
    }

    public AxisLabel fontWeight(int fontWeight) {
        this.fontWeight = String.valueOf(fontWeight);
        return this;
    }

    public AxisLabel fontFamily(String fontFamily) {
        this.fontFamily = fontFamily;
        return this;
    }

    protected AxisLabelModel resolve() {
        return new AxisLabelModel(
                formatter,
                rotate,
                fontSize,
                fontStyle != null ? fontStyle.name() : null,
                fontWeight,
                fontFamily);
    }
}
