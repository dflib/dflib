package org.dflib.jupyter;

import org.dflib.jjava.jupyter.Extension;
import org.dflib.jjava.jupyter.kernel.BaseKernel;

/**
 * @since 1.0.0-M23
 */
public class DFLibJupyterExtension implements Extension {

    private static final String STARTUP_SCRIPT =
                    "import org.dflib.jupyter.*;\n" +
                    "import org.dflib.*;\n" +
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
