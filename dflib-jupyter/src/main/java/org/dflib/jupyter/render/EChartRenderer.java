package org.dflib.jupyter.render;

import org.dflib.echarts.EChartHtml;
import org.dflib.jjava.jupyter.kernel.display.RenderContext;
import org.dflib.jjava.jupyter.kernel.display.RenderFunction;
import org.dflib.jjava.jupyter.kernel.display.mime.MIMEType;

import java.util.Set;

/**
 * Renders {@link EChartHtml} object as HTML with embedded JS.
 */
public class EChartRenderer implements RenderFunction<EChartHtml> {

    final static Set<MIMEType> SUPPORTED_TYPES = Set.of(MIMEType.TEXT_HTML, MIMEType.TEXT_PLAIN);

    @Override
    public void render(EChartHtml data, RenderContext context) {
        boolean canRender = SUPPORTED_TYPES.stream().anyMatch(context::wantsDataRenderedAs);

        if (canRender) {
            String content = toString(data);
            SUPPORTED_TYPES.forEach(t -> context.renderIfRequested(t, () -> content));
        }
    }

    private String toString(EChartHtml chart) {
        return
                "<script type='text/javascript' src='" + chart.getEchartsUrl() + "'></script>"
                        + chart.getChartDiv()
                        + "<script type='text/javascript'>" + chart.getChartScript() + "</script>";
    }
}
