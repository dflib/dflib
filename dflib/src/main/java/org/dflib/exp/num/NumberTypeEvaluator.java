package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Series;

final class NumberTypeEvaluator {

    private NumberTypeEvaluator() {
    }

    @SuppressWarnings("unchecked")
    static Series<Number> eval(Series<?> series, NumberOps.Unary op) {
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(series);
        return (Series<Number>) op.apply(NumericExpFactory.factory(resolved.getType()), resolved).eval(series);
    }

    @SuppressWarnings("unchecked")
    static Series<Number> eval(Series<?> one, Series<?> two, NumberOps.Binary op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        return (Series<Number>) op.apply(NumericExpFactory.factory(r1, r2), r1, r2).eval(one);
    }

    static BooleanSeries eval(Series<?> one, Series<?> two, NumberOps.BinaryCondition op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        return op.apply(NumericExpFactory.factory(r1, r2), r1, r2).eval(one);
    }

    static BooleanSeries eval(Series<?> one, Series<?> two, Series<?> three, NumberOps.TernaryCondition op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        ResolvedNumExp<?> r3 = NumberTypeResolver.resolve(three);
        return op.apply(NumericExpFactory.factory(r1, r2, r3), r1, r2, r3).eval(one);
    }
}
