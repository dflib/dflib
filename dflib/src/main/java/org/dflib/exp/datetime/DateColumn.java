package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.exp.Column;

import java.time.LocalDate;


public class DateColumn extends Column<LocalDate> implements DateExp {

    public DateColumn(String name) {
        super(name, LocalDate.class);
    }

    public DateColumn(int position) {
        super(position, LocalDate.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$date(" + position + ")" : name;
    }
}
