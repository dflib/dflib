package com.nhl.dflib;

import com.nhl.dflib.series.LongArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class LongSeries_CastTest {

    @Test
    public void unsafeCastAs() {
        LongSeries s = new LongArraySeries(1L, 2L);
        assertDoesNotThrow(() -> s.unsafeCastAs(String.class));
        assertDoesNotThrow(() -> s.unsafeCastAs(Long.class));
    }

    @Test
    public void castAs() {
        LongSeries s = new LongArraySeries(1L, 2L);
        assertDoesNotThrow(() -> s.castAs(Long.class));
        assertDoesNotThrow(() -> s.castAs(Long.TYPE));
        assertThrows(ClassCastException.class, () -> s.castAs(String.class));
    }

    @Test
    public void castAs_Upcast() {
        LongSeries s = new LongArraySeries(1L, 2L);
        assertDoesNotThrow(() -> s.castAs(Long.class));
        assertDoesNotThrow(() -> s.castAs(Number.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Integer.class));
    }

    @Test
    public void castAsPrimitive() {
        LongSeries s = new LongArraySeries(1L, 2L);
        assertDoesNotThrow(() -> s.castAsLong());
        assertThrows(ClassCastException.class, () -> s.castAsBool());
        assertThrows(ClassCastException.class, () -> s.castAsInt());
        assertThrows(ClassCastException.class, () -> s.castAsDouble());
    }
}
