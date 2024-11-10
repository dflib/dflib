package org.dflib.echarts.render.option;

/**
 * @since 2.0.0
 */
public class TitleModel {

    private final String text;
    private final String subtext;
    private final String top;
    private final String bottom;
    private final String left;
    private final String right;

    public TitleModel(
            String text,
            String subtext,
            String top,
            String bottom,
            String left,
            String right
    ) {
        this.text = text;
        this.subtext = subtext;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
    }

    public String getText() {
        return text;
    }

    public String getSubtext() {
        return subtext;
    }

    public String getTop() {
        return top;
    }

    public String getBottom() {
        return bottom;
    }

    public String getLeft() {
        return left;
    }

    public String getRight() {
        return right;
    }
}
