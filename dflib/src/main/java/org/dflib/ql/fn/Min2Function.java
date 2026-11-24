package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.Udf2;

import static org.dflib.ql.TypeClassifier.DATE;
import static org.dflib.ql.TypeClassifier.DATETIME;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.STRING;
import static org.dflib.ql.TypeClassifier.TIME;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code min(e, filter)} function.
 *
 * @since 2.0.0
 */
public class Min2Function implements Udf2<Object, Boolean, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Boolean> filter) {
        Condition f = filter.castAsBool();
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.min(f);
            case StrExp s -> s.min(f);
            case DateExp d -> d.min(f);
            case TimeExp t -> t.min(f);
            case DateTimeExp dt -> dt.min(f);
            default -> throw unsupportedArg("min", e, NUMERIC, STRING, DATE, TIME, DATETIME);
        });
    }
}
