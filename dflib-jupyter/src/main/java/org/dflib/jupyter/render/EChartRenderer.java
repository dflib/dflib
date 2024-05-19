package org.dflib.jupyter.render;

import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;
import org.dflib.echarts.EChart;

import java.util.Set;

/**
 * Renders {@link EChart} object as HTML with embedded JS.
 *
 * @since 1.0.0-M21
 */
public class EChartRenderer implements RenderFunction<EChart> {

    final static Set<MIMEType> SUPPORTED_TYPES = Set.of(MIMEType.TEXT_HTML, MIMEType.TEXT_PLAIN);

    @Override
    public void render(EChart data, RenderContext context) {
        boolean canRender = SUPPORTED_TYPES.stream().filter(context::wantsDataRenderedAs).findFirst().isPresent();

        if (canRender) {
            String content = toString(data);
            SUPPORTED_TYPES.stream().forEach(t -> context.renderIfRequested(t, () -> content));
        }
    }

    private String toString(EChart chart) {
        return chart.getExternalScript() + chart.getContainer() + chart.getScript();
    }
}
