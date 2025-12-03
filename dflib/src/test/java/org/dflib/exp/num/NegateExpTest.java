package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.junit.jupiter.api.Assertions.*;

class NegateExpTest extends BaseExpTest {

    @Test
    void toQL() {
        assertEquals("-1", $intVal(1).negate().toQL());
        assertEquals("-(-1)", $intVal(-1).negate().toQL());
        assertEquals("-(-1)", $intVal(1).negate().negate().toQL());
        assertEquals("-a", $int("a").negate().toQL());
        assertEquals("-count", Exp.count().negate().toQL());
        assertEquals("-abs(a)", $int("a").abs().negate().toQL());

        assertEquals("-(1 + 2)", $intVal(1).add($intVal(2)).negate().toQL());
        assertEquals("-(a as b)", $int("a").as("b").negate().toQL());
    }
}
