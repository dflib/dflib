package org.dflib.echarts.render.option.tooltip;

public class TooltipModel {

    private final String trigger;
    private final TooltipAxisPointerModel axisPointer;

    public TooltipModel(TooltipAxisPointerModel axisPointer, String trigger) {
        this.axisPointer = axisPointer;
        this.trigger = trigger;
    }

    public String getTrigger() {
        return trigger;
    }

    public TooltipAxisPointerModel getAxisPointer() {
        return axisPointer;
    }
}
