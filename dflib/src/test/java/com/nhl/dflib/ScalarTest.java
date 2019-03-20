package com.nhl.dflib;

import org.junit.Test;

import static org.junit.Assert.*;

public class ScalarTest {

    @Test
    public void testGet() {
        Scalar<Integer> S = Scalar.at(2);
        Object[] a = new Object[]{1, "a", 3, 8};
        assertEquals(Integer.valueOf(3), S.get(a));
    }

    @Test
    public void testSet() {
        Scalar<Integer> S = Scalar.at(2);
        Object[] a = new Object[4];
        S.set(a, 6);
        assertEquals(Integer.valueOf(6), a[2]);
    }

    @Test
    public void testGet_FromEnum() {

        Object[] arr = new Object[]{1, "a", 3, 8};
        assertEquals(Integer.valueOf(1), DFDescriptor.A.get(arr));
        assertEquals("a", DFDescriptor.B.get(arr));
        assertEquals(Integer.valueOf(3), DFDescriptor.C.get(arr));
    }

    interface DFDescriptor {
        Scalar<String> A = Scalar.at(E1.a);
        Scalar<String> B = Scalar.at(E1.b);
        Scalar<String> C = Scalar.at(E1.c);

        enum E1 {a, b, c}
    }

}
