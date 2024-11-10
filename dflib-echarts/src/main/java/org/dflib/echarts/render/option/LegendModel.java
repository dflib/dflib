package org.dflib.echarts.render.option;

import java.util.Set;

/**
 * @since 2.0.0
 */
public class LegendModel {

    private final String type;
    private final Boolean show;
    private final String orient;
    private final String top;
    private final String bottom;
    private final String left;
    private final String right;
    private final Set<String> unselected;

    public LegendModel(
            Boolean show,
            String type,
            String orient,
            String top,
            String bottom,
            String left,
            String right,
            Set<String> unselected) {
        this.show = show;
        this.type = type;
        this.orient = orient;
        this.top = top;
        this.bottom = bottom;
        this.left = left;
        this.right = right;
        this.unselected = unselected;
    }

    public Boolean getShow() {
        return show;
    }

    public String getType() {
        return type;
    }

    public String getOrient() {
        return orient;
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

    public boolean isUnselectedPresent() {
        return unselected != null;
    }

    public Set<String> getUnselected() {
        return unselected;
    }
}
