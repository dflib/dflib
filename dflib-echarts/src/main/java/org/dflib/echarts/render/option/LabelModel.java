package org.dflib.echarts.render.option;

/**
 * @since 1.0.0-M22
 */
public class LabelModel {

    private final String position;
    private final String formatter;

    public LabelModel(String formatter, String position) {
        this.formatter = formatter;
        this.position = position;
    }

    public String getPosition() {
        return position;
    }

    public String getFormatter() {
        return formatter;
    }
}
