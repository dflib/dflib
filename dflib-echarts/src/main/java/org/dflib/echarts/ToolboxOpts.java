package org.dflib.echarts;

/**
 * @since 1.0.0-M21
 */
public class ToolboxOpts {

    private boolean featureDataZoom;
    private boolean featureSaveAsImage;
    private boolean featureRestore;

    public static ToolboxOpts create() {
        return new ToolboxOpts();
    }

    public ToolboxOpts featureSaveAsImage() {
        this.featureSaveAsImage = Boolean.TRUE;
        return this;
    }

    public ToolboxOpts featureRestore() {
        this.featureRestore = true;
        return this;
    }

    public ToolboxOpts featureDataZoom() {
        this.featureDataZoom = true;
        return this;
    }

    public boolean isFeatureSaveAsImage() {
        return featureSaveAsImage;
    }

    public boolean isFeatureRestore() {
        return featureRestore;
    }

    public boolean isFeatureDataZoom() {
        return featureDataZoom;
    }
}
