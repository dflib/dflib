package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.exp.AsExp;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class DateTimeAsExp extends AsExp<LocalDateTime> implements DateTimeExp {

    public DateTimeAsExp(String name, Exp<LocalDateTime> delegate) {
        super(name, delegate);
    }

    @Override
    public DateTimeExp as(String name) {
        return Objects.equals(name, this.name) ? this : DateTimeExp.super.as(name);
    }
}
