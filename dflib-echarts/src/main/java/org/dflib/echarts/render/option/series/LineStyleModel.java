package org.dflib.echarts.render.option.series;

/**
 * @since 1.1.0
 */
public class LineStyleModel {

    private final String color;
    private final Integer width;
    private final Double opacity;
    private final String type;

    public LineStyleModel(String color, Integer width, Double opacity, String type) {
        this.color = color;
        this.width = width;
        this.opacity = opacity;
        this.type = type;
    }

    public String getColor() {
        return color;
    }

    public Integer getWidth() {
        return width;
    }

    public Double getOpacity() {
        return opacity;
    }

    public String getType() {
        return type;
    }
}
