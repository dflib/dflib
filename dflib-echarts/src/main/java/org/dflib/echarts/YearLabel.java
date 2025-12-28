package org.dflib.echarts;

import org.dflib.echarts.render.option.calendar.YearLabelModel;

/**
 * @since 2.0.0
 */
public class YearLabel {

    private Boolean show;
    private String color;

    public static YearLabel of() {
        return new YearLabel();
    }

    protected YearLabel() {
    }

    public YearLabel show(boolean show) {
        this.show = show;
        return this;
    }

    public YearLabel color(String color) {
        this.color = color;
        return this;
    }

    protected YearLabelModel resolve() {
        return new YearLabelModel(show, color);
    }
}
