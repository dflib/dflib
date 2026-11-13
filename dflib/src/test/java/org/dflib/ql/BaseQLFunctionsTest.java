package org.dflib.ql;

import org.dflib.Environment;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;

public abstract class BaseQLFunctionsTest {

    private QLContext originalContext;

    @BeforeEach
    public void saveContext() {
        this.originalContext = Environment.commonEnv().qlContext();
    }

    @AfterEach
    public void restoreContext() {
        Environment.setQLContext(originalContext);
    }
}
