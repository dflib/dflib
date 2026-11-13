package org.dflib.exp.num;

import org.dflib.Series;

final class NumberTypeReducer {

    private static final Series<?> SCALAR_CONTEXT = Series.ofVal(null, 1);

    private NumberTypeReducer() {
    }

    static Number reduce(Object value, NumberOps.Unary op) {
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(value);
        return op.apply(NumericExpFactory.factory(resolved.getType()), resolved).reduce(SCALAR_CONTEXT);
    }

    static Number reduce(Series<?> series, NumberOps.Unary op) {
        ResolvedNumExp<?> resolved = NumberTypeResolver.resolve(series);
        return op.apply(NumericExpFactory.factory(resolved.getType()), resolved).reduce(series);
    }

    static Number reduce(Object one, Object two, NumberOps.Binary op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        return op.apply(NumericExpFactory.factory(r1, r2), r1, r2).reduce(SCALAR_CONTEXT);
    }

    static Boolean reduce(Object one, Object two, NumberOps.BinaryCondition op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        return op.apply(NumericExpFactory.factory(r1, r2), r1, r2).reduce(SCALAR_CONTEXT);
    }

    static Boolean reduce(Object one, Object two, Object three, NumberOps.TernaryCondition op) {
        ResolvedNumExp<?> r1 = NumberTypeResolver.resolve(one);
        ResolvedNumExp<?> r2 = NumberTypeResolver.resolve(two);
        ResolvedNumExp<?> r3 = NumberTypeResolver.resolve(three);
        return op.apply(NumericExpFactory.factory(r1, r2, r3), r1, r2, r3).reduce(SCALAR_CONTEXT);
    }
}
