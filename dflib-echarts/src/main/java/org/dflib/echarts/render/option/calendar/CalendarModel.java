package org.dflib.echarts.render.option.calendar;

import java.util.Objects;

public class CalendarModel {

    private final String rangeFrom;
    private final String rangeTo;
    private final String cellSize;
    private final String orient;
    private final String left;
    private final String right;
    private final String top;
    private final String bottom;
    private final String width;
    private final String height;
    private final YearLabelModel yearLabel;

    public CalendarModel(
            String rangeFrom,
            String rangeTo,
            String cellSize,
            String orient,
            String left,
            String right,
            String top,
            String bottom,
            String width,
            String height,
            YearLabelModel yearLabel) {
        this.rangeFrom = Objects.requireNonNull(rangeFrom);
        this.rangeTo = Objects.requireNonNull(rangeTo);
        this.cellSize = cellSize;
        this.orient = orient;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.top = top;
        this.width = width;
        this.height = height;
        this.yearLabel = yearLabel;
    }

    public String getRangeFrom() {
        return rangeFrom;
    }

    public String getRangeTo() {
        return rangeTo;
    }

    public String getCellSize() {
        return cellSize;
    }

    public String getOrient() {
        return orient;
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

    public YearLabelModel getYearLabel() {
        return yearLabel;
    }
}
