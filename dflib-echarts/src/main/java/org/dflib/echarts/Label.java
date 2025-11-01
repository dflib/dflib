package org.dflib.echarts;

import org.dflib.echarts.render.option.LabelModel;

public class Label {

    private Boolean show;
    private LabelPosition position;
    private String formatter;

    public static Label of(LabelPosition position) {
        return new Label(position);
    }

    public static Label ofTop() {
        return new Label(LabelPosition.top);
    }

    public static Label ofLeft() {
        return new Label(LabelPosition.left);
    }

    public static Label ofRight() {
        return new Label(LabelPosition.right);
    }

    public static Label ofBottom() {
        return new Label(LabelPosition.bottom);
    }

    public static Label ofInside() {
        return new Label(LabelPosition.inside);
    }

    public static Label ofInsideLeft() {
        return new Label(LabelPosition.insideLeft);
    }

    public static Label ofInsideRight() {
        return new Label(LabelPosition.insideRight);
    }

    public static Label ofInsideTop() {
        return new Label(LabelPosition.insideTop);
    }

    public static Label ofInsideBottom() {
        return new Label(LabelPosition.insideBottom);
    }

    public static Label ofInsideTopLeft() {
        return new Label(LabelPosition.insideTopLeft);
    }

    public static Label ofInsideBottomLeft() {
        return new Label(LabelPosition.insideBottomLeft);
    }

    public static Label ofInsideTopRight() {
        return new Label(LabelPosition.insideTopRight);
    }

    public static Label ofInsideBottomRight() {
        return new Label(LabelPosition.insideBottomRight);
    }

    protected Label(LabelPosition position) {
        this.position = position;
    }

    public Label formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    public Label show(boolean show) {
        this.show = show;
        return this;
    }

    LabelModel resolve(Integer itemNameDimension) {

        // point label to the item name, unless formatter is set explicitly
        String formatter = this.formatter != null
                ? this.formatter
                : (itemNameDimension != null ? "{@[" + itemNameDimension + "]}" : null);

        return new LabelModel(
                show,
                formatter,
                position != null ? position.name() : null
        );
    }
}
