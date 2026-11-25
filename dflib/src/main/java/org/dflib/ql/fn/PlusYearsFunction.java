package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Udf2;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.scalar;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code plusYears(e, count)} function.
 *
 * @since 2.0.0
 */
public class PlusYearsFunction implements Udf2<Object, Number, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> count) {
        int n = scalar(count, int.class);
        return unchecked(switch ((Exp<?>) e) {
            case DateExp d -> d.plusYears(n);
            case DateTimeExp dt -> dt.plusYears(n);
            case OffsetDateTimeExp odt -> odt.plusYears(n);
            default -> throw unsupportedArg("plusYears", e,
                    TypeClassifier.DATE, TypeClassifier.DATETIME, TypeClassifier.OFFSETDATETIME);
        });
    }
}
