package com.nhl.dflib;

import com.nhl.dflib.series.DoubleArraySeries;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class DoubleSeries_CastTest {

    @Test
    public void unsafeCastAs() {
        DoubleSeries s = new DoubleArraySeries(1., 2.);
        assertDoesNotThrow(() -> s.unsafeCastAs(String.class));
        assertDoesNotThrow(() -> s.unsafeCastAs(Double.class));
    }

    @Test
    public void castAs() {
        DoubleSeries s = new DoubleArraySeries(1., 2.);
        assertDoesNotThrow(() -> s.castAs(Double.class));
        assertDoesNotThrow(() -> s.castAs(Double.TYPE));
        assertThrows(ClassCastException.class, () -> s.castAs(String.class));
    }

    @Test
    public void castAs_Upcast() {
        DoubleSeries s = new DoubleArraySeries(1., 2.);
        assertDoesNotThrow(() -> s.castAs(Double.class));
        assertDoesNotThrow(() -> s.castAs(Number.class));
        assertThrows(ClassCastException.class, () -> s.castAs(Integer.class));
    }

    @Test
    public void castAsPrimitive() {
        DoubleSeries s = new DoubleArraySeries(1., 2.);
        assertDoesNotThrow(() -> s.castAsDouble());
        assertThrows(ClassCastException.class, () -> s.castAsBool());
        assertThrows(ClassCastException.class, () -> s.castAsInt());
        assertThrows(ClassCastException.class, () -> s.castAsLong());
    }
}
