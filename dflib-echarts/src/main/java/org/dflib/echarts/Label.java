package org.dflib.echarts;

import org.dflib.echarts.render.option.LabelModel;

/**
 * @since 1.0.0-M22
 */
public class Label {

    private LabelPosition position;
    private String formatter;

    public static Label create() {
        return new Label();
    }

    protected Label() {
        this.position = LabelPosition.top;
    }

    public Label position(LabelPosition position) {
        this.position = position;
        return this;
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
