package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;

import static org.dflib.ql.TypeClassifier.DATE;
import static org.dflib.ql.TypeClassifier.DATETIME;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.STRING;
import static org.dflib.ql.TypeClassifier.TIME;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code max(e)} function.
 *
 * @since 2.0.0
 */
public class Max1Function implements Udf1<Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e) {
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.max();
            case StrExp s -> s.max();
            case DateExp d -> d.max();
            case TimeExp t -> t.max();
            case DateTimeExp dt -> dt.max();
            default -> throw unsupportedArg("max", e, NUMERIC, STRING, DATE, TIME, DATETIME);
        });
    }
}
