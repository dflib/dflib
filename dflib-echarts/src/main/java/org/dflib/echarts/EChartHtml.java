package org.dflib.echarts;

/**
 * Contains parts of an EChart in HTML format.
 */
public class EChartHtml {

    private final String container;
    private final String externalScript;
    private final String script;

    public EChartHtml(String container, String externalScript, String script) {
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
