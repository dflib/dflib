package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Udf1;

import static org.dflib.ql.TypeClassifier.DATE;
import static org.dflib.ql.TypeClassifier.DATETIME;
import static org.dflib.ql.TypeClassifier.OFFSETDATETIME;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code day(e)} function.
 *
 * @since 2.0.0
 */
public class DayFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case DateExp d -> d.day();
            case DateTimeExp dt -> dt.day();
            case OffsetDateTimeExp odt -> odt.day();
            default -> throw unsupportedArg("day", e, DATE, DATETIME, OFFSETDATETIME);
        };
    }
}
