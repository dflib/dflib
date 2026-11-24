package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.Udf2;

import static org.dflib.ql.TypeClassifier.DATETIME;
import static org.dflib.ql.TypeClassifier.OFFSETDATETIME;
import static org.dflib.ql.TypeClassifier.TIME;
import static org.dflib.ql.fn.FunctionUtils.scalar;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code plusMinutes(e, count)} function.
 *
 * @since 2.0.0
 */
public class PlusMinutesFunction implements Udf2<Object, Number, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> count) {
        int n = scalar(count, int.class);
        return unchecked(switch ((Exp<?>) e) {
            case TimeExp t -> t.plusMinutes(n);
            case DateTimeExp dt -> dt.plusMinutes(n);
            case OffsetDateTimeExp odt -> odt.plusMinutes(n);
            default -> throw unsupportedArg("plusMinutes", e, TIME, DATETIME, OFFSETDATETIME);
        });
    }
}
