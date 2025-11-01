package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

public class PieSeriesOpts extends SeriesOpts<PieSeriesOpts> implements SeriesOptsNamedItems {

    Distance[] radius;
    Distance[] center;
    ColumnLinkedPieLabel label;
    Integer startAngle;
    Integer endAngle;
    RoseType roseType;
    PieItemStyle itemStyle;

    @Override
    public ChartType getType() {
        return ChartType.pie;
    }

    @Override
    public CoordinateSystemType getCoordinateSystemType() {
        // TODO: pie seems to also support "geo" and "calendar"
        return CoordinateSystemType.none;
    }

    @Override
    public String getItemNameSeries() {
        return label != null ? label.columnName : null;
    }

    public PieSeriesOpts label(String labelColumn) {
        this.label = new ColumnLinkedPieLabel(labelColumn, null);
        return this;
    }

    public PieSeriesOpts label(String labelColumn, PieLabel label) {
        this.label = new ColumnLinkedPieLabel(labelColumn, label);
        return this;
    }

    public PieSeriesOpts label(PieLabel label) {
        this.label = new ColumnLinkedPieLabel(null, label);
        return this;
    }

    public PieSeriesOpts radiusPx(int pixels) {
        this.radius = new Distance[]{Distance.ofPx(pixels)};
        return this;
    }

    public PieSeriesOpts radiusPx(int inner, int outer) {
        this.radius = new Distance[]{Distance.ofPx(inner), Distance.ofPx(outer)};
        return this;
    }

    public PieSeriesOpts radiusPct(double percent) {
        this.radius = new Distance[]{Distance.ofPct(percent)};
        return this;
    }

    public PieSeriesOpts radiusPct(double inner, double outer) {
        this.radius = new Distance[]{Distance.ofPct(inner), Distance.ofPct(outer)};
        return this;
    }

    public PieSeriesOpts centerPixels(int horizontal, int vertical) {
        this.center = new Distance[]{Distance.ofPx(horizontal), Distance.ofPx(vertical)};
        return this;
    }

    public PieSeriesOpts centerPct(double horizontal, double vertical) {
        this.center = new Distance[]{Distance.ofPct(horizontal), Distance.ofPct(vertical)};
        return this;
    }

    public PieSeriesOpts startAngle(int angle) {
        this.startAngle = angle;
        return this;
    }

    public PieSeriesOpts endAngle(int angle) {
        this.endAngle = angle;
        return this;
    }

    public PieSeriesOpts roseType(RoseType roseType) {
        this.roseType = roseType;
        return this;
    }

    /**
     * @since 1.1.0
     */
    public PieSeriesOpts itemStyle(PieItemStyle itemStyle) {
        this.itemStyle = itemStyle;
        return this;
    }

    static class ColumnLinkedPieLabel {
        final String columnName;
        final PieLabel label;

        ColumnLinkedPieLabel(String columnName, PieLabel label) {
            this.columnName = columnName;
            this.label = label;
        }
    }
}
