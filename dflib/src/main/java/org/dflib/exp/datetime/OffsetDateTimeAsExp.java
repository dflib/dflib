package org.dflib.exp.datetime;

import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.exp.AsExp;

import java.time.OffsetDateTime;
import java.util.Objects;

/**
 * @since 2.0.0
 */
public class OffsetDateTimeAsExp extends AsExp<OffsetDateTime> implements OffsetDateTimeExp {

    public OffsetDateTimeAsExp(String name, Exp<OffsetDateTime> delegate) {
        super(name, delegate);
    }

    @Override
    public OffsetDateTimeExp as(String name) {
        return Objects.equals(name, this.name) ? this : OffsetDateTimeExp.super.as(name);
    }
}
