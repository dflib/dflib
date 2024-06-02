package org.dflib.echarts;

import org.dflib.echarts.render.option.LabelModel;

/**
 * @since 1.0.0-M22
 */
public class Label {

    private LabelPosition position;
    private String formatter;

    public static Label of(LabelPosition position) {
        return new Label(position);
    }

    public static Label top() {
        return new Label(LabelPosition.top);
    }

    public static Label left() {
        return new Label(LabelPosition.left);
    }

    public static Label right() {
        return new Label(LabelPosition.right);
    }

    public static Label bottom() {
        return new Label(LabelPosition.bottom);
    }

    public static Label inside() {
        return new Label(LabelPosition.inside);
    }

    public static Label insideLeft() {
        return new Label(LabelPosition.insideLeft);
    }

    public static Label insideRight() {
        return new Label(LabelPosition.insideRight);
    }

    public static Label insideTop() {
        return new Label(LabelPosition.insideTop);
    }

    public static Label insideBottom() {
        return new Label(LabelPosition.insideBottom);
    }

    public static Label insideTopLeft() {
        return new Label(LabelPosition.insideTopLeft);
    }

    public static Label insideBottomLeft() {
        return new Label(LabelPosition.insideBottomLeft);
    }

    public static Label insideTopRight() {
        return new Label(LabelPosition.insideTopRight);
    }

    public static Label insideBottomRight() {
        return new Label(LabelPosition.insideBottomRight);
    }

    protected Label(LabelPosition position) {
        this.position = position;
    }

    public Label formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    LabelModel resolve() {
        return new LabelModel(
                formatter,
                position != null ? position.name() : null
        );
    }
}
