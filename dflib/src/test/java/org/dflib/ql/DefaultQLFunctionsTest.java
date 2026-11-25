package org.dflib.ql;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.List;
import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class DefaultQLFunctionsTest {

    @Test
    public void allDefaults() {

        List<String> names = QLContext.builder().build().functions.descriptors()
                .map(QLFunctionDescriptor::name).distinct().sorted().toList();

        assertEquals(
                List.of(
                        "abs", "avg", "castAsBigint", "castAsBool", "castAsDate", "castAsDateTime", "castAsDecimal",
                        "castAsDouble", "castAsFloat", "castAsInt", "castAsLong", "castAsNumber", "castAsOffsetDateTime",
                        "castAsStr", "castAsTime", "concat", "contains", "count", "cumSum", "day", "endsWith",
                        "first", "hour", "if", "ifNull", "last", "len", "list", "lower", "matches", "max", "median",
                        "millisecond", "min", "minute", "month", "plusDays", "plusHours", "plusMilliseconds",
                        "plusMinutes", "plusMonths", "plusNanos", "plusSeconds", "plusWeeks", "plusYears",
                        "quantile", "round", "rowNum", "scale", "second", "set", "shift", "split", "sqrt",
                        "startsWith", "substr", "sum", "trim", "upper", "vConcat", "year"),
                names);
    }

    @ParameterizedTest
    @MethodSource
    public void function(String text, Exp<?> expected) {
        assertEquals(expected, parseExp(text));
    }

    static Stream<Arguments> function() {
        return Stream.of(

                // a function result as a typed argument
                arguments("hour(castAsDateTime(a))", $col("a").castAsDateTime().hour()),
                arguments("plusDays(castAsDateTime(a), 1)", $col("a").castAsDateTime().plusDays(1)),
                arguments("min(castAsDate(a))", $col("a").castAsDate().min()),
                arguments("avg(castAsTime(a))", $col("a").castAsTime().avg()),
                arguments("plusDays(plusMonths(date(a), 1), 2)", $date("a").plusMonths(1).plusDays(2)),
                arguments("sum(abs(int(a)))", $int("a").abs().sum()),
                arguments("scale(abs(int(a)), 2)", $int("a").abs().castAsDecimal().scale(2)),
                arguments("len(castAsStr(int(a)))", $int("a").castAsStr().len()),
                arguments("abs(int(a)) > count()", $int("a").abs().gt(count())),
                arguments("shift(abs(int(a)), 1)", $int("a").abs().shift(1)),

                // an untyped argument is cast to the parameter type
                arguments("abs(a)", $col("a").castAsNumber().abs()),
                arguments("round(first(a))", $col("a").first().castAsNumber().round()),
                arguments("count(first(date(a)))", count($date("a").first().castAsBool())),
                arguments("sum(a)", $col("a").castAsNumber().sum()),
                arguments("sum(null)", $val(null).castAsNumber().sum()),
                arguments("count(a)", count($col("a").castAsBool())),
                arguments("if(a, 1, 2)", ifExp($col("a").castAsBool(), $intVal(1), $intVal(2))),
                arguments("min(str(s), a)", $str("s").min($col("a").castAsBool())),
                arguments("vConcat(a, b, ',', '[', ']')", $col("a").vConcat($col("b").castAsBool(), ",", "[", "]")),

                arguments("first(a, bool(b))", $col("a").first($bool("b"))),
                arguments("first(a, int(b) > 0)", $col("a").first($int("b").gt(0))),
                arguments("last(a)", $col("a").last()),
                arguments("vConcat(a, ',')", $col("a").vConcat(null, ",", "", "")),
                arguments("vConcat(a, bool(b), ',')", $col("a").vConcat($bool("b"), ",", "", "")),
                arguments("vConcat(a, ',', '[', ']')", $col("a").vConcat(null, ",", "[", "]")),
                arguments("vConcat(a, bool(b), ',', '[', ']')", $col("a").vConcat($bool("b"), ",", "[", "]")),
                arguments("quantile(int(a), 0.1)", $int("a").quantile(0.1)),
                arguments("quantile(int(a), 1)", $int("a").quantile(1.0))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "year(a)",
            "year(null)",
            "plusDays(a, 1)",
            "max(a)",
            "min(a) = min(b)",
            "min(offsetDateTime(a))",
            "sum(str(a))",
            "quantile(str(a), 0.5)",
            "year(time(a))",
            "hour(date(a))",
            "plusHours(date(a), 1)",
            "rowNum(int(a))",
            "FIRST(a)",
            "first()",
            "first(a, )",
            "last()",
            "last(a, int(b) > 0)",
    })
    public void function_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }

    @Test
    public void count_Eval() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, true,
                null, false,
                3, true);

        assertEquals(2, parseExp("count(a)").reduce(df));
        assertEquals(2, parseExp("count(bool(b))").reduce(df));
        assertEquals(3, parseExp("count()").reduce(df));
    }

    // only names whose "toQL" is a call, over receivers that round-trip themselves
    @ParameterizedTest
    @ValueSource(strings = {
            "castAsInt(a)",
            "castAsLong(a)",
            "castAsDecimal(a)",
            "castAsDate(a)",
            "min(castAsInt(a))",
            "year(castAsDate('2020-01-01'))",
            "millisecond(castAsTime('12:00:00.123'))",
            "concat(a, b)",
            "shift(a, 2)",
            "ifNull(a, b)",
            "if(a, 1, 2)",
            "first(a)",
            "last(a)",
            "vConcat(a, ',')",
    })
    public void toQL_RoundTrip(String text) {
        Exp<?> exp = parseExp(text);
        assertEquals(exp, parseExp(exp.toQL()));
    }
}
