package com.nhl.dflib.exp.num;

import com.nhl.dflib.exp.ColumnExp;
import com.nhl.dflib.NumExp;

/**
 * @since 0.11
 */
public class IntColumn extends ColumnExp<Integer> implements NumExp<Integer> {

    public IntColumn(String name) {
        super(name, Integer.class);
    }

    public IntColumn(int position) {
        super(position, Integer.class);
    }
}
