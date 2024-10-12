package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.exp.ConstExp;

import java.time.LocalDateTime;

public class DateTimeConstExp extends ConstExp<LocalDateTime> implements DateTimeExp {

    public DateTimeConstExp(LocalDateTime value) {
        super(value, LocalDateTime.class);
    }
}
