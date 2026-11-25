package org.dflib.ql.fn;

import org.dflib.Condition;
import org.dflib.Exp;
import org.dflib.UdfN;
import org.dflib.ql.TypeClassifier;

import static org.dflib.ql.fn.FunctionUtils.scalar;

/**
 * QL {@code vConcat(e, delimiter, prefix, suffix)} and {@code vConcat(e, filter, delimiter, prefix, suffix)} functions.
 *
 * @since 2.0.0
 */
public class VConcatNFunction implements UdfN<String> {

    @Override
    public Exp<String> call(Exp<?>... exps) {
        return switch (exps.length) {
            case 4 -> exps[0].vConcat(null, str(exps[1]), str(exps[2]), str(exps[3]));
            case 5 -> exps[0].vConcat(filter(exps[1]), str(exps[2]), str(exps[3]), str(exps[4]));
            default -> throw new IllegalArgumentException(
                    "vConcat() takes (exp, [filter,] delimiter[, prefix, suffix]) arguments");
        };
    }

    private static Condition filter(Exp<?> e) {
        return switch (TypeClassifier.classify(e)) {
            case BOOLEAN -> (Condition) e;
            case OBJECT -> e.castAsBool();
            default -> throw new IllegalArgumentException("vConcat() filter is not a condition: " + e.toQL());
        };
    }

    private static String str(Exp<?> e) {
        return scalar(e, String.class);
    }
}
