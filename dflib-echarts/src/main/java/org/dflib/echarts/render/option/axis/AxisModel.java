package org.dflib.echarts.render.option.axis;

import org.dflib.echarts.render.util.Renderer;

public class AxisModel {

    private final Boolean alignTicks;
    private final Integer gridIndex;
    private final String name;
    private final Integer offset;
    private final String position;
    private final String type;
    private final AxisLabelModel axisLabel;
    private final boolean boundaryGap;
    private final AxisLineModel axisLine;
    private final Boolean scale;

    public AxisModel(
            Boolean alignTicks,
            Integer gridIndex,
            String name,
            Integer offset,
            String position,
            String type,
            AxisLabelModel axisLabel,
            AxisLineModel axisLine,
            boolean boundaryGap,
            Boolean scale) {

        this.alignTicks = alignTicks;
        this.gridIndex = gridIndex;
        this.name = name;
        this.offset = offset;
        this.position = position;
        this.type = type;
        this.axisLabel = axisLabel;
        this.axisLine = axisLine;
        this.boundaryGap = boundaryGap;
        this.scale = scale;
    }

    public boolean isAlignTicksPresent() {
        return alignTicks != null;
    }

    public Boolean getAlignTicks() {
        return alignTicks;
    }

    public Integer getGridIndex() {
        return gridIndex;
    }

    public String getName() {
        return name;
    }

    public String getQuotedName() {
        return Renderer.quotedValue(name);
    }

    public String getPosition() {
        return position;
    }

    public Integer getOffset() {
        return offset;
    }

    public String getType() {
        return type;
    }

    public AxisLabelModel getAxisLabel() {
        return axisLabel;
    }

    public AxisLineModel getAxisLine() {
        return axisLine;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public boolean isNoBoundaryGap() {
        return !boundaryGap;
    }

    /**
     * @since 2.0.0
     */
    public boolean isScalePresent() {
        return scale != null;
    }

    /**
     * @since 2.0.0
     */
    public Boolean getScale() {
        return scale;
    }
}
