package org.dflib.echarts.render.option.axis;

import org.dflib.echarts.render.util.Renderer;

/**
 * @since 2.0.0
 */
public class SingleAxisModel {

    private final String name;
    private final String type;
    private final boolean boundaryGap;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final String width;
    private final String height;
    private final String min;
    private final String max;
    private final Boolean scale;

    public SingleAxisModel(
            String name,
            String type,
            boolean boundaryGap,
            String left,
            String right,
            String top,
            String bottom,
            String width,
            String height,
            String min,
            String max,
            Boolean scale) {

        this.name = name;
        this.type = type;
        this.boundaryGap = boundaryGap;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
        this.width = width;
        this.height = height;
        this.min = min;
        this.max = max;
        this.scale = scale;
    }

    public String getName() {
        return name;
    }

    public String getQuotedName() {
        return Renderer.quotedValue(name);
    }

    public String getType() {
        return type;
    }

    public boolean isBoundaryGap() {
        return boundaryGap;
    }

    public boolean isNoBoundaryGap() {
        return !boundaryGap;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }

    public String getTop() {
        return top;
    }

    public String getBottom() {
        return bottom;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public String getMin() {
        return min;
    }

    public String getMax() {
        return max;
    }

    public boolean isScalePresent() {
        return scale != null;
    }

    public Boolean getScale() {
        return scale;
    }
}
