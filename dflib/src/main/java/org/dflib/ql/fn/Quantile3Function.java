package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.TimeExp;
import org.dflib.Udf3;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.scalar;
import static org.dflib.ql.fn.FunctionUtils.unchecked;
import static org.dflib.ql.fn.FunctionUtils.unsupportedArg;

/**
 * QL {@code quantile(e, q, filter)} function.
 *
 * @since 2.0.0
 */
public class Quantile3Function implements Udf3<Object, Number, Boolean, Object> {

    @Override
    public Exp<Object> call(Exp<Object> e, Exp<Number> q, Exp<Boolean> filter) {
        double p = scalar(q, double.class);
        Condition f = filter.castAsBool();
        return unchecked(switch ((Exp<?>) e) {
            case NumExp<?> n -> n.quantile(p, f);
            case DateExp d -> d.quantile(p, f);
            case TimeExp t -> t.quantile(p, f);
            case DateTimeExp dt -> dt.quantile(p, f);
            default -> throw unsupportedArg("quantile", e,
                    TypeClassifier.NUMERIC, TypeClassifier.DATE, TypeClassifier.TIME, TypeClassifier.DATETIME);
        });
    }
}
