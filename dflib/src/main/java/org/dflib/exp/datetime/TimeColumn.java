package org.dflib.exp.datetime;

import org.dflib.TimeExp;
import org.dflib.exp.Column;

import java.time.LocalTime;

public class TimeColumn extends Column<LocalTime> implements TimeExp {

    public TimeColumn(String name) {
        super(name, LocalTime.class);
    }

    public TimeColumn(int position) {
        super(position, LocalTime.class);
    }

    @Override
    public String getColumnName() {
        return position >= 0 ? "time(" + position + ")" : name;
    }
}
