package org.dflib.echarts.render.option.visualmap;

/**
 * @since 2.0.0
 */
public class VisualMapModel {

    private final String type;
    private final Integer min;
    private final Integer max;
    private final Boolean calculable;
    private final String orient;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final String itemWidth;
    private final String itemHeight;
    private Boolean show;
    private final VisualChannelsModel outOfRange;

    public VisualMapModel(
            String type,
            Integer min,
            Integer max,
            Boolean calculable,
            String orient,
            String left,
            String right,
            String top,
            String bottom,
            String itemWidth,
            String itemHeight,
            Boolean show,
            VisualChannelsModel outOfRange) {

        this.bottom = bottom;
        this.type = type;
        this.min = min;
        this.max = max;
        this.calculable = calculable;
        this.orient = orient;
        this.left = left;
        this.right = right;
        this.top = top;
        this.itemWidth = itemWidth;
        this.itemHeight = itemHeight;
        this.show = show;
        this.outOfRange = outOfRange;
    }

    public String getBottom() {
        return bottom;
    }

    public Boolean getCalculable() {
        return calculable;
    }

    public String getItemHeight() {
        return itemHeight;
    }

    public String getLeft() {
        return left;
    }

    public Integer getMax() {
        return max;
    }

    public Integer getMin() {
        return min;
    }

    public String getOrient() {
        return orient;
    }

    public String getRight() {
        return right;
    }

    public String getTop() {
        return top;
    }

    public String getType() {
        return type;
    }

    public String getItemWidth() {
        return itemWidth;
    }

    public boolean isShowPresent() {
        return show != null;
    }

    public Boolean getShow() {
        return show;
    }

    public VisualChannelsModel getOutOfRange() {
        return outOfRange;
    }
}
