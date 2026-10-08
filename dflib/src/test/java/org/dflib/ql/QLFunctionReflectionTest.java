package org.dflib.ql;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DecimalExp;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.UdfN;
import org.dflib.ql.fn.OtherPackageFunction;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$decimal;
import static org.dflib.Exp.$double;
import static org.dflib.Exp.$doubleVal;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$long;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QLFunctionReflectionTest extends BaseQLFunctionsTest {

    private static QLFunctions registry(String name, QLFunction function) {
        return QLFunctions.builder().noDefaultFunctions().function(name, function).build();
    }

    private static Exp<?> call(QLFunctions functions, String name, Exp<?>... args) {
        return functions.call(name, Arrays.asList(args));
    }

    private static List<String> shapes(QLFunctions functions) {
        return functions.descriptors().map(QLFunctionDescriptor::shape).toList();
    }

    @Test
    public void overloads() {

        QLFunctions functions = registry("min", new MinFunction());

        assertEquals(3, functions.descriptors().count());

        assertEquals($int("a").min(), call(functions, "min", $int("a")));
        assertEquals($str("a").min(), call(functions, "min", $str("a")));
        assertEquals($date("a").min(), call(functions, "min", $date("a")));
    }

    @Test
    public void intParam() {

        QLFunctions functions = registry("plusDays", new PlusDaysFunction());

        assertEquals(List.of("plusDays(DATE, const NUMERIC)"), shapes(functions));
        assertEquals($date("a").plusDays(3), call(functions, "plusDays", $date("a"), $intVal(3)));
        assertEquals($date("a").plusDays(3), call(functions, "plusDays", $date("a"), Exp.$longVal(3L)));
    }

    @Test
    public void intParam_Fraction_throws() {

        QLFunctions functions = registry("plusDays", new PlusDaysFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> call(functions, "plusDays", $date("a"), $doubleVal(1.5)));
        assertEquals("Not an integer constant: 1.5", e.getMessage());
    }

    @Test
    public void constParam_NonConst_throws() {

        QLFunctions functions = registry("plusDays", new PlusDaysFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> call(functions, "plusDays", $date("a"), $int("b")));
        assertEquals("No overload of plusDays matches plusDays(DATE, NUMERIC<Integer>). Available: plusDays(DATE, const NUMERIC)",
                e.getMessage());
    }

    @Test
    public void doubleParam() {

        QLFunctions functions = registry("quantile", new QuantileFunction());

        assertEquals($int("a").quantile(0.5), call(functions, "quantile", $int("a"), $doubleVal(0.5)));
        assertEquals($int("a").quantile(1.0), call(functions, "quantile", $int("a"), $intVal(1)));
    }

    @Test
    public void stringParam() {

        QLFunctions functions = registry("castAsDate", new CastAsDateFunction());

        assertEquals($col("a").castAsDate("yyyy-MM-dd"),
                call(functions, "castAsDate", $col("a"), $strVal("yyyy-MM-dd")));
    }

    @Test
    public void objectParam() {

        QLFunctions functions = registry("shift", new ObjectFillerFunction());

        assertEquals(List.of("shift(OBJECT, const NUMERIC, const OBJECT)"), shapes(functions));

        assertEquals($col("a").shift(1, "x"), call(functions, "shift", $col("a"), $intVal(1), $strVal("x")));
        assertEquals($col("a").shift(1, 5), call(functions, "shift", $col("a"), $intVal(1), $intVal(5)));
        assertEquals($col("a").shift(1, null), call(functions, "shift", $col("a"), $intVal(1), Exp.$val(null)));

        assertThrows(IllegalArgumentException.class,
                () -> call(functions, "shift", $col("a"), $intVal(1), $col("b")));
    }

    @Test
    public void numberTypeVariable() {

        QLFunctions functions = registry("shift", new ShiftFunction());

        assertEquals(List.of("shift(NUMERIC, const NUMERIC, const NUMERIC)"), shapes(functions));
        assertEquals($int("a").shift(2, 0), call(functions, "shift", $int("a"), $intVal(2), $intVal(0)));
    }

    @Test
    public void expOfTypeVariable() {

        QLFunctions functions = registry("first", new FirstFunction());

        assertEquals(List.of("first(OBJECT)"), shapes(functions));
        assertEquals($col("a").first(), call(functions, "first", $col("a")));
        assertEquals($int("a").first(), call(functions, "first", $int("a")));
    }

    @Test
    public void varArgs() {

        QLFunctions functions = registry("concat", new ConcatFunction());

        assertEquals(List.of("concat()", "concat(...)"), shapes(functions));

        assertEquals(Exp.concat(), call(functions, "concat"));
        assertEquals(Exp.concat($str("a")), call(functions, "concat", $str("a")));
        assertEquals(Exp.concat($str("a"), $col("b"), $intVal(1)),
                call(functions, "concat", $str("a"), $col("b"), $intVal(1)));
    }

    @Test
    public void varArgs_TypedComponent_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", new TypedVarArgsFunction()));
        assertTrue(e.getMessage().contains("must be an Exp<?> array"), e.getMessage());
    }

    @Test
    public void typedParam_UntypedArg() {

        QLFunctions functions = registry("negation", new NotFunction());

        assertEquals(Exp.not($col("a").castAsBool()), call(functions, "negation", $col("a")));
        assertEquals(Exp.not($bool("a")), call(functions, "negation", $bool("a")));

        assertThrows(IllegalArgumentException.class, () -> call(functions, "negation", $str("a")));
    }

    @Test
    public void narrowExpParam_Widening() {

        // "DecimalExp" binds the BigDecimal value type, so a narrower numeric arg is cast to it
        QLFunctions functions = registry("dec", new DecimalFunction());

        assertEquals($decimal("a").abs(), call(functions, "dec", $decimal("a")));
        assertEquals($int("a").castAsDecimal().abs(), call(functions, "dec", $int("a")));
        assertEquals($double("a").castAsDecimal().abs(), call(functions, "dec", $double("a")));
        assertEquals($col("a").castAsDecimal().abs(), call(functions, "dec", $col("a")));
    }

    @Test
    public void narrowExpParam_UnknownValueType_throws() {

        // the value type of "castAsNumber" is only known at eval time, so the resolver lets it through, and the
        // producer has to check
        QLFunctions functions = registry("dec", new DecimalFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> call(functions, "dec", $col("a").castAsNumber()));
        assertTrue(e.getMessage().startsWith("dec() expects argument 1 to be a DecimalExp, got: "), e.getMessage());
    }

    @Test
    public void numericParam_ValueTypeMismatch_throws() {

        QLFunctions functions = registry("f", new IntExpFunction());

        assertEquals($int("a"), call(functions, "f", $int("a")));
        assertEquals($col("a").castAsInt(), call(functions, "f", $col("a")));

        // a wider value type is not narrowed implicitly
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> call(functions, "f", $double("a")));
        assertEquals("No overload of f matches f(NUMERIC<Double>). Available: f(NUMERIC<Integer>)", e.getMessage());
    }

    @Test
    public void numericParam_ExactValueType() {

        QLFunctions functions = registry("f", new AnyOrDecimalFunction());

        assertEquals($decimal("a"), call(functions, "f", $decimal("a")));
        assertEquals($int("a").abs(), call(functions, "f", $int("a")));
    }

    @Test
    public void numericParam_AmbiguousWidening() {

        QLFunctions functions = registry("f", new LongOrDoubleFunction());

        assertEquals($long("a"), call(functions, "f", $long("a")));
        assertEquals($double("a"), call(functions, "f", $double("a")));

        assertThrows(IllegalArgumentException.class,
                () -> call(functions, "f", $int("a")));
    }

    @Test
    public void nonExpReturn_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", new NonExpReturnFunction()));
        assertTrue(e.getMessage().contains("must return an Exp"), e.getMessage());
    }

    @Test
    public void unsupportedConstantType_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", new UnsupportedConstantFunction()));
        assertTrue(e.getMessage().contains("must be declared as one of"), e.getMessage());
    }

    @Test
    public void alsoUdf_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", (QLFunction) new AlsoAUdfFunction()));
        assertTrue(e.getMessage().contains("must not also implement Udf0..UdfN"), e.getMessage());
    }

    @Test
    public void nonPublicClass_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", new NonPublicFunction()));
        assertTrue(e.getMessage().contains("must be public to be invoked reflectively"), e.getMessage());
    }

    @Test
    public void noCallMethod_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("bad", new NoCallFunction()));
        assertTrue(e.getMessage().contains("must declare at least one public 'call' method"), e.getMessage());
    }

    @Test
    public void sameArgShapeOverloads_throws() {

        QLFunctions.Builder builder = QLFunctions.builder().noDefaultFunctions();

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("dup", new SameShapeFunction()));
        assertEquals("Function dup(NUMERIC) already defined", e.getMessage());
    }

    @Test
    public void overloadOrder() {
        assertEquals(
                List.of("min(NUMERIC)", "min(STRING)", "min(DATE)"),
                shapes(registry("min", new MinFunction())));

        assertEquals(
                List.of("mixed()", "mixed(NUMERIC)", "mixed(STRING)", "mixed(NUMERIC, const NUMERIC)"),
                shapes(registry("mixed", new MixedArityFunction())));

        assertEquals(shapes(registry("mixed", new MixedArityFunction())),
                shapes(registry("mixed", new MixedArityFunction())));
    }

    @Test
    public void bodyException_Unwrapped() {

        Environment.setQLFunctions(QLFunctions.builder().function("boom", new BoomFunction()).build());

        QLParserException e = assertThrows(QLParserException.class, () -> parseExp("1 + boom(int(a))"));
        assertEquals("1:4 boom() does not like " + $int("a").toQL(), e.getMessage());
    }

    @Test
    public void bodyException_RuntimeException() {

        Environment.setQLFunctions(QLFunctions.builder()
                .function("state", new StateFunction())
                .function("npe", new NpeFunction())
                .function("regex", new RegexFunction())
                .build());

        assertEquals("1:4 not ready",
                assertThrows(QLParserException.class, () -> parseExp("1 + state(int(a))")).getMessage());

        // no message: the exception class stands in for it
        assertEquals("2:0 NullPointerException",
                assertThrows(QLParserException.class, () -> parseExp("1 +\nnpe(int(a))")).getMessage());

        // a library exception from the body
        assertTrue(assertThrows(QLParserException.class, () -> parseExp("regex(str(a), '[')")).getMessage()
                .startsWith("1:0 Unclosed character class"));
    }

    @Test
    public void bodyException_Checked() {

        Environment.setQLFunctions(QLFunctions.builder().function("io", new CheckedFunction()).build());

        assertEquals("1:4 Error calling io(): disk is full",
                assertThrows(QLParserException.class, () -> parseExp("1 + io(int(a))")).getMessage());
    }

    @Test
    public void publicClass_OtherPackage() {

        QLFunctions functions = registry("other", new OtherPackageFunction());

        assertEquals(3, functions.descriptors().count());

        assertEquals($col("a").castAsStr().len(), call(functions, "other", $col("a")));
        assertEquals($int("a").shift(2), call(functions, "other", $int("a"), $intVal(2)));
        assertEquals(Exp.concat($str("a"), $str("b"), $str("c")),
                call(functions, "other", $str("a"), $str("b"), $str("c")));
    }

    // Udf objects

    @Test
    public void udf_GenericSignature() {

        QLFunctions functions = QLFunctions.builder().noDefaultFunctions()
                .function("f0", new CountUdf())
                .function("f1", new IntUdf())
                .function("f2", new IntStrUdf())
                .function("fN", new VarArgsUdf())
                .build();

        assertEquals(List.of("f0()", "f1(NUMERIC<Integer>)", "f2(NUMERIC<Integer>, STRING)", "fN(...)"), shapes(functions));

        assertEquals(Exp.count(), call(functions, "f0"));
        assertEquals($int("a").castAsInt(), call(functions, "f1", $int("a")));
        assertEquals(Exp.concat($int("a"), $str("b")), call(functions, "f2", $int("a"), $str("b")));
        assertEquals($int("a").add($int("b")), call(functions, "fN", $int("a"), $int("b")));

        assertEquals($col("a").castAsInt().castAsInt(), call(functions, "f1", $col("a")));
        assertThrows(IllegalArgumentException.class, () -> call(functions, "f1", $str("a")));
    }

    @Test
    public void udf_CovariantReturn() {

        // the bridge "call" emitted for a covariant return carries no generic types and must be skipped
        QLFunctions functions = QLFunctions.builder().noDefaultFunctions().function("scale", new CovariantUdf()).build();

        assertEquals(List.of("scale(NUMERIC, NUMERIC<Integer>)"), shapes(functions));
    }

    @Test
    public void udf_Lambda() {

        // a lambda's "call" is erased to "Exp call(Exp)", so it accepts arguments of any type
        QLFunctions functions = QLFunctions.builder().noDefaultFunctions()
                .function("f0", Udf0.of(Exp::count))
                .function("f1", Udf1.of(e -> e.castAsStr().trim()))
                .function("f2", Udf2.of((a, b) -> Exp.concat(a, b)))
                .function("fN", UdfN.of(Exp::concat))
                .build();

        assertEquals(List.of("f0()", "f1(OBJECT)", "f2(OBJECT, OBJECT)", "fN(...)"), shapes(functions));

        assertEquals($col("a").castAsStr().trim(), call(functions, "f1", $col("a")));
        assertEquals($int("a").castAsStr().trim(), call(functions, "f1", $int("a")));
        assertEquals(Exp.concat($int("a"), $str("b")), call(functions, "f2", $int("a"), $str("b")));
        assertEquals(Exp.concat($int("a"), $str("b"), $col("c")), call(functions, "fN", $int("a"), $str("b"), $col("c")));
    }

    @Test
    public void udf_InheritedCall() {

        QLFunctions functions = QLFunctions.builder().noDefaultFunctions()
                .function("format", new InheritedConcreteUdf())
                .build();

        assertEquals(List.of("format(STRING)"), shapes(functions));
        assertEquals($str("a").castAsStr(), call(functions, "format", $str("a")));
    }

    @Test
    public void udf_InheritedGenericCall_throws() {

        QLFunctions.Builder b1 = QLFunctions.builder().noDefaultFunctions();
        IllegalArgumentException e1 = assertThrows(IllegalArgumentException.class,
                () -> b1.function("f1", new InheritedGenericUdf()));
        assertTrue(e1.getMessage().contains(InheritedGenericUdf.class.getName()), e1.getMessage());
        assertTrue(e1.getMessage().contains("type variable 'T'"), e1.getMessage());

        QLFunctions.Builder bN = QLFunctions.builder().noDefaultFunctions();
        assertThrows(IllegalArgumentException.class, () -> bN.function("fN", new InheritedGenericVarArgsUdf()));
    }

    @Test
    public void qlFunction_InheritedCall() {

        QLFunctions functions = registry("upper", new InheritedQLFunction());

        assertEquals(List.of("upper(STRING)", "upper(STRING, NUMERIC)"), shapes(functions));
        assertEquals($str("a").upper(), call(functions, "upper", $str("a")));
        assertEquals($str("a").lower(), call(functions, "upper", $str("a"), $int("b")));
    }

    @Test
    public void qlFunction_InheritedGenericCall_throws() {
        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> registry("id", new InheritedGenericQLFunction()));
        assertTrue(e.getMessage().contains("type variable 'T'"), e.getMessage());
    }

    public static class MinFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            return e.min();
        }

        public StrExp call(StrExp e) {
            return e.min();
        }

        public DateExp call(DateExp e) {
            return e.min();
        }
    }

    public static class MixedArityFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e, int n) {
            return e.shift(n);
        }

        public StrExp call(StrExp e) {
            return e.trim();
        }

        public NumExp<?> call() {
            return Exp.count();
        }

        public NumExp<?> call(NumExp<?> e) {
            return e.abs();
        }
    }

    public static class PlusDaysFunction implements QLFunction {

        public DateExp call(DateExp e, int days) {
            return e.plusDays(days);
        }
    }

    public static class QuantileFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e, double q) {
            return e.quantile(q);
        }
    }

    public static class ObjectFillerFunction implements QLFunction {

        @SuppressWarnings("unchecked")
        public <T> Exp<T> call(Exp<T> e, int offset, Object filler) {
            return e.shift(offset, (T) filler);
        }
    }

    public static class CastAsDateFunction implements QLFunction {

        public DateExp call(Exp<?> e, String format) {
            return e.castAsDate(format);
        }
    }

    public static class ShiftFunction implements QLFunction {

        public <N extends Number> NumExp<N> call(NumExp<N> e, int offset, N filler) {
            return e.shift(offset, filler);
        }
    }

    public static class FirstFunction implements QLFunction {

        public <T> Exp<T> call(Exp<T> e) {
            return e.first();
        }
    }

    public static class ConcatFunction implements QLFunction {

        public StrExp call() {
            return Exp.concat();
        }

        public StrExp call(Exp<?>... exps) {
            return Exp.concat((Object[]) exps);
        }
    }

    public static class TypedVarArgsFunction implements QLFunction {

        public StrExp call(StrExp... exps) {
            return Exp.concat((Object[]) exps);
        }
    }

    public static class NotFunction implements QLFunction {

        public Condition call(Condition c) {
            return c.not();
        }
    }

    public static class DecimalFunction implements QLFunction {

        public NumExp<?> call(DecimalExp e) {
            return e.abs();
        }
    }

    public static class BoomFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            throw new IllegalArgumentException("boom() does not like " + e.toQL());
        }
    }

    public static class StateFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            throw new IllegalStateException("not ready");
        }
    }

    public static class NpeFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            throw new NullPointerException();
        }
    }

    public static class RegexFunction implements QLFunction {

        public Condition call(StrExp e, String regex) {
            return e.matches(regex);
        }
    }

    public static class CheckedFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) throws java.io.IOException {
            throw new java.io.IOException("disk is full");
        }
    }

    public static class NonExpReturnFunction implements QLFunction {

        public String call(Exp<?> e) {
            return e.toQL();
        }
    }

    public static class UnsupportedConstantFunction implements QLFunction {

        public StrExp call(StrExp e, StringBuilder unsupported) {
            return e.trim();
        }
    }

    public static class AlsoAUdfFunction implements QLFunction, Udf1<Object, Object> {

        @Override
        public Exp<Object> call(Exp<Object> exp) {
            return exp;
        }
    }

    static class NonPublicFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            return e.abs();
        }
    }

    public static class NoCallFunction implements QLFunction {

        public NumExp<?> notCall(NumExp<?> e) {
            return e.abs();
        }
    }

    public static class SameShapeFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            return e.abs();
        }

        public Exp<Number> call(Exp<Number> e) {
            return e;
        }
    }

    public static class IntExpFunction implements QLFunction {

        public Exp<Integer> call(Exp<Integer> e) {
            return e;
        }
    }

    public static class AnyOrDecimalFunction implements QLFunction {

        public NumExp<?> call(NumExp<?> e) {
            return e.abs();
        }

        public DecimalExp call(DecimalExp e) {
            return e;
        }
    }

    public static class LongOrDoubleFunction implements QLFunction {

        public NumExp<Long> call(NumExp<Long> e) {
            return e;
        }

        // a different erasure than the NumExp<Long> one, but the same kind of a parameter for the resolver
        public Exp<Double> call(Exp<Double> e) {
            return e;
        }
    }

    public static class CountUdf implements Udf0<Integer> {
        @Override
        public Exp<Integer> call() {
            return Exp.count();
        }
    }

    public static class IntUdf implements Udf1<Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> exp) {
            return exp.castAsInt();
        }
    }

    public static class IntStrUdf implements Udf2<Integer, String, String> {
        @Override
        public Exp<String> call(Exp<Integer> a, Exp<String> b) {
            return Exp.concat(a, b);
        }
    }

    public static class VarArgsUdf implements UdfN<Number> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public NumExp<Number> call(Exp<?>... exps) {
            NumExp<Number> result = (NumExp) exps[0];
            for (int i = 1; i < exps.length; i++) {
                result = result.add((NumExp) exps[i]);
            }
            return result;
        }
    }

    public static class CovariantUdf implements Udf2<Number, Integer, Number> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public NumExp<Number> call(Exp<Number> exp, Exp<Integer> scale) {
            return (NumExp) exp.castAsDecimal();
        }
    }

    public static abstract class ConcreteBaseUdf implements Udf1<String, String> {
        @Override
        public Exp<String> call(Exp<String> exp) {
            return exp.castAsStr();
        }
    }

    public static class InheritedConcreteUdf extends ConcreteBaseUdf {
    }

    public static abstract class GenericBaseUdf<T, R> implements Udf1<T, R> {
        @SuppressWarnings("unchecked")
        @Override
        public Exp<R> call(Exp<T> exp) {
            return (Exp<R>) exp;
        }
    }

    public static class InheritedGenericUdf extends GenericBaseUdf<String, String> {
    }

    public static abstract class GenericBaseVarArgsUdf<R> implements UdfN<R> {
        @SuppressWarnings("unchecked")
        @Override
        public Exp<R> call(Exp<?>... exps) {
            return (Exp<R>) exps[0];
        }
    }

    public static class InheritedGenericVarArgsUdf extends GenericBaseVarArgsUdf<Number> {
    }

    public static class BaseQLFunction implements QLFunction {
        public StrExp call(StrExp e) {
            return e.lower();
        }

        public StrExp call(StrExp e, NumExp<?> n) {
            return e.lower();
        }
    }

    public static class InheritedQLFunction extends BaseQLFunction {
        @Override
        public StrExp call(StrExp e) {
            return e.upper();
        }
    }

    public static class GenericBaseQLFunction<T> implements QLFunction {
        public Exp<T> call(Exp<T> e) {
            return e;
        }
    }

    public static class InheritedGenericQLFunction extends GenericBaseQLFunction<String> {
    }
}
