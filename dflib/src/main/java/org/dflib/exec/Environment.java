package org.dflib.exec;


import org.dflib.print.Printer;

import java.util.concurrent.ExecutorService;

/**
 * @deprecated moved to another package. See {@link org.dflib.Environment}
 */
@Deprecated(since = "1.0.0-M20", forRemoval = true)
public class Environment extends org.dflib.Environment {

    protected Environment(ExecutorService threadPool, int parallelExecThreshold, Printer printer) {
        super(threadPool, parallelExecThreshold, printer);
    }
}
