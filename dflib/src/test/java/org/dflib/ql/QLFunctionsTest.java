package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.StrExp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$str;
import static org.dflib.ql.DescriptorBuilder.descriptor;
import static org.dflib.ql.TypeClassifier.BOOLEAN;
import static org.dflib.ql.TypeClassifier.DATE;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.OBJECT;
import static org.dflib.ql.TypeClassifier.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QLFunctionsTest {

    private static QLFunctionArg arg(TypeClassifier type) {
        return new QLFunctionArg(type, false);
    }

    private static QLFunctionArg constant(TypeClassifier type) {
        return new QLFunctionArg(type, true);
    }

    private static QLFunctions.Builder registry() {
        return QLFunctions.builder().noDefaultFunctions();
    }

    private static String notFound(QLFunctions functions, String name, QLFunctionArg... args) {
        return assertThrows(IllegalArgumentException.class, () -> functions.function(name, List.of(args))).getMessage();
    }

    @Test
    public void function_ByArity() {
        QLFunctions functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals(2, functions.function("sum", List.of(arg(NUMERIC), arg(NUMERIC))).args().size());
        assertEquals(3, functions.function("sum", List.of(arg(NUMERIC), arg(NUMERIC), arg(NUMERIC))).args().size());
    }

    @Test
    public void function_UnknownName() {
        QLFunctions functions = registry().function("sum", new Int2SumFunction()).build();

        assertEquals("Unknown function: multiply", notFound(functions, "multiply", arg(NUMERIC), arg(NUMERIC)));
    }

    @Test
    public void function_WrongArgTypes_throws() {
        QLFunctions functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals("No overload of sum matches sum(NUMERIC, STRING). Available:"
                        + " sum(NUMERIC<Integer>, NUMERIC<Integer>),"
                        + " sum(NUMERIC<Integer>, NUMERIC<Integer>, NUMERIC<Integer>)",
                notFound(functions, "sum", arg(NUMERIC), arg(STRING)));
    }

    @Test
    public void function_UntypedArg_Cast() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(STRING).as(args -> args.get(0)))
                .function(descriptor("g").arg(BOOLEAN).constArg(NUMERIC).as(args -> args.get(0)))
                .function(descriptor("h").arg(DATE).arg(OBJECT).as(args -> args.get(0)))
                .build();

        assertEquals(List.of(arg(STRING)), functions.function("f", List.of(arg(OBJECT))).args());
        assertEquals($col("a").castAsStr(), functions.call("f", List.of($col("a"))));
        assertEquals($str("a"), functions.call("f", List.of($str("a"))));

        assertEquals($col("a").castAsBool(), functions.call("g", List.of($col("a"), $intVal(1))));
        assertEquals($col("a").castAsDate(), functions.call("h", List.of($col("a"), $col("b"))));
    }

    @Test
    public void function_UntypedArg_ConstParam_throws() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(OBJECT).constArg(STRING).as(args -> args.get(0)))
                .build();

        assertEquals("No overload of f matches f(OBJECT, const OBJECT). Available: f(OBJECT, const STRING)",
                notFound(functions, "f", arg(OBJECT), constant(OBJECT)));
    }

    @Test
    public void function_UntypedArg_CastNumeric() {

        QLFunctions numOnly = registry()
                .function(descriptor("f").arg(NUMERIC).as(args -> args.get(0)))
                .build();

        assertEquals(List.of(arg(NUMERIC)), numOnly.function("f", List.of(arg(OBJECT))).args());
        assertEquals($col("a").castAsNumber(), numOnly.call("f", List.of($col("a"))));

        QLFunctions functions = registry()
                .function(descriptor("f").arg(NUMERIC).as(args -> args.get(0)))
                .function(descriptor("f").arg(DATE).as(args -> args.get(0)))
                .build();

        assertThrows(IllegalArgumentException.class, () -> functions.function("f", List.of(arg(OBJECT))));
    }

    @Test
    public void function_UntypedArg_WildcardPreferred() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(STRING).arg(STRING).as(args -> args.get(0)))
                .function(descriptor("f").arg(OBJECT).arg(OBJECT).as(args -> args.get(1)))
                .build();

        // "shift(a, 1, 'x')": one cast costs more than any number of wildcard matches
        assertEquals(List.of(arg(OBJECT), arg(OBJECT)), functions.function("f", List.of(arg(OBJECT), arg(STRING))).args());
        assertEquals(List.of(arg(STRING), arg(STRING)), functions.function("f", List.of(arg(STRING), arg(STRING))).args());
    }

    @Test
    public void function_UntypedArg_Ambiguous() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(STRING).as(args -> args.get(0)))
                .function(descriptor("f").arg(DATE).as(args -> args.get(0)))
                .build();

        assertEquals("Ambiguous call to f(OBJECT): argument 1 is untyped, cast it explicitly.",
                notFound(functions, "f", arg(OBJECT)));

        // an untyped overload takes the untyped argument without a cast, and resolves the ambiguity
        QLFunctions withWildcard = registry()
                .function(descriptor("f").arg(STRING).as(args -> args.get(0)))
                .function(descriptor("f").arg(DATE).as(args -> args.get(0)))
                .function(descriptor("f").arg(OBJECT).as(args -> args.get(0)))
                .build();

        assertEquals(List.of(arg(OBJECT)), withWildcard.function("f", List.of(arg(OBJECT))).args());
    }

    @Test
    public void function_UntypedArg_AmbiguousPosition() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(BOOLEAN).arg(DATE).as(args -> args.get(0)))
                .function(descriptor("f").arg(BOOLEAN).arg(STRING).as(args -> args.get(0)))
                .build();

        assertEquals("Ambiguous call to f(OBJECT, OBJECT): argument 2 is untyped, cast it explicitly.",
                notFound(functions, "f", arg(OBJECT), arg(OBJECT)));

        assertEquals(List.of(arg(BOOLEAN), arg(DATE)),
                functions.function("f", List.of(arg(OBJECT), arg(DATE))).args());
    }

    @Test
    public void function_UntypedArg_NumType() {
        QLFunctionArg intArg = new QLFunctionArg(NUMERIC, false, Integer.class);

        QLFunctions functions = registry()
                .function(descriptor("f").arg(NUMERIC).as(args -> args.get(0)))
                .function(descriptor("f").arg(intArg).as(args -> args.get(0)))
                .build();

        // "castAsNumber" and "castAsInt" are different casts
        assertEquals("Ambiguous call to f(OBJECT): argument 1 is untyped, cast it explicitly.",
                notFound(functions, "f", arg(OBJECT)));

        // a typed argument is not ambiguous: the matching value type wins, any other one goes to NumExp<?>
        assertEquals(List.of(intArg), functions.function("f", List.of(intArg)).args());
        assertEquals(List.of(arg(NUMERIC)),
                functions.function("f", List.of(new QLFunctionArg(NUMERIC, false, Double.class))).args());
    }

    @Test
    public void function_UntypedArg_TieElsewhere() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(DATE).arg(STRING).arg(OBJECT).as(args -> args.get(0)))
                .function(descriptor("f").arg(DATE).arg(OBJECT).arg(STRING).as(args -> args.get(0)))
                .build();

        assertEquals(List.of(arg(DATE), arg(STRING), arg(OBJECT)),
                functions.function("f", List.of(arg(OBJECT), arg(STRING), arg(STRING))).args());
    }

    @Test
    public void builder_InvalidName() {
        assertEquals("Function name must be a QL identifier (a letter, '$' or '_' followed by letters, digits,"
                        + " '$' or '_'), got: 'my fn'",
                assertThrows(IllegalArgumentException.class,
                        () -> registry().function("my fn", new ObjectArgFunction())).getMessage());

        assertThrows(IllegalArgumentException.class, () -> registry().function("1fn", new ObjectArgFunction()));
        assertThrows(IllegalArgumentException.class, () -> registry().function("", new ObjectArgFunction()));
    }

    @Test
    public void builder_KeywordName() {
        for (String keyword : List.of("date", "int", "col", "array", "and", "not", "in", "asc", "as", "null")) {
            assertEquals("Function name '" + keyword + "' is a QL keyword.",
                    assertThrows(IllegalArgumentException.class,
                            () -> registry().function(keyword, new ObjectArgFunction())).getMessage());
        }

        // a keyword in a different case or a longer name starting with a keyword is a valid identifier
        assertNotNull(registry().function("Date", new ObjectArgFunction()).build());
        assertNotNull(registry().function("dateOf", new ObjectArgFunction()).build());
    }

    @Test
    public void function_ExactMatchPreferred() {
        QLFunctions functions = registry()
                .function("f", new ObjectArgFunction())
                .function("f", new StrArgFunction())
                .build();

        assertEquals(List.of(arg(STRING)), functions.function("f", List.of(arg(STRING))).args());
        assertEquals(List.of(arg(OBJECT)), functions.function("f", List.of(arg(NUMERIC))).args());
        assertEquals(List.of(arg(OBJECT)), functions.function("f", List.of(arg(OBJECT))).args());
    }

    @Test
    public void function_FewerWildcardsPreferred() {
        QLFunctions functions = registry()
                .function(descriptor("f").arg(OBJECT).arg(OBJECT).as(args -> args.get(0)))
                .function(descriptor("f").arg(STRING).arg(OBJECT).as(args -> args.get(0)))
                .build();

        assertEquals(List.of(arg(STRING), arg(OBJECT)),
                functions.function("f", List.of(arg(STRING), arg(STRING))).args());
    }

    @Test
    public void function_EquallySpecific() {
        List<QLFunctionArg> argTypes = List.of(arg(STRING), arg(STRING));

        QLFunctions strFirst = registry()
                .function("f", new StrObjArgFunction())
                .function("f", new ObjStrArgFunction())
                .build();

        assertEquals(List.of(arg(STRING), arg(OBJECT)), strFirst.function("f", argTypes).args());

        QLFunctions objFirst = registry()
                .function("f", new ObjStrArgFunction())
                .function("f", new StrObjArgFunction())
                .build();

        assertEquals(List.of(arg(OBJECT), arg(STRING)), objFirst.function("f", argTypes).args());
    }

    @Test
    public void function_VarArgs() {
        QLFunctions functions = registry().function("sum", new IntNSumFunction()).build();

        assertTrue(functions.function("sum", List.of()).varArgs());
        assertTrue(functions.function("sum", List.of(arg(NUMERIC))).varArgs());
        assertTrue(functions.function("sum", List.of(arg(NUMERIC), arg(STRING), arg(DATE))).varArgs());
    }

    @Test
    public void function_VarArgs_FixedArityPreferred() {
        QLFunctions functions = registry()
                .function("sum", new IntNSumFunction())
                .function("sum", new Int2SumFunction())
                .function(descriptor("sum").as(args -> Exp.$intVal(0)))
                .build();

        assertFalse(functions.function("sum", List.of()).varArgs());
        assertFalse(functions.function("sum", List.of(arg(NUMERIC), arg(NUMERIC))).varArgs());
        assertTrue(functions.function("sum", List.of(arg(NUMERIC))).varArgs());
        assertTrue(functions.function("sum", List.of(arg(NUMERIC), arg(NUMERIC), arg(NUMERIC))).varArgs());
    }

    @Test
    public void function_VarArgs_LeadingParams() {
        QLFunctions functions = registry()
                .function(descriptor("vConcat").constArg(STRING).varArgs().as(args -> args.get(1)))
                .build();

        assertNotNull(functions.function("vConcat", List.of(constant(STRING), arg(NUMERIC), arg(DATE))));
        assertNotNull(functions.function("vConcat", List.of(constant(STRING))));

        assertThrows(IllegalArgumentException.class, () -> functions.function("vConcat", List.of()));
        assertThrows(IllegalArgumentException.class,
                () -> functions.function("vConcat", List.of(arg(STRING), arg(STRING))));
        assertThrows(IllegalArgumentException.class,
                () -> functions.function("vConcat", List.of(constant(NUMERIC), arg(STRING))));
    }

    @Test
    public void function_ConstantParam() {
        QLFunctions functions = registry().function("substr", new StrConstIntFunction()).build();

        assertEquals(List.of(arg(OBJECT), constant(NUMERIC)),
                functions.function("substr", List.of(arg(STRING), constant(NUMERIC))).args());

        assertEquals("No overload of substr matches substr(STRING, NUMERIC). Available: substr(OBJECT, const NUMERIC)",
                notFound(functions, "substr", arg(STRING), arg(NUMERIC)));

        assertEquals("No overload of substr matches substr(STRING, const STRING). Available: substr(OBJECT, const NUMERIC)",
                notFound(functions, "substr", arg(STRING), constant(STRING)));
    }

    @Test
    public void function_PlainParam_ConstArg() {
        QLFunctions functions = registry().function("sum", new Int2SumFunction()).build();

        assertNotNull(functions.function("sum", List.of(constant(NUMERIC), constant(NUMERIC))));
    }

    @Test
    public void function_Constancy() {
        QLFunctions functions = registry()
                .function("substr", new StrConstIntFunction())
                .function("substr", new StrIntFunction())
                .build();

        assertEquals(List.of(arg(OBJECT), constant(NUMERIC)),
                functions.function("substr", List.of(arg(STRING), constant(NUMERIC))).args());

        assertEquals(List.of(arg(OBJECT), new QLFunctionArg(NUMERIC, false, Integer.class)),
                functions.function("substr", List.of(arg(STRING), arg(NUMERIC))).args());
    }

    @Test
    public void builder_DuplicateShape_throws() {
        QLFunctions.Builder builder = registry().function("substr", new StrConstIntFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> builder.function("substr", new StrConstIntFunction()));
        assertEquals("Function substr(OBJECT, const NUMERIC) already defined", e.getMessage());

        // the same shape via a different registration path
        assertThrows(IllegalArgumentException.class, () -> registry()
                .function(descriptor("f").arg(NUMERIC).as(args -> args.get(0)))
                .function(descriptor("f").arg(NUMERIC).as(args -> args.get(0).castAsStr())));
    }

    @Test
    public void call() {
        QLFunctions functions = registry()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        assertEquals(new Int2SumFunction().call(Exp.$int("a"), Exp.$int("b")),
                functions.call("sum", List.of(Exp.$int("a"), Exp.$int("b"))));
        assertEquals(new Int3SumFunction().call(Exp.$int("a"), Exp.$int("b"), Exp.$int("c")),
                functions.call("sum", List.of(Exp.$int("a"), Exp.$int("b"), Exp.$int("c"))));

        assertThrows(IllegalArgumentException.class, () -> functions.call("sum", List.of(Exp.$str("a"), Exp.$int("b"))));
    }

    public static class StrConstIntFunction implements QLFunction {
        public StrExp call(Exp<?> exp, int from) {
            return exp.substr(from);
        }
    }

    private static class StrIntFunction implements Udf2<Object, Integer, String> {
        @Override
        public Exp<String> call(Exp<Object> exp, Exp<Integer> from) {
            return exp.castAsStr();
        }
    }

    private static class StrObjArgFunction implements Udf2<String, Object, String> {
        @Override
        public Exp<String> call(Exp<String> a, Exp<Object> b) {
            return Exp.concat(a, b);
        }
    }

    private static class ObjStrArgFunction implements Udf2<Object, String, String> {
        @Override
        public Exp<String> call(Exp<Object> a, Exp<String> b) {
            return Exp.concat(a, b);
        }
    }

    private static class StrArgFunction implements Udf1<String, String> {
        @Override
        public Exp<String> call(Exp<String> exp) {
            return exp.castAsStr().trim();
        }
    }

    private static class ObjectArgFunction implements Udf1<Object, String> {
        @Override
        public Exp<String> call(Exp<Object> exp) {
            return exp.trim();
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
