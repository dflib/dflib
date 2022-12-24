package com.nhl.dflib.exp.datetime;

import com.nhl.dflib.DateTimeExp;
import com.nhl.dflib.exp.ConstExp;

import java.time.LocalDateTime;

/**
 * @since 0.16
 */
public class DateTimeConstExp extends ConstExp<LocalDateTime> implements DateTimeExp {

    public DateTimeConstExp(LocalDateTime value) {
        super(value, LocalDateTime.class);
    }
}
