package org.dflib.exp.num;

import org.dflib.BooleanSeries;
import org.dflib.Series;

final class NumberTypeEvaluator {

    private NumberTypeEvaluator() {
    }

    @SuppressWarnings("unchecked")
    static Series<Number> eval(Series<?> series, NumberOps.Unary op) {
        NumberTypeSupport.ScanResult result = NumberTypeSupport.scanSeriesIfNeeded(series);
        int rank = result.rank();
        ResolvedNumExp<? extends Number> resolvedNumExp = NumberTypeSupport.typeResolvedExp(series, rank, result.hasNulls());
        return (Series<Number>) op.apply(
                NumberTypeSupport.factoryForRank(rank), resolvedNumExp
        ).eval(series);
    }

    @SuppressWarnings("unchecked")
    static Series<Number> eval(Series<?> one, Series<?> two, NumberOps.Binary op) {
        NumberTypeSupport.ScanResult result1 = NumberTypeSupport.scanSeriesIfNeeded(one);
        NumberTypeSupport.ScanResult result2 = NumberTypeSupport.scanSeriesIfNeeded(two);
        int rank = Math.min(result1.rank(), result2.rank());
        return (Series<Number>) op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.typeResolvedExp(one, rank, result1.hasNulls()),
                NumberTypeSupport.typeResolvedExp(two, rank, result2.hasNulls())
        ).eval(one);
    }

    static BooleanSeries eval(Series<?> one, Series<?> two, NumberOps.BinaryCondition op) {
        NumberTypeSupport.ScanResult result1 = NumberTypeSupport.scanSeriesIfNeeded(one);
        NumberTypeSupport.ScanResult result2 = NumberTypeSupport.scanSeriesIfNeeded(two);
        int rank = Math.min(result1.rank(), result2.rank());
        return op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.typeResolvedExp(one, rank, result1.hasNulls()),
                NumberTypeSupport.typeResolvedExp(two, rank, result2.hasNulls())
        ).eval(one);
    }

    static BooleanSeries eval(Series<?> one, Series<?> two, Series<?> three, NumberOps.TernaryCondition op) {
        NumberTypeSupport.ScanResult result1 = NumberTypeSupport.scanSeriesIfNeeded(one);
        NumberTypeSupport.ScanResult result2 = NumberTypeSupport.scanSeriesIfNeeded(two);
        NumberTypeSupport.ScanResult result3 = NumberTypeSupport.scanSeriesIfNeeded(three);
        int rank = Math.min(result1.rank(), Math.min(result2.rank(), result3.rank()));
        return op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.typeResolvedExp(one, rank, result1.hasNulls()),
                NumberTypeSupport.typeResolvedExp(two, rank, result2.hasNulls()),
                NumberTypeSupport.typeResolvedExp(three, rank, result3.hasNulls())
        ).eval(one);
    }
}
