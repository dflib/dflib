package org.dflib.echarts.render.option;

import java.util.List;

public class EncodeModel {

    private final Integer x;
    private final List<Integer> ys;
    private final Integer value;
    private final Integer itemName;

    public EncodeModel(Integer x, List<Integer> ys, Integer itemName, Integer value) {
        this.itemName = itemName;
        this.x = x;
        this.ys = ys;
        this.value = value;
    }

    public Integer getX() {
        return x;
    }

    /**
     * @since 1.0.0-M23
     */
    public boolean isYsPresent() {
        return ys != null && ys.size() > 1;
    }

    /**
     * @since 1.0.0-M23
     */
    public List<Integer> getYs() {
        return ys;
    }

    /**
     * @since 1.0.0-M23
     */
    public boolean isYPresent() {
        return ys != null && ys.size() == 1;
    }

    public Integer getY() {
        return ys.get(0);
    }

    /**
     * @since 1.0.0-M22
     */
    public Integer getItemName() {
        return itemName;
    }

    /**
     * @since 1.0.0-M22
     */
    public Integer getValue() {
        return value;
    }
}
