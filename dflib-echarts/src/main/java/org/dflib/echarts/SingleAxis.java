package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.axis.SingleAxisModel;

/**
 * @since 2.0.0
 */
public class SingleAxis {

    private final AxisType type;
    private String name;
    private boolean boundaryGap;
    private LeftDistance left;
    private Distance right;
    private TopDistance top;
    private Distance bottom;
    private Distance width;
    private Distance height;

    public static SingleAxis of(AxisType type) {
        return new SingleAxis(type);
    }

    public static SingleAxis ofCategory() {
        return new SingleAxis(AxisType.category);
    }

    public static SingleAxis ofValue() {
        return new SingleAxis(AxisType.value);
    }

    public static SingleAxis ofTime() {
        return new SingleAxis(AxisType.time);
    }

    public static SingleAxis ofLog() {
        return new SingleAxis(AxisType.log);
    }

    SingleAxis(AxisType type) {
        this.type = type;
    }

    public SingleAxis name(String name) {
        this.name = name;
        return this;
    }

    public SingleAxis boundaryGap(boolean gap) {
        this.boundaryGap = gap;
        return this;
    }

    public SingleAxis leftPct(double pct) {
        this.left = new LeftDistance(Distance.ofPct(pct));
        return this;
    }

    public SingleAxis leftPx(int pixels) {
        this.left = new LeftDistance(Distance.ofPx(pixels));
        return this;
    }

    public SingleAxis leftLeft() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.left);
        return this;
    }

    public SingleAxis leftRight() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.right);
        return this;
    }

    public SingleAxis leftCenter() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.center);
        return this;
    }

    public SingleAxis rightPct(double pct) {
        this.right = Distance.ofPct(pct);
        return this;
    }

    public SingleAxis rightPx(int pixels) {
        this.right = Distance.ofPx(pixels);
        return this;
    }

    public SingleAxis topPct(double pct) {
        this.top = new TopDistance(Distance.ofPct(pct));
        return this;
    }

    public SingleAxis topPx(int pixels) {
        this.top = new TopDistance(Distance.ofPx(pixels));
        return this;
    }

    public SingleAxis topTop() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.top);
        return this;
    }

    public SingleAxis topMiddle() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.middle);
        return this;
    }

    public SingleAxis topBottom() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.bottom);
        return this;
    }

    public SingleAxis bottomPct(double pct) {
        this.bottom = Distance.ofPct(pct);
        return this;
    }

    public SingleAxis bottomPx(int pixels) {
        this.bottom = Distance.ofPx(pixels);
        return this;
    }

    public SingleAxis widthPct(double pct) {
        this.width = Distance.ofPct(pct);
        return this;
    }

    public SingleAxis widthPx(int pixels) {
        this.width = Distance.ofPx(pixels);
        return this;
    }

    public SingleAxis heightPct(double pct) {
        this.height = Distance.ofPct(pct);
        return this;
    }

    public SingleAxis heightPx(int pixels) {
        this.height = Distance.ofPx(pixels);
        return this;
    }

    SingleAxisModel resolve() {
        return new SingleAxisModel(
                name,
                type.name(),
                boundaryGap,
                left != null ? left.asString() : null,
                right != null ? right.asString() : null,
                top != null ? top.asString() : null,
                bottom != null ? bottom.asString() : null,
                width != null ? width.asString() : null, // ignoring 'auto' option, which is the default
                height != null ? height.asString() : null // ignoring 'auto' option, which is the default
        );
    }

}
