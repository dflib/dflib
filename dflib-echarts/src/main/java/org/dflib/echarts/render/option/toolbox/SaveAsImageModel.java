package org.dflib.echarts.render.option.toolbox;

/**
 * @since 1.0.0-M21
 */
public class SaveAsImageModel {

    private final Integer pixelRatio;

    public SaveAsImageModel(Integer pixelRatio) {
        this.pixelRatio = pixelRatio;
    }

    public Integer getPixelRatio() {
        return pixelRatio;
    }
}
