package org.dflib.echarts;

import org.dflib.echarts.render.option.ToolboxModel;
import org.dflib.echarts.render.option.toolbox.FeatureDataZoomModel;
import org.dflib.echarts.render.option.toolbox.FeatureRestoreModel;
import org.dflib.echarts.render.option.toolbox.FeatureSaveAsImageModel;

/**
 * @since 1.0.0-M21
 */
public class Toolbox {

    private boolean featureDataZoom;
    private boolean featureSaveAsImage;
    private boolean featureRestore;

    public static Toolbox create() {
        return new Toolbox();
    }

    public Toolbox featureSaveAsImage() {
        this.featureSaveAsImage = Boolean.TRUE;
        return this;
    }

    public Toolbox featureRestore() {
        this.featureRestore = true;
        return this;
    }

    public Toolbox featureDataZoom() {
        this.featureDataZoom = true;
        return this;
    }

    protected ToolboxModel resolve() {
        return new ToolboxModel(
                featureDataZoom ? new FeatureDataZoomModel() : null,
                featureSaveAsImage ? new FeatureSaveAsImageModel() : null,
                featureRestore ? new FeatureRestoreModel() : null
        );
    }
}
