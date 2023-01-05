package com.nhl.dflib;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ValueMapperTest {

    @Test
    public void testAnd() {
        ValueMapper<String, Integer> trimming = ValueMapper.stringTrim().and(ValueMapper.stringToInt());
        assertNull(trimming.map(null));
        assertNull(trimming.map(""));
        assertNull(trimming.map(" "));
        assertEquals(Integer.valueOf(5), trimming.map("5 "));
    }

    @Test
    public void testStringToBoolean() {
        ValueMapper<String, Boolean> vm = ValueMapper.stringToBool();
        assertNull(vm.map(null));
        assertEquals(false, vm.map(""));
        assertEquals(true, vm.map("TRUE"));
        assertEquals(false, vm.map("false"));
        assertEquals(false, vm.map("nottrue"));
    }
}
