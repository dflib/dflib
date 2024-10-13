package org.dflib;

import org.dflib.series.FloatArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FloatSeries_CastTest {

    @Test
    public void unsafeCastAs() {
        FloatSeries s = new FloatArraySeries(1.f, 2.f);
        assertDoesNotThrow(() -> s.unsafeCastAs(String.class));
        assertDoesNotThrow(() -> s.unsafeCastAs(Float.class));
    }

    @Test
    public void castAs() {
        FloatSeries s = new FloatArraySeries(1.f, 2.f);
        assertDoesNotThrow(() -> s.castAs(Float.class));
        assertDoesNotThrow(() -> s.castAs(Float.TYPE));
        assertThrows(ClassCastException.class, () -> s.castAs(String.class));
    }

    @Test
    public void castAs_Upcast() {
        FloatSeries s = new FloatArraySeries(1.f, 2.f);
        assertDoesNotThrow(() -> s.castAs(Float.class));
        assertDoesNotThrow(() -> s.castAs(Number.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Integer.class));
    }

    @Test
    public void castAsPrimitive() {
        FloatSeries s = new FloatArraySeries(1.f, 2.f);
        assertDoesNotThrow(() -> s.castAsFloat());
        assertThrows(ClassCastException.class, () -> s.castAsBool());
        assertThrows(ClassCastException.class, () -> s.castAsInt());
        assertThrows(ClassCastException.class, () -> s.castAsLong());
        assertThrows(ClassCastException.class, () -> s.castAsDouble());
    }
}
