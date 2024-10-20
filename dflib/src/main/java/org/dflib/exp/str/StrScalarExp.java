package org.dflib.exp.str;

import org.dflib.StrExp;
import org.dflib.exp.ScalarExp;

/**
 * @since 2.0.0
 */
public class StrScalarExp extends ScalarExp<String> implements StrExp {

    public StrScalarExp(String value) {
        super(value, String.class);
    }
}
