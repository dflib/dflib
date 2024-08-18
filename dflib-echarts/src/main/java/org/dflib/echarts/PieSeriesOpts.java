package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;

/**
 * @since 1.0.0-M22
 */
public class PieSeriesOpts extends SeriesOpts<PieSeriesOpts> {

    Distance[] radius;
    Distance[] center;
    BoundLabel label;
    Integer startAngle;
    Integer endAngle;
    RoseType roseType;

    @Override
    public ChartType getType() {
        return ChartType.pie;
    }

    public PieSeriesOpts label(String labelColumn) {
        this.label = new BoundLabel(labelColumn, null);
        return this;
    }

    public PieSeriesOpts label(String labelColumn, PieLabel label) {
        this.label = new BoundLabel(labelColumn, label);
        return this;
    }

    public PieSeriesOpts label(PieLabel label) {
        this.label = new BoundLabel(null, label);
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

    protected String getLabelColumn() {
        return label != null ? label.columnName : null;
    }

    static class BoundLabel {
        final String columnName;
        final PieLabel label;

        BoundLabel(String columnName, PieLabel label) {
            this.columnName = columnName;
            this.label = label;
        }
    }
}
