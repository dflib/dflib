package org.dflib.echarts.render.option.axis;

/**
 * @since 1.0.0-M22
 */
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
