package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.TimeExp;
import com.nhl.dflib.exp.Column;

import java.time.LocalTime;

/**
 * @since 0.16
 */
public class TimeColumn extends Column<LocalTime> implements TimeExp {

    public TimeColumn(String name) {
        super(name, LocalTime.class);
    }

    public TimeColumn(int position) {
        super(position, LocalTime.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$time(" + position + ")" : name;
    }
}
