package org.dflib.exp.datetime;

import org.dflib.OffsetDateTimeExp;
import org.dflib.exp.Column;

import java.time.OffsetDateTime;

/**
 * @since 1.1.0
 */
public class OffsetDateTimeColumn extends Column<OffsetDateTime> implements OffsetDateTimeExp {

    public OffsetDateTimeColumn(String name) {
        super(name, OffsetDateTime.class);
    }

    public OffsetDateTimeColumn(int position) {
        super(position, OffsetDateTime.class);
    }

    @Override
    public String toQL() {
        return position >= 0 ? "$offsetDateTime(" + position + ")" : name;
    }
}
