package org.dflib.echarts.render.option;

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
    private final boolean showSymbol;
    private final boolean smooth;
    private final boolean stack;
    private final boolean last;

    public SeriesModel(
            String name,
            String type,
            EncodeModel encode,
            String seriesLayoutBy,
            boolean areaStyle,
            boolean showSymbol,
            boolean stack,
            boolean smooth,
            boolean last) {

        this.name = name;
        this.type = type;
        this.encode = encode;
        this.seriesLayoutBy = seriesLayoutBy;
        this.areaStyle = areaStyle;
        this.showSymbol = showSymbol;
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

    /**
     * @since 1.0.0-M22
     */
    public boolean dontShowSymbol() {
        return !showSymbol;
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
