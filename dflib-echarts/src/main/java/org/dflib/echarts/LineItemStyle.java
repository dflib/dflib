package org.dflib.echarts;

import org.dflib.echarts.render.option.series.ItemStyleModel;

/**
 * @since 1.1.0
 */
public class LineItemStyle {

    ValOrSeries<String> color;
    private Double opacity;

    public static LineItemStyle of() {
        return new LineItemStyle();
    }

    public LineItemStyle color(String color) {
        this.color = ValOrSeries.ofVal(color);
        return this;
    }

    /**
     * Will generate style color using a dynamic value coming from the specified DataFrame column, essentially
     * providing an extra visual dimension.
     *
     * @since 2.0.0
     */
    public LineItemStyle colorData(String colorDataColumn) {
        this.color = ValOrSeries.ofSeries(colorDataColumn);
        return this;
    }

    public LineItemStyle opacity(double opacity) {
        this.opacity = opacity;
        return this;
    }

    ItemStyleModel resolve(Integer symbolSizeColorDimension) {

        String color = this.color != null
                ? (this.color.isVal() ? this.color.valQuotedString() : ValOrSeries.jsFunctionWithObjectParam(symbolSizeColorDimension))
                : null;

        return new ItemStyleModel(
                color,
                null,
                null,
                null,
                null,
                null,
                null,
                null,
                opacity);
    }
}
