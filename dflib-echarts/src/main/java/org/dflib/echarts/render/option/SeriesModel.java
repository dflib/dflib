package org.dflib.echarts.render.option;

import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.RadiusModel;

/**
 * A model for rendering EChart script "series" element
 *
 * @since 1.0.0-M21
 */
public class SeriesModel {

    private final String name;
    private final String type;
    private final EncodeModel encode;
    private final LabelModel label;
    private final String seriesLayoutBy;
    private final Boolean areaStyle;
    private final Boolean showSymbol;
    private final Boolean smooth;
    private final Boolean stack;
    private final Integer yAxisIndex;
    private final RadiusModel radius;
    private final CenterModel center;
    private final Integer startAngle;
    private final Integer endAngle;
    private final String roseType;

    public SeriesModel(
            String name,
            String type,
            EncodeModel encode,
            LabelModel label,
            String seriesLayoutBy,
            Boolean areaStyle,
            Boolean showSymbol,
            Boolean stack,
            Boolean smooth,
            Integer yAxisIndex,
            RadiusModel radius,
            CenterModel center,
            Integer startAngle,
            Integer endAngle,
            String roseType) {

        this.name = name;
        this.type = type;
        this.encode = encode;
        this.label = label;
        this.seriesLayoutBy = seriesLayoutBy;
        this.areaStyle = areaStyle;
        this.showSymbol = showSymbol;
        this.stack = stack;
        this.smooth = smooth;
        this.yAxisIndex = yAxisIndex;
        this.radius = radius;
        this.center = center;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.roseType = roseType;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public EncodeModel getEncode() {
        return encode;
    }

    /**
     * @since 1.0.0-M22
     */
    public LabelModel getLabel() {
        return label;
    }

    public String getSeriesLayoutBy() {
        return seriesLayoutBy;
    }

    public boolean isAreaStyle() {
        return areaStyle != null && areaStyle;
    }

    /**
     * @since 1.0.0-M22
     */
    public boolean dontShowSymbol() {
        return showSymbol != null && !showSymbol;
    }

    /**
     * @since 1.0.0-M22
     */
    public Integer getYAxisIndex() {
        return yAxisIndex;
    }

    /**
     * @since 1.0.0-M22
     */
    public RadiusModel getRadius() {
        return radius;
    }

    /**
     * @since 1.0.0-M22
     */
    public CenterModel getCenter() {
        return center;
    }

    /**
     * @since 1.0.0-M22
     */
    public Integer getStartAngle() {
        return startAngle;
    }

    /**
     * @since 1.0.0-M22
     */
    public Integer getEndAngle() {
        return endAngle;
    }

    /**
     * @since 1.0.0-M22
     */
    public String getRoseType() {
        return roseType;
    }

    public boolean isStack() {
        return stack != null && stack;
    }

    public boolean isSmooth() {
        return smooth != null && smooth;
    }
}
