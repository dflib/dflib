package org.dflib.ql.fn;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.time.LocalDate;

import static org.dflib.Exp.$bigintVal;
import static org.dflib.Exp.$boolVal;
import static org.dflib.Exp.$dateVal;
import static org.dflib.Exp.$doubleVal;
import static org.dflib.Exp.$floatVal;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$longVal;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.$val;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FunctionUtils_ScalarTest {

    @Test
    public void integer() {
        assertEquals(3, FunctionUtils.scalar($intVal(3), int.class));
        assertEquals(3, FunctionUtils.scalar($longVal(3L), Integer.class));
        assertEquals(3, FunctionUtils.scalar($bigintVal(BigInteger.valueOf(3)), int.class));
        assertEquals(3L, FunctionUtils.scalar($intVal(3), long.class));
        assertEquals(3_000_000_000L, FunctionUtils.scalar($longVal(3_000_000_000L), Long.class));
    }

    @Test
    public void integer_Fraction_throws() {
        assertEquals("Not an integer constant: 1.5", assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($doubleVal(1.5), int.class)).getMessage());
        assertThrows(IllegalArgumentException.class, () -> FunctionUtils.scalar($strVal("1"), long.class));
        assertThrows(IllegalArgumentException.class, () -> FunctionUtils.scalar($val(null), int.class));
    }

    @Test
    public void integer_OutOfRange_throws() {
        assertEquals("Integer constant out of range [-2147483648..2147483647]: 3000000000",
                assertThrows(IllegalArgumentException.class,
                        () -> FunctionUtils.scalar($longVal(3_000_000_000L), int.class)).getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($bigintVal(BigInteger.TWO.pow(64)), long.class));
    }

    @Test
    public void decimal() {
        assertEquals(0.5, FunctionUtils.scalar($doubleVal(0.5), double.class));
        assertEquals(1.0, FunctionUtils.scalar($intVal(1), Double.class));

        // a Float literal keeps its decimal value
        assertEquals(0.1, FunctionUtils.scalar($floatVal(0.1f), double.class));

        assertEquals("Not a numeric constant: 'x'", assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($strVal("x"), double.class)).getMessage());
    }

    @Test
    public void reference() {
        assertEquals("a", FunctionUtils.scalar($strVal("a"), String.class));
        assertEquals(LocalDate.of(2024, 1, 2), FunctionUtils.scalar($dateVal(LocalDate.of(2024, 1, 2)), LocalDate.class));
        assertEquals(1.5, FunctionUtils.scalar($doubleVal(1.5), Number.class));
        assertEquals("a", FunctionUtils.scalar($strVal("a"), Object.class));
        assertNull(FunctionUtils.scalar($val(null), String.class));

        assertEquals("Not a String constant: 1", assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($intVal(1), String.class)).getMessage());
    }

    @Test
    public void primitive() {
        assertEquals(true, FunctionUtils.scalar($boolVal(true), boolean.class));
        assertThrows(IllegalArgumentException.class, () -> FunctionUtils.scalar($val(null), boolean.class));
    }

    @Test
    public void notConstant_throws() {
        assertEquals("Not a constant: a", assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($int("a"), int.class)).getMessage());
        assertEquals("Not a constant: 1 + 2", assertThrows(IllegalArgumentException.class,
                () -> FunctionUtils.scalar($intVal(1).add(2), int.class)).getMessage());
    }
}
