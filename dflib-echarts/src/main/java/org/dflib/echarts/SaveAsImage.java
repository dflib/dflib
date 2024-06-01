package org.dflib.echarts;

import org.dflib.echarts.render.option.toolbox.SaveAsImageModel;

/**
 * @since 1.0.0-M21
 */
public class SaveAsImage {

    private Integer pixelRatio;

    public static SaveAsImage create() {
        return new SaveAsImage();
    }

    protected SaveAsImage() {
    }

    public SaveAsImage pixelRatio(int pixelRatio) {
        this.pixelRatio = pixelRatio;
        return this;
    }

    SaveAsImageModel resolve() {
        return new SaveAsImageModel(pixelRatio);
    }
}
