package org.dflib.echarts;

import org.dflib.echarts.render.option.toolbox.ToolboxModel;
import org.dflib.echarts.render.option.toolbox.DataZoomModel;
import org.dflib.echarts.render.option.toolbox.RestoreModel;

public class Toolbox {

    private boolean featureDataZoom;
    private SaveAsImage featureSaveAsImage;
    private boolean featureRestore;

    public static Toolbox of() {
        return new Toolbox();
    }

    public Toolbox featureSaveAsImage() {
        this.featureSaveAsImage = SaveAsImage.of();
        return this;
    }

    public Toolbox featureSaveAsImage(SaveAsImage saveAsImage) {
        this.featureSaveAsImage = saveAsImage;
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
                featureDataZoom ? new DataZoomModel() : null,
                featureSaveAsImage != null ? featureSaveAsImage.resolve() : null,
                featureRestore ? new RestoreModel() : null
        );
    }
}
