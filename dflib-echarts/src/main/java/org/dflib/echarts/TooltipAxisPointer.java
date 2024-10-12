package org.dflib.echarts;

import org.dflib.echarts.render.option.tooltip.TooltipAxisPointerModel;

public class TooltipAxisPointer {

    private TooltipAxisPointerType type;

    public static TooltipAxisPointer of(TooltipAxisPointerType type) {
        return new TooltipAxisPointer(type);
    }

    public static TooltipAxisPointer ofLine() {
        return new TooltipAxisPointer(TooltipAxisPointerType.line);
    }

    public static TooltipAxisPointer ofShadow() {
        return new TooltipAxisPointer(TooltipAxisPointerType.shadow);
    }

    public static TooltipAxisPointer ofCross() {
        return new TooltipAxisPointer(TooltipAxisPointerType.cross);
    }

    protected TooltipAxisPointer(TooltipAxisPointerType type) {
        this.type = type;
    }

    protected TooltipAxisPointerModel resolve() {
        return new TooltipAxisPointerModel(
                this.type != null ? this.type.name() : TooltipAxisPointerType.line.name()
        );
    }
}
