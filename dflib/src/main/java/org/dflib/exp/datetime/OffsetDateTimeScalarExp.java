package org.dflib.exp.datetime;

import org.dflib.OffsetDateTimeExp;
import org.dflib.exp.ScalarExp;

import java.time.OffsetDateTime;

/**
 * @since 2.0.0
 */
public class OffsetDateTimeScalarExp extends ScalarExp<OffsetDateTime> implements OffsetDateTimeExp {

    public OffsetDateTimeScalarExp(OffsetDateTime value) {
        super(value, OffsetDateTime.class);
    }
}
