package org.dflib.echarts;

import org.dflib.echarts.render.option.axis.AxisModel;

public class XAxis extends Axis<XAxis> {

    private XAxisPosition position;

    public static XAxis ofDefault() {
        return new XAxis(AxisType.category);
    }

    public static XAxis of(AxisType type) {
        return new XAxis(type);
    }

    public static XAxis ofCategory() {
        return new XAxis(AxisType.category);
    }

    public static XAxis ofValue() {
        return new XAxis(AxisType.value);
    }

    public static XAxis ofTime() {
        return new XAxis(AxisType.time);
    }

    public static XAxis ofLog() {
        return new XAxis(AxisType.log);
    }

    protected XAxis(AxisType type) {
        super(type);
    }

    public XAxis top() {
        this.position = XAxisPosition.top;
        return this;
    }

    public XAxis bottom() {
        this.position = XAxisPosition.bottom;
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
