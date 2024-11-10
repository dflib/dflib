package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.LegendModel;

import java.util.Objects;
import java.util.Set;

/**
 * @since 2.0.0
 */
public class Legend {

    private final LegendType type;
    private LegendOrientType orient;
    private LeftDistance left;
    private Distance right;
    private TopDistance top;
    private Distance bottom;
    private Boolean show;
    private Set<String> unselected;

    public static Legend ofPlain() {
        return new Legend(null, LegendType.plain);
    }

    public static Legend ofScroll() {
        return new Legend(null, LegendType.scroll);
    }

    public Legend vertical() {
        this.orient = LegendOrientType.vertical;
        return this;
    }

    public Legend horizontal() {
        this.orient = LegendOrientType.horizontal;
        return this;
    }

    public Legend leftPct(double pct) {
        this.left = new LeftDistance(Distance.ofPct(pct));
        return this;
    }

    public Legend leftPx(int pixels) {
        this.left = new LeftDistance(Distance.ofPx(pixels));
        return this;
    }

    public Legend leftLeft() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.left);
        return this;
    }

    public Legend leftRight() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.right);
        return this;
    }

    public Legend leftCenter() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.center);
        return this;
    }

    public Legend rightPct(double pct) {
        this.right = Distance.ofPct(pct);
        return this;
    }

    public Legend rightPx(int pixels) {
        this.right = Distance.ofPx(pixels);
        return this;
    }

    public Legend topPct(double pct) {
        this.top = new TopDistance(Distance.ofPct(pct));
        return this;
    }

    public Legend topPx(int pixels) {
        this.top = new TopDistance(Distance.ofPx(pixels));
        return this;
    }

    public Legend topTop() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.top);
        return this;
    }

    public Legend topMiddle() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.middle);
        return this;
    }

    public Legend topBottom() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.bottom);
        return this;
    }

    public Legend bottomPct(double pct) {
        this.bottom = Distance.ofPct(pct);
        return this;
    }

    public Legend bottomPx(int pixels) {
        this.bottom = Distance.ofPx(pixels);
        return this;
    }

    /**
     * Unselects the specified series (DataFrame column names) from the legend and from the chart. By default, all
     * series in the legend are active. This allows to unclutter busy charts, leaving an opportunity for the user
     * to turn them back on if they need them.
     */
    public Legend unselected(String... series) {
        this.unselected = Set.of(series);
        return this;
    }

    private Legend(Boolean show, LegendType type) {
        this.show = show;
        this.type = Objects.requireNonNull(type);
    }

    protected LegendModel resolve() {
        return new LegendModel(
                show,
                type.name(),
                orient != null ? orient.name() : null,
                top != null ? top.asString() : null,
                bottom != null ? bottom.asString() : null,
                left != null ? left.asString() : null,
                right != null ? right.asString() : null,
                unselected
        );
    }

    enum LegendType {
        plain, scroll
    }

    enum LegendOrientType {
        horizontal, vertical
    }
}
