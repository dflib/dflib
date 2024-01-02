package org.dflib.exp.datetime;

import org.dflib.DateTimeExp;
import org.dflib.exp.Column;

import java.time.LocalDateTime;

/**
 * @since 0.16
 */
public class DateTimeColumn extends Column<LocalDateTime> implements DateTimeExp {

    public DateTimeColumn(String name) {
        super(name, LocalDateTime.class);
    }

    public DateTimeColumn(int position) {
        super(position, LocalDateTime.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$dateTime(" + position + ")" : name;
    }
}
