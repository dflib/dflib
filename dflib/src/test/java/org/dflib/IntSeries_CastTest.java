package org.dflib;

import org.dflib.IntSeries;
import org.dflib.series.IntArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class IntSeries_CastTest {

    @Test
    public void unsafeCastAs() {
        IntSeries s = new IntArraySeries(1, 2);
        assertDoesNotThrow(() -> s.unsafeCastAs(String.class));
        assertDoesNotThrow(() -> s.unsafeCastAs(Integer.class));
    }

    @Test
    public void castAs() {
        IntSeries s = new IntArraySeries(1, 2);
        assertDoesNotThrow(() -> s.castAs(Integer.class));
        assertDoesNotThrow(() -> s.castAs(Integer.TYPE));
        assertThrows(ClassCastException.class, () -> s.castAs(String.class));
    }

    @Test
    public void castAs_Upcast() {
        IntSeries s = new IntArraySeries(1, 2);
        assertDoesNotThrow(() -> s.castAs(Integer.class));
        assertDoesNotThrow(() -> s.castAs(Number.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Long.class));
    }

    @Test
    public void castAsPrimitive() {
        IntSeries s = new IntArraySeries(1, 2);
        assertDoesNotThrow(() -> s.castAsInt());
        assertThrows(ClassCastException.class, () -> s.castAsBool());
        assertThrows(ClassCastException.class, () -> s.castAsDouble());
        assertThrows(ClassCastException.class, () -> s.castAsLong());
    }
}
