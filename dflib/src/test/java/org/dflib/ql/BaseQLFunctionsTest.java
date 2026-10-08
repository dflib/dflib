package org.dflib.ql;

import org.dflib.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseQLFunctionsTest {

    private QLFunctions originalFunctions;

    @BeforeEach
    public void saveFunctions() {
        this.originalFunctions = Environment.commonEnv().qlFunctions();
    }

    @AfterEach
    public void restoreFunctions() {
        Environment.setQLFunctions(originalFunctions);
    }
}
