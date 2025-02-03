package org.dflib.exp.bool;

import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.and;

public class AndConditionTest extends BaseExpTest {

    @Test
    public void testEquals() {
        assertExpEquals(
                and($bool("a"), $bool("b")),
                and($bool("a"), $bool("b")),
                and($bool("a"), $bool("b"), $bool("c")));
    }

    @Test
    public void testHashCode() {
        assertExpHashCode(
                and($bool("a"), $bool("b")),
                and($bool("a"), $bool("b")),
                and($bool("a"), $bool("b"), $bool("c")));
    }
}
