package org.dflib.echarts;

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
