package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisLabelModel;

/**
 * @since 1.0.0-M21
 */
public class AxisLabel {

    private String formatter;
    private Integer rotate;
    private Integer fontSize;
    private FontStyle fontStyle;
    private String fontWeight;
    private String fontFamily;

    /**
     * @since 1.0.0-M22
     */
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

    /**
     * @since 1.0.0-M23
     */
    public AxisLabel rotate(int rotateDegrees) {
        // TODO: validate range -90..90
        this.rotate = rotateDegrees;
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public AxisLabel fontSize(int fontSize) {
        this.fontSize = fontSize;
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public AxisLabel fontStyle(FontStyle fontStyle) {
        this.fontStyle = fontStyle;
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public AxisLabel fontWeight(FontWeight fontWeight) {
        this.fontWeight = fontWeight != null ? "'" + fontWeight + "'" : null;
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
    public AxisLabel fontWeight(int fontWeight) {
        this.fontWeight = String.valueOf(fontWeight);
        return this;
    }

    /**
     * @since 1.0.0-M23
     */
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
