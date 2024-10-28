package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.AsExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class NumAsExp<N extends Number> extends AsExp<N> implements NumExp<N> {

    public NumAsExp(String name, Exp<N> delegate) {
        super(name, delegate);
    }

    @Override
    public NumExp<N> as(String name) {
        return Objects.equals(name, this.name) ? this : NumExp.super.as(name);
    }
}
