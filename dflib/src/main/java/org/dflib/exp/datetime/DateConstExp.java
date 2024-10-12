package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.exp.ConstExp;

import java.time.LocalDate;

public class DateConstExp extends ConstExp<LocalDate> implements DateExp {

    public DateConstExp(LocalDate value) {
        super(value, LocalDate.class);
    }
}
