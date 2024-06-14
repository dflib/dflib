package org.dflib.echarts.render.option;

public class EncodeModel {

    private final Integer x;
    private final Integer y;
    private final Integer value;
    private final Integer itemName;

    public EncodeModel(Integer x, Integer y, Integer itemName, Integer value) {
        this.itemName = itemName;
        this.x = x;
        this.y = y;
        this.value = value;
    }

    public Integer getX() {
        return x;
    }

    public Integer getY() {
        return y;
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
