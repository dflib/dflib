package org.dflib.echarts;

import org.dflib.echarts.render.option.EncodeModel;
import org.dflib.echarts.render.option.SeriesModel;
import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

/**
 * @since 1.0.0-M22
 */
public class PieSeriesOpts extends SeriesOpts<PieSeriesOpts> {

    private Object[] radius;
    private Object[] center;
    private BoundLabel label;
    private Integer startAngle;
    private Integer endAngle;
    private RoseType roseType;

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

    public PieSeriesOpts radiusPixels(int pixels) {
        this.radius = new Object[]{pixels};
        return this;
    }

    public PieSeriesOpts radiusPixels(int inner, int outer) {
        this.radius = new Object[]{inner, outer};
        return this;
    }

    public PieSeriesOpts radiusPct(double percent) {
        this.radius = new Object[]{percent + "%"};
        return this;
    }

    public PieSeriesOpts radiusPct(double inner, double outer) {
        this.radius = new Object[]{inner + "%", outer + "%"};
        return this;
    }

    public PieSeriesOpts centerPixels(int horizontal, int vertical) {
        this.center = new Object[]{horizontal, vertical};
        return this;
    }

    public PieSeriesOpts centerPct(double horizontal, double vertical) {
        this.center = new Object[]{horizontal + "%", vertical + "%"};
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

    @Override
    protected SeriesModel resolve(String name, int labelsPos, int seriesPos, String seriesLayoutBy) {
        return new SeriesModel(
                name,
                ChartType.pie.name(),
                new EncodeModel(null, null, labelsPos, seriesPos),
                label != null && label.label != null ? label.label.resolve() : null,
                seriesLayoutBy,
                null,
                null,
                null,
                null,
                null,
                radius != null ? new RadiusModel(radius) : null,
                center != null ? new CenterModel(center[0], center[1]) : null,
                startAngle,
                endAngle,
                roseType != null ? roseType.name() : null
        );
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
