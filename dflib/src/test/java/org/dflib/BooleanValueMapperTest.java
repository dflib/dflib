package org.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class BooleanValueMapperTest {

    @Deprecated
    @Test
    void fromObject() {

        assertFalse(BoolValueMapper.fromObject().map(null));

        assertTrue(BoolValueMapper.fromObject().map(Boolean.TRUE));
        assertFalse(BoolValueMapper.fromObject().map(Boolean.FALSE));

        assertTrue(BoolValueMapper.fromObject().map("true"));
        assertFalse(BoolValueMapper.fromObject().map("false"));
        assertFalse(BoolValueMapper.fromObject().map("OTHER"));

        // different from ".of()"
        assertFalse(BoolValueMapper.fromObject().map(1));
        assertFalse(BoolValueMapper.fromObject().map(0));
    }

    @Test
    void of() {

        assertFalse(BoolValueMapper.of().map(null));

        assertTrue(BoolValueMapper.of().map(Boolean.TRUE));
        assertFalse(BoolValueMapper.of().map(Boolean.FALSE));

        assertTrue(BoolValueMapper.of().map("true"));
        assertFalse(BoolValueMapper.of().map("false"));
        assertFalse(BoolValueMapper.of().map("OTHER"));

        assertTrue(BoolValueMapper.of().map(1));
        assertFalse(BoolValueMapper.of().map(0));
    }

    @Test
    void ofString() {
        assertFalse(BoolValueMapper.ofString().map(null));

        assertTrue(BoolValueMapper.ofString().map("true"));
        assertFalse(BoolValueMapper.ofString().map("false"));

        assertTrue(BoolValueMapper.ofString().map("TRUE"));
        assertFalse(BoolValueMapper.ofString().map("FALSE"));

        assertFalse(BoolValueMapper.ofString().map("OTHER"));
    }
}
