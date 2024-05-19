package org.dflib.jupyter.render;

import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;
import org.dflib.echarts.EChart;

/**
 * Renders {@link EChart} object as HTML with embedded JS.
 *
 * @since 1.0.0-M21
 */
public class EChartRenderer implements RenderFunction<EChart> {

    @Override
    public void render(EChart data, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_HTML, () -> data.getContent());
        context.renderIfRequested(MIMEType.TEXT_PLAIN, () -> data.getContent());
    }
}
