package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.TimeExp;
import com.nhl.dflib.exp.ConstExp;

import java.time.LocalTime;

/**
 * @since 0.16
 */
public class TimeConstExp extends ConstExp<LocalTime> implements TimeExp {

    public TimeConstExp(LocalTime value) {
        super(value, LocalTime.class);
    }
}
