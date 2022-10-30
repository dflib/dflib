package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.exp.ConstExp;

import java.time.LocalDate;

/**
 * @since 0.16
 */
public class DateConstExp extends ConstExp<LocalDate> implements DateExp {

    public DateConstExp(LocalDate value) {
        super(value, LocalDate.class);
    }
}
