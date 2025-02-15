package org.dflib.exp.datetime;

import org.dflib.DateExp;
import org.dflib.Exp;
import org.dflib.exp.AsExp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class DateAsExp extends AsExp<LocalDate> implements DateExp {

    public DateAsExp(String name, Exp<LocalDate> delegate) {
        super(name, delegate);
    }

    @Override
    public DateExp as(String name) {
        return Objects.equals(name, this.name) ? this : DateExp.super.as(name);
    }
}
