package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.dflib.ql.fn.Substr2Function;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QLFunctionsTest {

    private static QLContext.Builder registry() {
        return QLContext.builder().noDefaultFunctions();
    }

    private static String error(QLContext functions, String name, Exp<?>... args) {
        return assertThrows(IllegalArgumentException.class, () -> functions.call(name, List.of(args))).getMessage();
    }

    @Test
    public void call_ByArity() {
        QLContext functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals(new Int2SumFunction().call($int("a"), $int("b")),
                functions.call("sum", List.of($int("a"), $int("b"))));
        assertEquals(new Int3SumFunction().call($int("a"), $int("b"), $int("c")),
                functions.call("sum", List.of($int("a"), $int("b"), $int("c"))));
    }

    @Test
    public void call_UnknownName() {
        QLContext functions = registry().function("sum", new Int2SumFunction()).build();

        assertEquals("Unknown function: multiply", error(functions, "multiply", $int("a"), $int("b")));
    }

    @Test
    public void call_WrongArity_throws() {
        QLContext functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals("No overload of sum matches sum(NUMERIC). Available: sum(NUMERIC, NUMERIC),"
                        + " sum(NUMERIC, NUMERIC, NUMERIC)",
                error(functions, "sum", $int("a")));
    }

    @Test
    public void call_WrongArgTypes_throws() {
        QLContext functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals("No overload of sum matches sum(NUMERIC, STRING). Available: sum(NUMERIC, NUMERIC),"
                        + " sum(NUMERIC, NUMERIC, NUMERIC)",
                error(functions, "sum", $int("a"), $str("b")));
    }

    @Test
    public void call_UntypedArg_Cast() {
        QLContext functions = registry()
                .function("f", new StrArgFunction())
                .function("g", new BoolNumArgFunction())
                .function("h", new DateObjArgFunction())
                .build();

        assertEquals($col("a").castAsStr(), functions.call("f", List.of($col("a"))));
        assertEquals($str("a"), functions.call("f", List.of($str("a"))));

        assertEquals($col("a").castAsBool(), functions.call("g", List.of($col("a"), $intVal(1))));
        assertEquals($col("a").castAsBool(), functions.call("g", List.of($col("a").castAsBool(), $col("b"))));
        assertEquals($col("a").castAsDate(), functions.call("h", List.of($col("a"), $col("b"))));
    }

    @Test
    public void call_ObjectParam() {
        QLContext functions = registry().function("f", Udf1.of(e -> e)).build();

        assertEquals($col("a"), functions.call("f", List.of($col("a"))));
        assertEquals($int("a"), functions.call("f", List.of($int("a"))));
        assertEquals($str("a"), functions.call("f", List.of($str("a"))));
    }

    @Test
    public void call_VarArgs() {
        QLContext functions = registry().function("sum", new IntNSumFunction()).build();

        assertEquals($int("a"), functions.call("sum", List.of($int("a"))));
        assertEquals($int("a").add($int("b")).add($int("c")),
                functions.call("sum", List.of($int("a"), $int("b"), $int("c"))));
    }

    @Test
    public void call_VarArgs_FixedArityPreferred() {
        QLContext functions = registry()
                .function("sum", new IntNSumFunction())
                .function("sum", new Int2SumFunction())
                .build();

        assertEquals(new Int2SumFunction().call($int("a"), $int("b")),
                functions.call("sum", List.of($int("a"), $int("b"))));
        assertEquals($int("a"), functions.call("sum", List.of($int("a"))));
        assertEquals($int("a").add($int("b")).add($int("c")),
                functions.call("sum", List.of($int("a"), $int("b"), $int("c"))));
    }

    @Test
    public void call_ConstantParam() {
        QLContext functions = registry().function("substr", new Substr2Function()).build();

        assertEquals($str("a").substr(1), functions.call("substr", List.of($str("a"), $intVal(1))));

        assertEquals("Not a constant: b", error(functions, "substr", $str("a"), $int("b")));
        assertEquals("No overload of substr matches substr(STRING, STRING). Available: substr(OBJECT, NUMERIC)",
                error(functions, "substr", $str("a"), $str("b")));
    }

    @Test
    public void build_DuplicateArity_throws() {
        QLContext.Builder fixed = registry()
                .function("f", new StrArgFunction())
                .function("f", Udf1.of(e -> e));
        assertEquals("Function f(OBJECT) already defined",
                assertThrows(IllegalArgumentException.class, fixed::build).getMessage());

        QLContext.Builder varArgs = registry()
                .function("sum", new IntNSumFunction())
                .function("sum", UdfN.of(Exp::concat));
        assertEquals("Function sum(...) already defined",
                assertThrows(IllegalArgumentException.class, varArgs::build).getMessage());
    }

    private static class StrArgFunction implements Udf1<String, String> {
        @Override
        public Exp<String> call(Exp<String> exp) {
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
}
