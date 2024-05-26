package org.dflib.echarts.render.option;

import org.dflib.echarts.render.option.toolbox.DataZoomModel;
import org.dflib.echarts.render.option.toolbox.RestoreModel;
import org.dflib.echarts.render.option.toolbox.SaveAsImageModel;

/**
 * A model for rendering EChart toolbox
 *
 * @since 1.0.0-M21
 */
public class ToolboxModel {

    private final DataZoomModel dataZoom;
    private final SaveAsImageModel saveAsImage;
    private final RestoreModel restore;

    public ToolboxModel(
            DataZoomModel dataZoom,
            SaveAsImageModel saveAsImage,
            RestoreModel restore) {
        this.dataZoom = dataZoom;
        this.saveAsImage = saveAsImage;
        this.restore = restore;
    }

    public SaveAsImageModel getSaveAsImage() {
        return saveAsImage;
    }

    public DataZoomModel getDataZoom() {
        return dataZoom;
    }

    public RestoreModel getRestore() {
        return restore;
    }
}
