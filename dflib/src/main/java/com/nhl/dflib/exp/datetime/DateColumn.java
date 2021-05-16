package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateExp;
import com.nhl.dflib.exp.GenericColumn;

import java.time.LocalDate;

/**
 * @since 0.11
 */
public class DateColumn extends GenericColumn<LocalDate> implements DateExp {

    public DateColumn(String name) {
        super(name, LocalDate.class);
    }

    public DateColumn(int position) {
        super(position, LocalDate.class);
    }

    @Override
    public String getName() {
        return position >= 0 ? "$date(" + position + ")" : name;
    }
}
