package com.nhl.dflib.exp.str;

import com.nhl.dflib.StrExp;
import com.nhl.dflib.exp.SingleValueExp;

/**
 * @since 0.11
 */
public class StrSingleValueExp extends SingleValueExp<String> implements StrExp {

    public StrSingleValueExp(String value) {
        super(value, String.class);
    }
}
