package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.Udf1;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code year(e)} function.
 *
 * @since 2.0.0
 */
public class YearFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case DateExp d -> d.year();
            case DateTimeExp dt -> dt.year();
            case OffsetDateTimeExp odt -> odt.year();
            default -> throw unsupportedArg("year", e,
                    TypeClassifier.DATE, TypeClassifier.DATETIME, TypeClassifier.OFFSETDATETIME);
        };
    }
}
