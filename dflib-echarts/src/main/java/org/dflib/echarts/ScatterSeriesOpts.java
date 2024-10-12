package org.dflib.echarts;

public class ScatterSeriesOpts extends CartesianSeriesOpts<ScatterSeriesOpts> {

    Label label;

    @Override
    public ChartType getType() {
        return ChartType.scatter;
    }

    public ScatterSeriesOpts label(LabelPosition position) {
        this.label = Label.of(position);
        return this;
    }

    public ScatterSeriesOpts label(Label label) {
        this.label = label;
        return this;
    }

}
