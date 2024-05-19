package org.dflib.echarts;

/**
 * Contains rendered parts of an EChart. Each part is made of a set of HTML tags.
 *
 * @since 1.0.0-M21
 */
public class EChart {

    private final String container;
    private final String externalScript;
    private final String script;

    public EChart(String container, String externalScript, String script) {
        this.container = container;
        this.externalScript = externalScript;
        this.script = script;
    }

    public String getContainer() {
        return container;
    }

    public String getExternalScript() {
        return externalScript;
    }

    public String getScript() {
        return script;
    }
}
