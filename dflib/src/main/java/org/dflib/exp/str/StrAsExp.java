package org.dflib.exp.str;

import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.exp.AsExp;

import java.util.Objects;

/**
 * @since 2.0.0
 */
public class StrAsExp extends AsExp<String> implements StrExp {

    public StrAsExp(String name, Exp<String> delegate) {
        super(name, delegate);
    }

    @Override
    public StrExp as(String name) {
        return Objects.equals(name, this.name) ? this : StrExp.super.as(name);
    }
}
