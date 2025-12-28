package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.visualmap.VisualMapModel;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class VisualMap {

    public static VisualMap ofContinuous() {
        return new VisualMap(Type.continuous);
    }

    public static VisualMap ofPiecewise() {
        return new VisualMap(Type.piecewise);
    }

    private final Type type;

    private Integer min;
    private Integer max;
    private Boolean calculable;
    private Orient orient;

    private LeftDistance left;
    private Distance right;
    private TopDistance top;
    private Distance bottom;
    private Distance itemWidth;
    private Distance itemHeight;
    private VisualChannels outOfRange;

    private VisualMap(Type type) {
        this.type = Objects.requireNonNull(type);
    }

    public VisualMap min(int min) {
        this.min = min;
        return this;
    }

    public VisualMap max(int max) {
        this.max = max;
        return this;
    }

    public VisualMap calculable(boolean b) {
        this.calculable = b;
        return this;
    }

    public VisualMap horizontal() {
        this.orient = Orient.horizontal;
        return this;
    }

    public VisualMap vertical() {
        this.orient = Orient.vertical;
        return this;
    }

    public VisualMap leftPct(double pct) {
        this.left = new LeftDistance(Distance.ofPct(pct));
        return this;
    }

    public VisualMap leftPx(int pixels) {
        this.left = new LeftDistance(Distance.ofPx(pixels));
        return this;
    }

    public VisualMap leftLeft() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.left);
        return this;
    }

    public VisualMap leftRight() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.right);
        return this;
    }

    public VisualMap leftCenter() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.center);
        return this;
    }

    public VisualMap rightPct(double pct) {
        this.right = Distance.ofPct(pct);
        return this;
    }

    public VisualMap rightPx(int pixels) {
        this.right = Distance.ofPx(pixels);
        return this;
    }

    public VisualMap topPct(double pct) {
        this.top = new TopDistance(Distance.ofPct(pct));
        return this;
    }

    public VisualMap topPx(int pixels) {
        this.top = new TopDistance(Distance.ofPx(pixels));
        return this;
    }

    public VisualMap topTop() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.top);
        return this;
    }

    public VisualMap topMiddle() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.middle);
        return this;
    }

    public VisualMap topBottom() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.bottom);
        return this;
    }

    public VisualMap bottomPct(double pct) {
        this.bottom = Distance.ofPct(pct);
        return this;
    }

    public VisualMap bottomPx(int pixels) {
        this.bottom = Distance.ofPx(pixels);
        return this;
    }

    public VisualMap itemWidthPct(double pct) {
        this.itemWidth = Distance.ofPct(pct);
        return this;
    }

    public VisualMap itemWidthPx(int pixels) {
        this.itemWidth = Distance.ofPx(pixels);
        return this;
    }

    public VisualMap itemHeightPct(double pct) {
        this.itemHeight = Distance.ofPct(pct);
        return this;
    }

    public VisualMap itemHeightPx(int pixels) {
        this.itemHeight = Distance.ofPx(pixels);
        return this;
    }

    public VisualMap outOfRange(VisualChannels outOfRange) {
        this.outOfRange = outOfRange;
        return this;
    }

    enum Type {
        continuous, piecewise
    }

    enum Orient {
        horizontal, vertical
    }

    protected VisualMapModel resolve() {
        return new VisualMapModel(
                type.name(),
                min,
                max,
                calculable,
                orient != null ? orient.name() : null,
                left != null ? left.asString() : null,
                right != null ? right.asString() : null,
                top != null ? top.asString() : null,
                bottom != null ? bottom.asString() : null,
                itemWidth != null ? itemWidth.asString() : null, // ignoring 'auto' option, which is the default
                itemHeight != null ? itemHeight.asString() : null, // ignoring 'auto' option, which is the default
                outOfRange != null ? outOfRange.resolve() : null
        );
    }
}
