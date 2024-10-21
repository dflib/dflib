package org.dflib.echarts.render.option;

import org.dflib.echarts.render.option.series.CenterModel;
import org.dflib.echarts.render.option.series.ItemStyleModel;
import org.dflib.echarts.render.option.series.RadiusModel;

/**
 * A model for rendering EChart script "series" element
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
    private final Integer xAxisIndex;
    private final Integer yAxisIndex;
    private final RadiusModel radius;
    private final CenterModel center;
    private final Integer startAngle;
    private final Integer endAngle;
    private final String roseType;
    private final ItemStyleModel itemStyle;

    /**
     * @deprecated in favor of a constructor that takes an extra ItemStyleModel argument
     */
    @Deprecated(since = "1.1.0", forRemoval = true)
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
            Integer xAxisIndex,
            Integer yAxisIndex,
            RadiusModel radius,
            CenterModel center,
            Integer startAngle,
            Integer endAngle,
            String roseType) {

        this(name, type, encode, label, seriesLayoutBy, areaStyle, showSymbol, stack, smooth, xAxisIndex, yAxisIndex,
                radius, center, startAngle, endAngle, roseType, null);
    }

    /**
     * @since 1.1.0
     */
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
            Integer xAxisIndex,
            Integer yAxisIndex,
            RadiusModel radius,
            CenterModel center,
            Integer startAngle,
            Integer endAngle,
            String roseType,
            ItemStyleModel itemStyle) {

        this.name = name;
        this.type = type;
        this.encode = encode;
        this.label = label;
        this.seriesLayoutBy = seriesLayoutBy;
        this.areaStyle = areaStyle;
        this.showSymbol = showSymbol;
        this.stack = stack;
        this.smooth = smooth;
        this.xAxisIndex = xAxisIndex;
        this.yAxisIndex = yAxisIndex;
        this.radius = radius;
        this.center = center;
        this.startAngle = startAngle;
        this.endAngle = endAngle;
        this.roseType = roseType;
        this.itemStyle = itemStyle;
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

    public LabelModel getLabel() {
        return label;
    }

    public String getSeriesLayoutBy() {
        return seriesLayoutBy;
    }

    public boolean isAreaStyle() {
        return areaStyle != null && areaStyle;
    }

    public boolean dontShowSymbol() {
        return showSymbol != null && !showSymbol;
    }

    public Integer getXAxisIndex() {
        return xAxisIndex;
    }

    public Integer getYAxisIndex() {
        return yAxisIndex;
    }

    public RadiusModel getRadius() {
        return radius;
    }

    public CenterModel getCenter() {
        return center;
    }

    public Integer getStartAngle() {
        return startAngle;
    }

    public Integer getEndAngle() {
        return endAngle;
    }

    public String getRoseType() {
        return roseType;
    }

    public boolean isStack() {
        return stack != null && stack;
    }

    public boolean isSmooth() {
        return smooth != null && smooth;
    }

    public ItemStyleModel getItemStyle() {
        return itemStyle;
    }
}
