package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.Udf2;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code max(e, filter)} function.
 *
 * @since 2.0.0
 */
public class Max2Function implements Udf2<Object, Boolean, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Boolean> filter) {
        Condition f = filter.castAsBool();
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.max(f);
            case StrExp s -> s.max(f);
            case DateExp d -> d.max(f);
            case TimeExp t -> t.max(f);
            case DateTimeExp dt -> dt.max(f);
            default -> throw unsupportedArg("max", e,
                    TypeClassifier.NUMERIC, TypeClassifier.STRING, TypeClassifier.DATE, TypeClassifier.TIME,
                    TypeClassifier.DATETIME);
        });
    }
}
