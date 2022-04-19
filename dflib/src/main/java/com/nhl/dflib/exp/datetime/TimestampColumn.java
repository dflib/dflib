package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.exp.GenericColumn;

import java.sql.Timestamp;

public class TimestampColumn extends GenericColumn<Timestamp> implements TimestampExp {

    public TimestampColumn(String name) {
        super(name, Timestamp.class);
    }

    public TimestampColumn(int position) {
        super(position, Timestamp.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$timestamp(" + position + ")" : name;
    }
}
