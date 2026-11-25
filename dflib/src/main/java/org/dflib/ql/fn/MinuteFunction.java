package org.dflib.ql.fn;

import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code minute(e)} function.
 *
 * @since 2.0.0
 */
public class MinuteFunction implements Udf1<Object, Integer> {

    @Override
    public Exp<Integer> call(Exp<Object> e) {
        return switch ((Exp<?>) e) {
            case TimeExp t -> t.minute();
            case DateTimeExp dt -> dt.minute();
            case OffsetDateTimeExp odt -> odt.minute();
            default -> throw unsupportedArg("minute", e,
                    TypeClassifier.TIME, TypeClassifier.DATETIME, TypeClassifier.OFFSETDATETIME);
        };
    }
}
