package org.dflib.echarts;

import org.dflib.echarts.render.option.toolbox.SaveAsImageModel;

/**
 * @since 1.0.0-M21
 */
public class SaveAsImage {

    private Integer pixelRatio;

    /**
     * @since 1.0.0-M22
     */
    public static SaveAsImage of() {
        return new SaveAsImage();
    }

    /**
     * @deprecated in favor of {@link #of()}
     */
    @Deprecated(since = "1.0.0-M22", forRemoval = true)
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
