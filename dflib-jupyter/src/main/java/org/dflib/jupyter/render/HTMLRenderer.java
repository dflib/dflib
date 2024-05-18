package org.dflib.jupyter.render;

import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;
import org.dflib.echarts.HTML;

public class HTMLRenderer implements RenderFunction<HTML> {

    @Override
    public void render(HTML data, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_HTML, () -> data.getContent());
    }
}
