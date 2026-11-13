package org.dflib.ql;

import org.dflib.ql.fn.*;

/**
 * Registrations of the built-in QL functions.
 */
class DefaultQLFunctions {

    private DefaultQLFunctions() {
    }

    static void register(QLFunctions.Builder builder) {
        builder
                .function("abs", new AbsFunction())
                .function("avg", new AvgFunction())
                .function("castAsBigint", new CastAsBigintFunction())
                .function("castAsBool", new CastAsBoolFunction())
                .function("castAsDate", new CastAsDateFunction())
                .function("castAsDateTime", new CastAsDateTimeFunction())
                .function("castAsDecimal", new CastAsDecimalFunction())
                .function("castAsDouble", new CastAsDoubleFunction())
                .function("castAsFloat", new CastAsFloatFunction())
                .function("castAsInt", new CastAsIntFunction())
                .function("castAsLong", new CastAsLongFunction())
                .function("castAsNumber", new CastAsNumberFunction())
                .function("castAsOffsetDateTime", new CastAsOffsetDateTimeFunction())
                .function("castAsStr", new CastAsStrFunction())
                .function("castAsTime", new CastAsTimeFunction())
                .function("concat", new ConcatFunction())
                .function("contains", new ContainsFunction())
                .function("count", new CountFunction())
                .function("cumSum", new CumSumFunction())
                .function("day", new DayFunction())
                .function("endsWith", new EndsWithFunction())
                .function("first", new FirstFunction())
                .function("hour", new HourFunction())
                .function("if", new IfFunction())
                .function("ifNull", new IfNullFunction())
                .function("last", new LastFunction())
                .function("len", new LenFunction())
                .function("list", new ListFunction())
                .function("lower", new LowerFunction())
                .function("matches", new MatchesFunction())
                .function("max", new MaxFunction())
                .function("median", new MedianFunction())
                .function("millisecond", new MillisecondFunction())
                .function("min", new MinFunction())
                .function("minute", new MinuteFunction())
                .function("month", new MonthFunction())
                .function("plusDays", new PlusDaysFunction())
                .function("plusHours", new PlusHoursFunction())
                .function("plusMilliseconds", new PlusMillisecondsFunction())
                .function("plusMinutes", new PlusMinutesFunction())
                .function("plusMonths", new PlusMonthsFunction())
                .function("plusNanos", new PlusNanosFunction())
                .function("plusSeconds", new PlusSecondsFunction())
                .function("plusWeeks", new PlusWeeksFunction())
                .function("plusYears", new PlusYearsFunction())
                .function("quantile", new QuantileFunction())
                .function("round", new RoundFunction())
                .function("rowNum", new RowNumFunction())
                .function("scale", new ScaleFunction())
                .function("second", new SecondFunction())
                .function("set", new SetFunction())
                .function("shift", new ShiftFunction())
                .function("split", new SplitFunction())
                .function("sqrt", new SqrtFunction())
                .function("startsWith", new StartsWithFunction())
                .function("substr", new SubstrFunction())
                .function("sum", new SumFunction())
                .function("trim", new TrimFunction())
                .function("upper", new UpperFunction())
                .function("vConcat", new VConcatFunction())
                .function("year", new YearFunction());
    }
}
