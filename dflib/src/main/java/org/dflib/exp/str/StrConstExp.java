package org.dflib.exp.str;

import org.dflib.StrExp;
import org.dflib.exp.ConstExp;

/**
 * @since 0.11
 */
public class StrConstExp extends ConstExp<String> implements StrExp {

    public StrConstExp(String value) {
        super(value, String.class);
    }
}
