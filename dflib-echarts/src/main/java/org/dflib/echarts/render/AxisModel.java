package org.dflib.echarts.render;

/**
 * @since 1.0.0-M21
 */
public class AxisModel {

    private final String type;
    private final AxisLabelModel axisLabel;
    private final boolean boundaryGap;

    public AxisModel(String type, AxisLabelModel axisLabel, boolean boundaryGap) {
        this.type = type;
        this.axisLabel = axisLabel;
        this.boundaryGap = boundaryGap;
    }

    public String getType() {
        return type;
    }

    public AxisLabelModel getAxisLabel() {
        return axisLabel;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public boolean isNoBoundaryGap() {
        return !boundaryGap;
    }
}
