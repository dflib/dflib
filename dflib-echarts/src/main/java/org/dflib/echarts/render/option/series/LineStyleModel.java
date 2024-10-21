package org.dflib.echarts.render.option.series;

/**
 * @since 1.1.0
 */
public class LineStyleModel {

    private final String color;
    private final Integer width;
    private final Double opacity;

    public LineStyleModel(String color, Integer width, Double opacity) {
        this.color = color;
        this.width = width;
        this.opacity = opacity;
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
}
