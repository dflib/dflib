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
 * QL {@code month(e)} function.
 *
 * @since 2.0.0
 */
public class MonthFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case DateExp d -> d.month();
            case DateTimeExp dt -> dt.month();
            case OffsetDateTimeExp odt -> odt.month();
            default -> throw unsupportedArg("month", e, DATE, DATETIME, OFFSETDATETIME);
        };
    }
}
