package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.TimeExp;
import org.dflib.exp.AsExp;

import java.time.LocalTime;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class TimeAsExp extends AsExp<LocalTime> implements TimeExp {

    public TimeAsExp(String name, Exp<LocalTime> delegate) {
        super(name, delegate);
    }

    @Override
    public TimeExp as(String name) {
        return Objects.equals(name, this.name) ? this : new TimeAsExp(name, delegate);
    }
}
