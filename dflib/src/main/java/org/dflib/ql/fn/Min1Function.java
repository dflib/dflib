package org.dflib.ql.fn;

import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.Udf1;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code min(e)} function.
 *
 * @since 2.0.0
 */
public class Min1Function implements Udf1<Object, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e) {
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.min();
            case StrExp s -> s.min();
            case DateExp d -> d.min();
            case TimeExp t -> t.min();
            case DateTimeExp dt -> dt.min();
            default -> throw unsupportedArg("min", e,
                    TypeClassifier.NUMERIC, TypeClassifier.STRING, TypeClassifier.DATE, TypeClassifier.TIME,
                    TypeClassifier.DATETIME);
        });
    }
}
