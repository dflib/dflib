package org.dflib.echarts.model;

/**
 * A model for rendering EChart script "series" element
 *
 * @since 1.0.0-M21
 */
public class SeriesModel {

    private final String name;
    private final String type;
    private final EncodeModel encode;
    private final String seriesLayoutBy;
    private final boolean areaStyle;
    private final boolean stack;
    private final boolean smooth;
    private final boolean last;

    public SeriesModel(
            String name,
            String type,
            EncodeModel encode,
            String seriesLayoutBy,
            boolean areaStyle,
            boolean stack,
            boolean smooth,
            boolean last) {

        this.name = name;
        this.type = type;
        this.encode = encode;
        this.seriesLayoutBy = seriesLayoutBy;
        this.areaStyle = areaStyle;
        this.stack = stack;
        this.smooth = smooth;
        this.last = last;
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

    public String getSeriesLayoutBy() {
        return seriesLayoutBy;
    }

    public boolean isAreaStyle() {
        return areaStyle;
    }

    public boolean isStack() {
        return stack;
    }

    public boolean isSmooth() {
        return smooth;
    }

    public boolean isLast() {
        return last;
    }
}
