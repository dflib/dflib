package org.dflib.exp.num;

import org.dflib.NumExp;
import org.dflib.exp.ScalarExp;

public class IntScalarExp extends ScalarExp<Integer> implements NumExp<Integer> {

    public IntScalarExp(int value) {
        super(value, Integer.class);
    }
}
