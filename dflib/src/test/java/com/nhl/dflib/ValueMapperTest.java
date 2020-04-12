package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueMapperTest {

    @Test
    public void testAnd() {
        ValueMapper<String, Integer> trimming = ValueMapper.stringTrim().and(ValueMapper.stringToInt());
        assertEquals(null, trimming.map(null));
        assertEquals(null, trimming.map(""));
        assertEquals(null, trimming.map(" "));
        assertEquals(Integer.valueOf(5), trimming.map("5 "));
    }
}
