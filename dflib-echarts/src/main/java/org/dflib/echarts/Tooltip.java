package org.dflib.echarts;

import org.dflib.echarts.render.option.tooltip.TooltipModel;

/**
 * @since 1.0.0-M22
 */
public class Tooltip {

    private TooltipAxisPointer axisPointer;
    private TooltipTrigger trigger;

    public static Tooltip of(TooltipTrigger trigger) {
        return new Tooltip(trigger);
    }

    public static Tooltip ofItem() {
        return new Tooltip(TooltipTrigger.item);
    }

    public static Tooltip ofAxis() {
        return new Tooltip(TooltipTrigger.axis);
    }

    public static Tooltip ofNone() {
        return new Tooltip(TooltipTrigger.none);
    }

    protected Tooltip(TooltipTrigger trigger) {
        this.trigger = trigger;
    }

    public Tooltip axisPointer(TooltipAxisPointer axisPointer) {
        this.axisPointer = axisPointer;
        return this;
    }

    public Tooltip axisPointerLine() {
        this.axisPointer = TooltipAxisPointer.ofLine();
        return this;
    }

    public Tooltip axisPointerCross() {
        this.axisPointer = TooltipAxisPointer.ofCross();
        return this;
    }

    public Tooltip axisPointerShadow() {
        this.axisPointer = TooltipAxisPointer.ofShadow();
        return this;
    }

    protected TooltipModel resolve() {
        return new TooltipModel(
                this.axisPointer != null ? this.axisPointer.resolve() : null,
                this.trigger != null ? this.trigger.name() : TooltipTrigger.item.name()
        );
    }
}
