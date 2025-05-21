package org.dflib.exp.num;

import org.dflib.exp.bool.BoolScalarExp;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoolScalarExpTest {

    @Test
    public void toQL() {
        BoolScalarExp e1 = new BoolScalarExp(true);
        assertEquals("true", e1.toQL());

        BoolScalarExp e2 = new BoolScalarExp(false);
        assertEquals("false", e2.toQL());
    }
}
