package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;

import static org.dflib.ql.TypeClassifier.DATE;
import static org.dflib.ql.TypeClassifier.DATETIME;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.TIME;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code median(e)} function.
 *
 * @since 2.0.0
 */
public class Median1Function implements Udf1<Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e) {
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.median();
            case DateExp d -> d.median();
            case TimeExp t -> t.median();
            case DateTimeExp dt -> dt.median();
            default -> throw unsupportedArg("median", e, NUMERIC, DATE, TIME, DATETIME);
        });
    }
}
