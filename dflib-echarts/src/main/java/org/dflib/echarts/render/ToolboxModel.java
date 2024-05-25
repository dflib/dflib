package org.dflib.echarts.render;

import org.dflib.echarts.render.toolbox.FeatureDataZoomModel;
import org.dflib.echarts.render.toolbox.FeatureRestoreModel;
import org.dflib.echarts.render.toolbox.FeatureSaveAsImageModel;

/**
 * A model for rendering EChart toolbox
 *
 * @since 1.0.0-M21
 */
public class ToolboxModel {

    private final FeatureDataZoomModel dataZoom;
    private final FeatureSaveAsImageModel saveAsImage;
    private final FeatureRestoreModel restore;

    public ToolboxModel(
            FeatureDataZoomModel dataZoom,
            FeatureSaveAsImageModel saveAsImage,
            FeatureRestoreModel restore) {
        this.dataZoom = dataZoom;
        this.saveAsImage = saveAsImage;
        this.restore = restore;
    }

    public FeatureSaveAsImageModel getSaveAsImage() {
        return saveAsImage;
    }

    public FeatureDataZoomModel getDataZoom() {
        return dataZoom;
    }

    public FeatureRestoreModel getRestore() {
        return restore;
    }
}
