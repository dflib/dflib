package org.dflib.echarts;

import org.dflib.echarts.render.option.LabelModel;

/**
 * @since 1.0.0-M22
 */
public class PieLabel {

    private Boolean show;
    private PieLabelPosition position;
    private String formatter;

    public static PieLabel of(PieLabelPosition position) {
        return new PieLabel(position);
    }

    public static PieLabel ofOutside() {
        return new PieLabel(PieLabelPosition.outside);
    }

    public static PieLabel ofInside() {
        return new PieLabel(PieLabelPosition.inside);
    }

    public static PieLabel ofCenter() {
        return new PieLabel(PieLabelPosition.center);
    }

    protected PieLabel(PieLabelPosition position) {
        this.position = position;
    }

    public PieLabel formatter(String formatter) {
        this.formatter = formatter;
        return this;
    }

    public PieLabel show(boolean show) {
        this.show = show;
        return this;
    }

    LabelModel resolve() {
        return new LabelModel(
                show,
                formatter,
                position != null ? position.name() : null
        );
    }
}
