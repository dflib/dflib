package org.dflib.echarts.render.option.calendar;

/**
 * @since 2.0.0
 */
public class YearLabelModel {

    private final Boolean show;
    private final String color;

    public YearLabelModel(Boolean show, String color) {
        this.show = show;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public boolean isShowPresent() {
        return show != null;
    }

    public Boolean getShow() {
        return show;
    }
}
