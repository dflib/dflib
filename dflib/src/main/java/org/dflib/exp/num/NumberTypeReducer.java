package org.dflib.exp.num;

import org.dflib.Exp;
import org.dflib.Series;

import static org.dflib.exp.num.NumericExpFactory.RANK_BIG_DECIMAL;

final class NumberTypeReducer {

    private NumberTypeReducer() {
    }

    static Number reduce(Object rawValue, NumberOps.Unary op) {
        Number value = NumberTypeSupport.castToNumber(rawValue);
        int rank = NumberTypeSupport.valueRank(value);
        if (rank == NumberTypeSupport.NO_RANK) {
            rank = RANK_BIG_DECIMAL;
        }

        return op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.scalarExp(value, rank)
        ).reduce(Series.ofVal(null, 1));
    }

    static Number reduce(Series<?> series, NumberOps.Unary op) {
        NumberTypeSupport.ScanResult result = NumberTypeSupport.scanSeriesIfNeeded(series);
        int rank = result.rank();

        return op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.typeResolvedExp(series, rank, result.hasNulls())
        ).reduce(series);
    }

    static Number reduce(Object rawOne, Object rawTwo, NumberOps.Binary op) {
        RankedScalars result = resolveScalars(rawOne, rawTwo);

        return op.apply(
                NumberTypeSupport.factoryForRank(result.rank()),
                NumberTypeSupport.scalarExp(result.one(), result.rank()),
                NumberTypeSupport.scalarExp(result.two(), result.rank())
        ).reduce(Series.ofVal(null, 1));
    }

    static Boolean reduce(Object rawOne, Object rawTwo, NumberOps.BinaryCondition op) {
        RankedScalars result = resolveScalars(rawOne, rawTwo);

        return op.apply(
                NumberTypeSupport.factoryForRank(result.rank()),
                NumberTypeSupport.scalarExp(result.one(), result.rank()),
                NumberTypeSupport.scalarExp(result.two(), result.rank())
        ).reduce(Series.ofVal(null, 1));
    }

    static Boolean reduce(Object rawOne, Object rawTwo, Object rawThree, NumberOps.TernaryCondition op) {
        Number one = NumberTypeSupport.castToNumber(rawOne);
        Number two = NumberTypeSupport.castToNumber(rawTwo);
        Number three = NumberTypeSupport.castToNumber(rawThree);

        int r1 = NumberTypeSupport.valueRank(one);
        int r2 = NumberTypeSupport.valueRank(two);
        int r3 = NumberTypeSupport.valueRank(three);

        int rank = Math.min(r1, Math.min(r2, r3));
        if (rank == NumberTypeSupport.NO_RANK) {
            rank = RANK_BIG_DECIMAL;
        }

        return op.apply(
                NumberTypeSupport.factoryForRank(rank),
                NumberTypeSupport.scalarExp(one, rank),
                NumberTypeSupport.scalarExp(two, rank),
                NumberTypeSupport.scalarExp(three, rank)
        ).reduce(Series.ofVal(null, 1));
    }

    private static RankedScalars resolveScalars(Object rawOne, Object rawTwo) {
        Number one = NumberTypeSupport.castToNumber(rawOne);
        Number two = NumberTypeSupport.castToNumber(rawTwo);
        int r1 = NumberTypeSupport.valueRank(one);
        int r2 = NumberTypeSupport.valueRank(two);
        int rank = Math.min(r1, r2);
        if (rank == NumberTypeSupport.NO_RANK) {
            rank = RANK_BIG_DECIMAL;
        }

        return new RankedScalars(one, two, rank);
    }

    private record RankedScalars(Number one, Number two, int rank) {
    }
}
