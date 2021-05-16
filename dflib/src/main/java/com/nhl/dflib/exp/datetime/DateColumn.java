package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.exp.ColumnExp;

import java.time.LocalDate;

/**
 * @since 0.11
 */
public class DateColumn extends ColumnExp<LocalDate> implements DateExp {

    public DateColumn(String name) {
        super(name, LocalDate.class);
    }

    public DateColumn(int position) {
        super(position, LocalDate.class);
    }
}
