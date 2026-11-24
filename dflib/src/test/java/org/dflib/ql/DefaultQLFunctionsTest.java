package org.dflib.ql;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import java.math.BigInteger;
import java.util.List;

import static org.dflib.Exp.$bigintVal;
import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$boolVal;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$dateTime;
import static org.dflib.Exp.$decimal;
import static org.dflib.Exp.$doubleVal;
import static org.dflib.Exp.$floatVal;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$longVal;
import static org.dflib.Exp.$offsetDateTime;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.$time;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class DefaultQLFunctionsTest {

    private static final QLFunctions FUNCTIONS = QLContext.builder().build().functions;

    @Test
    public void allDefaults() {

        List<String> names = FUNCTIONS.descriptors().map(QLFunctionDescriptor::name).distinct().sorted().toList();

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

    @Test
    public void zeroArityOverloads() {
        assertEquals(Exp.count(), call("count"));
        assertEquals(Exp.count($bool("b")), call("count", $bool("b")));
        assertEquals(Exp.concat(), call("concat"));
        assertEquals(Exp.concat($str("a"), $str("b"), $str("c")), call("concat", $str("a"), $str("b"), $str("c")));
    }

    @Test
    public void vConcatArities() {
        assertEquals($str("a").vConcat(null, ",", "", ""), call("vConcat", $str("a"), $strVal(",")));
        assertEquals($str("a").vConcat($bool("b"), ",", "", ""),
                call("vConcat", $str("a"), $bool("b"), $strVal(",")));
        assertEquals($str("a").vConcat(null, ",", "[", "]"),
                call("vConcat", $str("a"), $strVal(","), $strVal("["), $strVal("]")));
        assertEquals($str("a").vConcat($bool("b"), ",", "[", "]"),
                call("vConcat", $str("a"), $bool("b"), $strVal(","), $strVal("["), $strVal("]")));
        assertEquals($col("a").vConcat($col("b").castAsBool(), ",", "[", "]"),
                call("vConcat", $col("a"), $col("b"), $strVal(","), $strVal("["), $strVal("]")));

        assertEquals("vConcat() takes (exp, [filter,] delimiter[, prefix, suffix]) arguments",
                assertThrows(IllegalArgumentException.class, () -> call("vConcat", $str("a"))).getMessage());
        assertEquals("vConcat() filter is not a condition: b",
                assertThrows(IllegalArgumentException.class, () -> call("vConcat",
                        $str("a"), $int("b"), $strVal(","), $strVal("["), $strVal("]"))).getMessage());
    }

    @Test
    public void integerConstant_OutOfRange() {
        assertEquals("Integer constant out of range [-2147483648..2147483647]: 3000000000",
                assertThrows(IllegalArgumentException.class,
                        () -> call("substr", $str("a"), $longVal(3_000_000_000L))).getMessage());

        assertEquals("Integer constant out of range [-2147483648..2147483647]: 18446744073709551616",
                assertThrows(IllegalArgumentException.class,
                        () -> call("plusDays", $date("a"), $bigintVal(BigInteger.TWO.pow(64)))).getMessage());

        // within range is fine, whatever the width of the constant
        assertEquals($str("a").substr(2), call("substr", $str("a"), $longVal(2L)));
        assertEquals($str("a").substr(2), call("substr", $str("a"), $bigintVal(BigInteger.TWO)));
    }

    @Test
    public void doubleConstant_FloatLiteral() {
        assertEquals($int("a").quantile(0.1), Exp.parseExp("quantile(int(a), 0.1)"));
        assertEquals($int("a").quantile(0.1), call("quantile", $int("a"), $floatVal(0.1f)));
    }

    @Test
    public void count_Eval() {
        DataFrame df = DataFrame.foldByRow("a", "b").of(
                1, true,
                null, false,
                3, true);

        assertEquals(2, Exp.parseExp("count(a)").reduce(df));
        assertEquals(2, Exp.parseExp("count(bool(b))").reduce(df));
        assertEquals(3, Exp.parseExp("count()").reduce(df));
    }

    @Test
    public void count_Cast() {
        assertEquals(Exp.count($bool("b")), call("count", $bool("b")));
        assertEquals(Exp.count($col("c").castAsBool()), call("count", $col("c")));
        assertEquals(Exp.count($date("d").first().castAsBool()), call("count", $date("d").first()));
    }

    @Test
    public void untypedReceiver_Cast() {
        assertEquals($col("c").castAsNumber().sum(), call("sum", $col("c")));
        assertEquals(Exp.ifExp($col("c").castAsBool(), $intVal(1), $intVal(2)),
                call("if", $col("c"), $intVal(1), $intVal(2)));
        assertEquals($str("c").min($col("f").castAsBool()), call("min", $str("c"), $col("f")));

        // an expression that carries a value type but implements no typed interface is untyped too
        assertEquals($date("d").first().castAsNumber().abs(), call("abs", $date("d").first()));

        // while a function declared over "Exp<?>" takes anything as is
        assertEquals($col("c").castAsStr().len(), call("len", $col("c")));
        assertEquals($col("c").shift(1, "x"), call("shift", $col("c"), $intVal(1), $strVal("x")));
    }

    @Test
    public void untypedReceiver_Ambiguous_throws() {
        assertEquals("year(): argument 1 is untyped, cast it explicitly to one of DATE, DATETIME, OFFSETDATETIME",
                assertThrows(IllegalArgumentException.class, () -> call("year", $col("c"))).getMessage());

        assertEquals("min(): argument 1 is untyped, cast it explicitly to one of NUMERIC, STRING, DATE, TIME, DATETIME",
                assertThrows(IllegalArgumentException.class, () -> call("min", $col("c"))).getMessage());

        assertThrows(IllegalArgumentException.class, () -> call("plusDays", $col("c"), $intVal(1)));
    }

    @Test
    public void untypedReceiver_CastNumeric() {
        assertEquals($col("c").castAsNumber().sum(), call("sum", $col("c")));
        assertEquals($col("c").castAsNumber().abs(), call("abs", $col("c")));
    }

    @Test
    public void numericAggregates() {
        assertEquals($int("i").sum(), call("sum", $int("i")));
        assertEquals($int("i").sum($bool("b")), call("sum", $int("i"), $bool("b")));
        assertEquals($int("i").cumSum(), call("cumSum", $int("i")));

        assertThrows(IllegalArgumentException.class, () -> call("sum", $str("s")));
        assertThrows(IllegalArgumentException.class, () -> call("sum", $date("d")));
    }

    @Test
    public void aggregates() {
        assertEquals($int("i").min(), call("min", $int("i")));
        assertEquals($str("s").max(), call("max", $str("s")));
        assertEquals($date("d").avg(), call("avg", $date("d")));
        assertEquals($time("t").median(), call("median", $time("t")));
        assertEquals($dateTime("dt").min($bool("b")), call("min", $dateTime("dt"), $bool("b")));
        assertEquals($decimal("m").min(), call("min", $decimal("m")));
    }

    @Test
    public void quantile() {
        assertEquals($int("i").quantile(0.5), call("quantile", $int("i"), $doubleVal(0.5)));
        assertEquals($int("i").quantile(0.5, $bool("b")), call("quantile", $int("i"), $doubleVal(0.5), $bool("b")));
        assertEquals($date("d").quantile(0.5), call("quantile", $date("d"), $doubleVal(0.5)));
        assertEquals($int("i").quantile(1.0), call("quantile", $int("i"), $intVal(1)));
    }

    @Test
    public void scale() {
        assertEquals($int("i").castAsDecimal().scale(2), call("scale", $int("i"), $intVal(2)));
    }

    @Test
    public void fieldFunctions() {
        assertEquals($date("d").year(), call("year", $date("d")));
        assertEquals($date("d").month(), call("month", $date("d")));
        assertEquals($date("d").day(), call("day", $date("d")));
        assertEquals($time("t").hour(), call("hour", $time("t")));
        assertEquals($time("t").millisecond(), call("millisecond", $time("t")));
        assertEquals($dateTime("dt").year(), call("year", $dateTime("dt")));
        assertEquals($dateTime("dt").second(), call("second", $dateTime("dt")));
        assertEquals($offsetDateTime("odt").minute(), call("minute", $offsetDateTime("odt")));
    }

    @Test
    public void temporalArithmetic() {
        assertEquals($date("d").plusDays(3), call("plusDays", $date("d"), $intVal(3)));
        assertEquals($dateTime("dt").plusNanos(3), call("plusNanos", $dateTime("dt"), $intVal(3)));
        assertEquals($offsetDateTime("odt").plusWeeks(3), call("plusWeeks", $offsetDateTime("odt"), $intVal(3)));
        assertEquals($time("t").plusMilliseconds(3), call("plusMilliseconds", $time("t"), $intVal(3)));
    }

    @Test
    public void casts() {
        assertEquals($col("c").castAsInt(), call("castAsInt", $col("c")));
        assertEquals($col("c").castAsDecimal(), call("castAsDecimal", $col("c")));
        assertEquals($col("c").castAsStr(), call("castAsStr", $col("c")));
        assertEquals($col("c").castAsDate(), call("castAsDate", $col("c")));
        assertEquals($col("c").castAsDate("yyyy"), call("castAsDate", $col("c"), $strVal("yyyy")));
        assertEquals($col("c").castAsOffsetDateTime("yyyy"),
                call("castAsOffsetDateTime", $col("c"), $strVal("yyyy")));
    }

    @Test
    public void specialForms() {
        assertEquals(Exp.ifExp($bool("b"), $int("i"), $int("j")), call("if", $bool("b"), $int("i"), $int("j")));
        assertEquals(Exp.ifNull($int("i"), $int("j")), call("ifNull", $int("i"), $int("j")));
        assertEquals($col("c").first(), call("first", $col("c")));
        assertEquals($col("c").first($bool("b")), call("first", $col("c"), $bool("b")));
        assertEquals($col("c").last(), call("last", $col("c")));
    }

    @Test
    public void shift() {
        assertEquals($int("i").shift(2), call("shift", $int("i"), $intVal(2)));
        assertEquals($int("i").shift(2, 0), call("shift", $int("i"), $intVal(2), $intVal(0)));
        assertEquals($str("s").shift(1, "x"), call("shift", $str("s"), $intVal(1), $strVal("x")));
        assertEquals($bool("b").shift(1, true), call("shift", $bool("b"), $intVal(1), $boolVal(true)));
        assertEquals($date("d").shift(1), call("shift", $date("d"), $intVal(1)));
        assertEquals($col("c").shift(1, "x"), call("shift", $col("c"), $intVal(1), $strVal("x")));
    }

    @Test
    public void shift_FillerType_throws() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> call("shift", $int("i"), $intVal(2), $strVal("replace")));
        assertTrue(e.getMessage().startsWith("shift() filler of type STRING"), e.getMessage());

        assertThrows(IllegalArgumentException.class,
                () -> call("shift", $str("s"), $intVal(1), $intVal(1)));
        assertThrows(IllegalArgumentException.class,
                () -> call("shift", $bool("b"), $intVal(1), $strVal("x")));
    }

    @Test
    public void plusDays_NonConstCount_throws() {
        assertThrows(IllegalArgumentException.class,
                () -> call("plusDays", $date("d"), $strVal("3")));

        assertEquals("Not a constant: n", assertThrows(IllegalArgumentException.class,
                () -> call("plusWeeks", $date("d"), $int("n"))).getMessage());
        assertEquals("Not a constant: 1 + 2", assertThrows(IllegalArgumentException.class,
                () -> call("plusWeeks", $date("d"), $intVal(1).add(2))).getMessage());
    }

    @Test
    public void unsupportedReceiver_throws() {
        assertEquals("avg(): argument 1 of type STRING is not one of NUMERIC, DATE, TIME, DATETIME",
                assertThrows(IllegalArgumentException.class, () -> call("avg", $str("s"))).getMessage());

        assertThrows(IllegalArgumentException.class, () -> call("min", $offsetDateTime("odt")));
        assertThrows(IllegalArgumentException.class, () -> call("year", $time("t")));
        assertThrows(IllegalArgumentException.class, () -> call("hour", $date("d")));
        assertThrows(IllegalArgumentException.class, () -> call("quantile", $str("s"), $doubleVal(0.5)));
        assertThrows(IllegalArgumentException.class, () -> call("plusDays", $time("t"), $intVal(1)));
        assertThrows(IllegalArgumentException.class, () -> call("plusHours", $date("d"), $intVal(1)));
    }

    @Test
    public void unknownFunction_throws() {
        assertEquals("Unknown function: noSuchFunction",
                assertThrows(IllegalArgumentException.class, () -> call("noSuchFunction", $int("i"))).getMessage());
        assertThrows(IllegalArgumentException.class, () -> call("rowNum", $int("i")));
        assertThrows(IllegalArgumentException.class, () -> call("year"));
        assertThrows(IllegalArgumentException.class, () -> call("last", $col("c"), $bool("b")));
    }

    private static Exp<?> call(String name, Exp<?>... args) {
        return FUNCTIONS.call(name, List.of(args));
    }
}
