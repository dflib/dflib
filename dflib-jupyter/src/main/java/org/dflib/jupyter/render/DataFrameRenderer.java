package org.dflib.jupyter.render;

import org.dflib.DataFrame;
import org.dflib.print.Printer;
import io.github.spencerpark.jupyter.kernel.display.RenderContext;
import io.github.spencerpark.jupyter.kernel.display.RenderFunction;
import io.github.spencerpark.jupyter.kernel.display.mime.MIMEType;

public class DataFrameRenderer implements RenderFunction<DataFrame> {

    private Printer printer;

    public DataFrameRenderer(Printer printer) {
        this.printer = printer;
    }

    @Override
    public void render(DataFrame df, RenderContext context) {
        context.renderIfRequested(MIMEType.TEXT_PLAIN, () -> printer.toString(df));
    }
}
