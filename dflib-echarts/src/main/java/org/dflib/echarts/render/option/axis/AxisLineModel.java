package org.dflib.echarts.render.option.axis;

public class AxisLineModel {

    private final boolean show;
    private final Boolean onZero;

    public AxisLineModel(boolean show, Boolean onZero) {
        this.show = show;
        this.onZero = onZero;
    }

    public boolean isShow() {
        return show;
    }

    public boolean isOnZeroPresent() {
        return onZero != null;
    }

    public Boolean getOnZero() {
        return onZero;
    }
}
