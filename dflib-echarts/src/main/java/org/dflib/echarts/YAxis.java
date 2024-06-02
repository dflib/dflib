package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisModel;

/**
 * @since 1.0.0-M22
 */
public class YAxis extends Axis<YAxis> {

    private YAxisPosition position;

    public static YAxis of(AxisType type) {
        return new YAxis(type);
    }

    public static YAxis ofDefault() {
        return new YAxis(AxisType.value);
    }

    public static YAxis ofCategory() {
        return new YAxis(AxisType.category);
    }

    public static YAxis ofValue() {
        return new YAxis(AxisType.value);
    }

    public static YAxis ofTime() {
        return new YAxis(AxisType.time);
    }

    public static YAxis ofLog() {
        return new YAxis(AxisType.log);
    }

    protected YAxis(AxisType type) {
        super(type);
    }

    public YAxis right() {
        this.position = YAxisPosition.right;
        return this;
    }

    public YAxis left() {
        this.position = YAxisPosition.left;
        return this;
    }

    @Override
    protected AxisModel resolve() {
        return new AxisModel(
                alignTicks,
                gridIndex,
                name,
                offset,
                this.position != null ? position.name() : null,
                type.name(),
                label != null ? label.resolve() : null,
                line != null ? line.resolve() : null,
                boundaryGap
        );
    }
}
