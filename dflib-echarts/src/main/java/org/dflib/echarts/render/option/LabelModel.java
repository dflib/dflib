package org.dflib.echarts.render.option;

public class LabelModel {

    private final Boolean show;
    private final String position;
    private final String formatter;

    public LabelModel(Boolean show, String formatter, String position) {
        this.show = show;
        this.formatter = formatter;
        this.position = position;
    }

    public boolean isShowPresent() {
        return show != null;
    }

    public Boolean getShow() {
        return show;
    }

    public String getPosition() {
        return position;
    }

    public String getFormatter() {
        return formatter;
    }
}
