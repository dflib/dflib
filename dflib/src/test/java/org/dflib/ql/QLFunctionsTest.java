package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.fn.Substr2Function;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$str;
import static org.dflib.ql.QLFunctionDescriptor.of;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QLFunctionsTest {

    private static QLFunctions functions(QLFunctionDescriptor... descriptors) {
        return new QLFunctions(List.of(descriptors));
    }

    private static Exp<?> call(QLFunctions functions, String name, Exp<?>... args) {
        return functions.call(name, List.of(args));
    }

    private static String error(QLFunctions functions, String name, Exp<?>... args) {
        return assertThrows(IllegalArgumentException.class, () -> call(functions, name, args)).getMessage();
    }

    @Test
    public void shape() {
        assertEquals("f()", of("f", Udf0.of(Exp::count)).shape());
        assertEquals("f(STRING)", of("f", new StrArgFunction()).shape());
        assertEquals("f(BOOLEAN, NUMERIC)", of("f", new BoolNumArgFunction()).shape());
        assertEquals("f(NUMERIC, NUMERIC, NUMERIC)", of("f", new Int3SumFunction()).shape());
        assertEquals("f(...)", of("f", new IntNSumFunction()).shape());
        assertEquals("f(NUMERIC, NUMERIC)", of("f", new CovariantFunction()).shape());
        assertEquals("f(STRING)", of("f", new InheritedFunction()).shape());

        // a lambda's "call" is erased to "Exp call(Exp)"
        assertEquals("f(OBJECT)", of("f", Udf1.of(e -> e)).shape());
        assertEquals("f(OBJECT, OBJECT)", of("f", Udf2.of((a, b) -> a)).shape());

        // a type variable of the superclass is not resolved
        assertEquals("f(OBJECT)", of("f", new InheritedGenericFunction()).shape());
    }

    @Test
    public void call_ByArity() {
        QLFunctions functions = functions(
                of("sum", new Int2SumFunction()),
                of("sum", new Int3SumFunction()),
                of("sum", new IntNSumFunction()));

        assertEquals($int("a"), call(functions, "sum", $int("a")));
        assertEquals(new Int2SumFunction().call($int("a"), $int("b")),
                call(functions, "sum", $int("a"), $int("b")));
        assertEquals(new Int3SumFunction().call($int("a"), $int("b"), $int("c")),
                call(functions, "sum", $int("a"), $int("b"), $int("c")));
        assertEquals($int("a").add($int("b")).add($int("c")).add($int("d")),
                call(functions, "sum", $int("a"), $int("b"), $int("c"), $int("d")));
    }

    @Test
    public void call_throws() {
        QLFunctions functions = functions(
                of("sum", new Int2SumFunction()),
                of("sum", new Int3SumFunction()));

        assertEquals("Unknown function: multiply", error(functions, "multiply", $int("a"), $int("b")));
        assertEquals("No overload of sum matches sum(NUMERIC). Available: sum(NUMERIC, NUMERIC),"
                        + " sum(NUMERIC, NUMERIC, NUMERIC)",
                error(functions, "sum", $int("a")));
        assertEquals("No overload of sum matches sum(NUMERIC, STRING). Available: sum(NUMERIC, NUMERIC),"
                        + " sum(NUMERIC, NUMERIC, NUMERIC)",
                error(functions, "sum", $int("a"), $str("b")));
    }

    @Test
    public void call_ArgTypes() {
        QLFunctions functions = functions(
                of("s", new StrArgFunction()),
                of("i", new IntArgFunction()),
                of("bn", new BoolNumArgFunction()),
                of("do", new DateObjArgFunction()),
                of("o", Udf1.of(e -> e)));

        assertEquals($str("a"), call(functions, "s", $str("a")));
        assertEquals($col("a").castAsStr(), call(functions, "s", $col("a")));
        assertEquals($col("a").castAsNumber(), call(functions, "i", $col("a")));
        assertEquals($col("a").castAsBool(), call(functions, "bn", $col("a"), $intVal(1)));
        assertEquals($col("a").castAsBool(), call(functions, "bn", $col("a").castAsBool(), $col("b")));
        assertEquals($col("a").castAsDate(), call(functions, "do", $col("a"), $col("b")));

        // any numeric argument matches a parameter of a specific numeric type
        assertEquals($double("a"), call(functions, "i", $double("a")));

        assertEquals($col("a"), call(functions, "o", $col("a")));
        assertEquals($int("a"), call(functions, "o", $int("a")));
    }

    @Test
    public void call_ConstantParam() {
        QLFunctions functions = functions(of("substr", new Substr2Function()));

        assertEquals($str("a").substr(1), call(functions, "substr", $str("a"), $intVal(1)));
        assertEquals("Not a constant: b", error(functions, "substr", $str("a"), $int("b")));
        assertEquals("No overload of substr matches substr(STRING, STRING). Available: substr(OBJECT, NUMERIC)",
                error(functions, "substr", $str("a"), $str("b")));
    }

    @Test
    public void duplicateArity_throws() {
        assertEquals("Function f(OBJECT) already defined", assertThrows(IllegalArgumentException.class,
                () -> functions(of("f", new StrArgFunction()), of("f", Udf1.of(e -> e)))).getMessage());

        assertEquals("Function sum(...) already defined", assertThrows(IllegalArgumentException.class,
                () -> functions(of("sum", new IntNSumFunction()), of("sum", UdfN.of(Exp::concat)))).getMessage());
    }

    private static class StrArgFunction implements Udf1<String, String> {
        @Override
        public Exp<String> call(Exp<String> exp) {
            return exp;
        }
    }

    private static class InheritedFunction extends StrArgFunction {
    }

    private static class IntArgFunction implements Udf1<Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> exp) {
            return exp;
        }
    }

    private static class BoolNumArgFunction implements Udf2<Boolean, Number, Boolean> {
        @Override
        public Exp<Boolean> call(Exp<Boolean> a, Exp<Number> b) {
            return a;
        }
    }

    private static class DateObjArgFunction implements Udf2<LocalDate, Object, LocalDate> {
        @Override
        public Exp<LocalDate> call(Exp<LocalDate> a, Exp<Object> b) {
            return a;
        }
    }

    private static class Int2SumFunction implements Udf2<Integer, Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> a, Exp<Integer> b) {
            return a.castAsInt().add(b.castAsInt()).castAsInt();
        }
    }

    private static class Int3SumFunction implements Udf3<Integer, Integer, Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> a, Exp<Integer> b, Exp<Integer> c) {
            return a.castAsInt().add(b.castAsInt()).castAsInt().add(c.castAsInt()).castAsInt();
        }
    }

    private static class IntNSumFunction implements UdfN<Number> {
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

    private static class CovariantFunction implements Udf2<Number, Integer, Number> {
        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public NumExp<Number> call(Exp<Number> exp, Exp<Integer> scale) {
            return (NumExp) exp.castAsDecimal();
        }
    }

    private static abstract class GenericBaseFunction<T, R> implements Udf1<T, R> {
        @SuppressWarnings("unchecked")
        @Override
        public Exp<R> call(Exp<T> exp) {
            return (Exp<R>) exp;
        }
    }

    private static class InheritedGenericFunction extends GenericBaseFunction<String, String> {
    }
}
