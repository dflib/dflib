package org.dflib.echarts.render.option;

public class GridModel {

    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final String width;
    private final String height;

    public GridModel(String left, String right, String top, String bottom, String width, String height) {
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
        this.width = width;
        this.height = height;
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
}
