package com.nhl.dflib.exp.str;

import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.ConstExp;

/**
 * @since 0.11
 */
public class StrConstExp extends ConstExp<String> implements StrExp {

    public StrConstExp(String value) {
        super(value, String.class);
    }
}
