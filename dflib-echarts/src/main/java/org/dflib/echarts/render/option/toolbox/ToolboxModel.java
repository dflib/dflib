package org.dflib.echarts.render.option.toolbox;

/**
 * A model for rendering EChart toolbox
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
