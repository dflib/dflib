package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code hour(e)} function.
 *
 * @since 2.0.0
 */
public class HourFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case TimeExp t -> t.hour();
            case DateTimeExp dt -> dt.hour();
            case OffsetDateTimeExp odt -> odt.hour();
            default -> throw unsupportedArg("hour", e,
                    TypeClassifier.TIME, TypeClassifier.DATETIME, TypeClassifier.OFFSETDATETIME);
        };
    }
}
