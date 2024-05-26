package org.dflib.echarts;

import org.dflib.echarts.render.option.toolbox.SaveAsImageModel;

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
