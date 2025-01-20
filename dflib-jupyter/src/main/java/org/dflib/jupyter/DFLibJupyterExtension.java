package org.dflib.jupyter;

import org.dflib.jjava.jupyter.Extension;
import org.dflib.jjava.jupyter.kernel.BaseKernel;

/**
 * A JJava kernel extension that preloads DFLib common imports and Jupyter renderers for DataFrame and Series.
 */
public class DFLibJupyterExtension implements Extension {

    private static final String STARTUP_SCRIPT = "" +
            "import org.dflib.*;\n" +
            "import org.dflib.http.*;\n" +
            "import org.dflib.fs.*;\n" +
            "import org.dflib.zip.*;\n" +
            "import org.dflib.avro.*;\n" +
            "import org.dflib.csv.*;\n" +
            "import org.dflib.echarts.*;\n" +
            "import org.dflib.excel.*;\n" +
            "import org.dflib.jdbc.*;\n" +
            "import org.dflib.json.*;\n" +
            "import org.dflib.jupyter .*;\n" +
            "import org.dflib.parquet.*;" +

            "import static org.dflib.Exp.*;\n" +

            // must call init method here, to process everything in order inside code executor
            "DFLibJupyter.init(getKernelInstance());";

    @Override
    public void install(BaseKernel kernel) {
        try {
            kernel.eval(STARTUP_SCRIPT);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
