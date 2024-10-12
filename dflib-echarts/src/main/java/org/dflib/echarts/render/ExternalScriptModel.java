package org.dflib.echarts.render;

import java.util.Objects;

/**
 * A model for rendering EChart external script
 */
public class ExternalScriptModel {

    private final String scriptUrl;

    public ExternalScriptModel(String scriptUrl) {
        this.scriptUrl = Objects.requireNonNull(scriptUrl);
    }

    public String getScriptUrl() {
        return scriptUrl;
    }
}
