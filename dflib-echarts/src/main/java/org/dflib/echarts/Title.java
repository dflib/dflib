package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.TitleModel;

/**
 * @since 2.0.0
 */
public class Title {

    private final String text;
    private String subtext;
    private LeftDistance left;
    private Distance right;
    private TopDistance top;
    private Distance bottom;

    public static Title of(String text) {
        return new Title(text);
    }

    private Title(String text) {
        this.text = text;
    }

    public Title subtext(String subtext) {
        this.subtext = subtext;
        return this;
    }

    public Title leftPct(double pct) {
        this.left = new LeftDistance(Distance.ofPct(pct));
        return this;
    }

    public Title leftPx(int pixels) {
        this.left = new LeftDistance(Distance.ofPx(pixels));
        return this;
    }

    public Title leftLeft() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.left);
        return this;
    }

    public Title leftRight() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.right);
        return this;
    }

    public Title leftCenter() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.center);
        return this;
    }

    public Title rightPct(double pct) {
        this.right = Distance.ofPct(pct);
        return this;
    }

    public Title rightPx(int pixels) {
        this.right = Distance.ofPx(pixels);
        return this;
    }

    public Title topPct(double pct) {
        this.top = new TopDistance(Distance.ofPct(pct));
        return this;
    }

    public Title topPx(int pixels) {
        this.top = new TopDistance(Distance.ofPx(pixels));
        return this;
    }

    public Title topTop() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.top);
        return this;
    }

    public Title topMiddle() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.middle);
        return this;
    }

    public Title topBottom() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.bottom);
        return this;
    }

    public Title bottomPct(double pct) {
        this.bottom = Distance.ofPct(pct);
        return this;
    }

    public Title bottomPx(int pixels) {
        this.bottom = Distance.ofPx(pixels);
        return this;
    }

    protected TitleModel resolve() {
        return new TitleModel(
                text,
                subtext,
                top != null ? top.asString() : null,
                bottom != null ? bottom.asString() : null,
                left != null ? left.asString() : null,
                right != null ? right.asString() : null
        );
    }
}
