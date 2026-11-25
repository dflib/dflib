package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code second(e)} function.
 *
 * @since 2.0.0
 */
public class SecondFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case TimeExp t -> t.second();
            case DateTimeExp dt -> dt.second();
            case OffsetDateTimeExp odt -> odt.second();
            default -> throw unsupportedArg("second", e,
                    TypeClassifier.TIME, TypeClassifier.DATETIME, TypeClassifier.OFFSETDATETIME);
        };
    }
}
