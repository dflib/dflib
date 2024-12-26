package org.dflib.exp.datetime;

import org.dflib.OffsetDateTimeExp;
import org.dflib.exp.ConstExp;

import java.time.OffsetDateTime;

/**
 * @since 1.1.0
 */
public class OffsetDateTimeConstExp extends ConstExp<OffsetDateTime> implements OffsetDateTimeExp {

    public OffsetDateTimeConstExp(OffsetDateTime value) {
        super(value, OffsetDateTime.class);
    }
}
