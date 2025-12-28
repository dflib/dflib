package org.dflib.echarts;

import org.dflib.echarts.render.option.Distance;
import org.dflib.echarts.render.option.calendar.CalendarModel;

import java.time.LocalDate;
import java.time.YearMonth;

/**
 * Represents calendar coordinates in a plot.
 *
 * @since 2.0.0
 */
public class CalendarCoords {

    public static CalendarCoords of(LocalDate from, LocalDate to) {
        return new CalendarCoords(from, to);
    }

    public static CalendarCoords ofLast12Months() {
        LocalDate now = LocalDate.now();
        return of(now.minusMonths(12), now);
    }

    public static CalendarCoords ofYear(int year) {
        return of(LocalDate.of(year, 1, 1), LocalDate.of(year, 12, 31));
    }

    public static CalendarCoords ofMonth(int year, int month) {
        YearMonth ym = YearMonth.of(year, month);
        return of(ym.atDay(1), ym.atEndOfMonth());
    }

    private final LocalDate rangeFrom;
    private final LocalDate rangeTo;
    private CellDimension cellWidth;
    private CellDimension cellHeight;
    private Orient orient;

    private LeftDistance left;
    private Distance right;
    private TopDistance top;
    private Distance bottom;
    private Distance width;
    private Distance height;
    private YearLabel yearLabel;

    private CalendarCoords(LocalDate rangeFrom, LocalDate rangeTo) {
        this.rangeFrom = rangeFrom;
        this.rangeTo = rangeTo;
    }

    public CalendarCoords cellWidth(int w) {
        this.cellWidth = CellDimension.of(w);
        return this;
    }

    public CalendarCoords cellWidthAuto() {
        this.cellWidth = CellDimension.ofAuto();
        return this;
    }

    public CalendarCoords cellHeight(int h) {
        this.cellHeight = CellDimension.of(h);
        return this;
    }

    public CalendarCoords cellHeightAuto() {
        this.cellHeight = CellDimension.ofAuto();
        return this;
    }

    public CalendarCoords horizontal() {
        this.orient = Orient.horizontal;
        return this;
    }

    public CalendarCoords vertical() {
        this.orient = Orient.vertical;
        return this;
    }

    public CalendarCoords leftPct(double pct) {
        this.left = new LeftDistance(Distance.ofPct(pct));
        return this;
    }

    public CalendarCoords leftPx(int pixels) {
        this.left = new LeftDistance(Distance.ofPx(pixels));
        return this;
    }

    public CalendarCoords leftLeft() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.left);
        return this;
    }

    public CalendarCoords leftRight() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.right);
        return this;
    }

    public CalendarCoords leftCenter() {
        this.left = new LeftDistance(LeftDistance.AutoLeftDistance.center);
        return this;
    }

    public CalendarCoords rightPct(double pct) {
        this.right = Distance.ofPct(pct);
        return this;
    }

    public CalendarCoords rightPx(int pixels) {
        this.right = Distance.ofPx(pixels);
        return this;
    }

    public CalendarCoords topPct(double pct) {
        this.top = new TopDistance(Distance.ofPct(pct));
        return this;
    }

    public CalendarCoords topPx(int pixels) {
        this.top = new TopDistance(Distance.ofPx(pixels));
        return this;
    }

    public CalendarCoords topTop() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.top);
        return this;
    }

    public CalendarCoords topMiddle() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.middle);
        return this;
    }

    public CalendarCoords topBottom() {
        this.top = new TopDistance(TopDistance.AutoTopDistance.bottom);
        return this;
    }

    public CalendarCoords bottomPct(double pct) {
        this.bottom = Distance.ofPct(pct);
        return this;
    }

    public CalendarCoords bottomPx(int pixels) {
        this.bottom = Distance.ofPx(pixels);
        return this;
    }

    public CalendarCoords widthPct(double pct) {
        this.width = Distance.ofPct(pct);
        return this;
    }

    public CalendarCoords widthPx(int pixels) {
        this.width = Distance.ofPx(pixels);
        return this;
    }

    public CalendarCoords heightPct(double pct) {
        this.height = Distance.ofPct(pct);
        return this;
    }

    public CalendarCoords heightPx(int pixels) {
        this.height = Distance.ofPx(pixels);
        return this;
    }

    /**
     * @since 2.0.0
     */
    public CalendarCoords yearLabel(YearLabel yearLabel) {
        this.yearLabel = yearLabel;
        return this;
    }

    protected CalendarModel resolve() {

        // ECharts bug: without top,bottom, left, right, the default width (or height, depending on orientation) must
        // be 'auto', not 20. Otherwise, the range will be truncated.

        Orient o = this.orient != null ? this.orient : Orient.horizontal;
        CellDimension w = cellWidth != null
                ? cellWidth
                : ((o == Orient.horizontal) ? CellDimension.ofAuto() : CellDimension.of(20));
        CellDimension h = cellHeight != null
                ? cellHeight
                : ((o == Orient.vertical) ? CellDimension.ofAuto() : CellDimension.of(20));

        return new CalendarModel(
                rangeFrom != null ? rangeFrom.toString() : null,
                rangeTo != null ? rangeTo.toString() : null,
                "[" + w.asString() + "," + h.asString() + "]",
                this.orient != null ? this.orient.name() : null,
                left != null ? left.asString() : null,
                right != null ? right.asString() : null,
                top != null ? top.asString() : null,
                bottom != null ? bottom.asString() : null,
                width != null ? width.asString() : null, // ignoring 'auto' option, which is the default
                height != null ? height.asString() : null, // ignoring 'auto' option, which is the default
                yearLabel != null ? yearLabel.resolve() : null
        );
    }

    enum Orient {
        horizontal, vertical
    }

    abstract static class CellDimension {
        abstract String asString();

        static CellDimension ofAuto() {
            return new AutoDimension();
        }

        static CellDimension of(int dim) {
            return new NumDimension(dim);
        }
    }

    static class NumDimension extends CellDimension {
        final int dim;

        NumDimension(int dim) {
            this.dim = dim;
        }

        @Override
        String asString() {
            return String.valueOf(dim);
        }
    }

    static class AutoDimension extends CellDimension {
        @Override
        String asString() {
            return "'auto'";
        }
    }
}
