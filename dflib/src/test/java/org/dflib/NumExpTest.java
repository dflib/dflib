package org.dflib;

import org.dflib.exp.BaseExpTest;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;

public class NumExpTest extends BaseExpTest {

    @Test
    public void equals_MixedTypes() {
        assertExpEquals(
                $int("a").add(5L),
                $int("a").add(5L),
                $int("a").add(5));
    }

    @Test
    public void hashCode_MixedTypes() {
        assertExpHashCode(
                $int("a").add(5L),
                $int("a").add(5L),
                $int("a").add(5));
    }
}
