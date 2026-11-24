package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf0;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.UdfN;
import org.dflib.ql.fn.MatchesFunction;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$double;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QLFunctionDescriptorTest extends BaseQLFunctionsTest {

    private static Exp<?> call(QLFunctionDescriptor descriptor, Exp<?>... args) {
        return new QLFunctions(List.of(descriptor)).call(descriptor.name(), List.of(args));
    }

    @Test
    public void of_GenericSignature() {

        QLFunctionDescriptor f0 = QLFunctionDescriptor.of("f0", new CountUdf());
        QLFunctionDescriptor f1 = QLFunctionDescriptor.of("f1", new IntUdf());
        QLFunctionDescriptor f2 = QLFunctionDescriptor.of("f2", new IntStrUdf());
        QLFunctionDescriptor fN = QLFunctionDescriptor.of("fN", new VarArgsUdf());

        assertEquals("f0()", f0.shape());
        assertEquals("f1(NUMERIC)", f1.shape());
        assertEquals("f2(NUMERIC, STRING)", f2.shape());
        assertEquals("fN(...)", fN.shape());

        assertEquals(Exp.count(), call(f0));
        assertEquals($int("a").castAsInt(), call(f1, $int("a")));
        assertEquals(Exp.concat($int("a"), $str("b")), call(f2, $int("a"), $str("b")));
        assertEquals($int("a").add($int("b")), call(fN, $int("a"), $int("b")));

        // a parameter of a specific numeric type takes any numeric argument
        assertEquals($double("a").castAsInt(), call(f1, $double("a")));
        assertEquals($col("a").castAsNumber().castAsInt(), call(f1, $col("a")));
        assertThrows(IllegalArgumentException.class, () -> call(f1, $str("a")));
    }

    @Test
    public void of_TypedParam_UntypedArg() {

        QLFunctionDescriptor negation = QLFunctionDescriptor.of("negation", new NotUdf());

        assertEquals(Exp.not($col("a").castAsBool()), call(negation, $col("a")));
        assertEquals(Exp.not($bool("a")), call(negation, $bool("a")));
        assertThrows(IllegalArgumentException.class, () -> call(negation, $str("a")));
    }

    @Test
    public void of_CovariantReturn() {
        assertEquals("scale(NUMERIC, NUMERIC)", QLFunctionDescriptor.of("scale", new CovariantUdf()).shape());
    }

    @Test
    public void of_Lambda() {

        // a lambda's "call" is erased to "Exp call(Exp)", so it accepts arguments of any type
        QLFunctionDescriptor f0 = QLFunctionDescriptor.of("f0", Udf0.of(Exp::count));
        QLFunctionDescriptor f1 = QLFunctionDescriptor.of("f1", Udf1.of(e -> e.castAsStr().trim()));
        QLFunctionDescriptor f2 = QLFunctionDescriptor.of("f2", Udf2.of((a, b) -> Exp.concat(a, b)));
        QLFunctionDescriptor fN = QLFunctionDescriptor.of("fN", UdfN.of(Exp::concat));

        assertEquals("f0()", f0.shape());
        assertEquals("f1(OBJECT)", f1.shape());
        assertEquals("f2(OBJECT, OBJECT)", f2.shape());
        assertEquals("fN(...)", fN.shape());

        assertEquals($col("a").castAsStr().trim(), call(f1, $col("a")));
        assertEquals($int("a").castAsStr().trim(), call(f1, $int("a")));
        assertEquals(Exp.concat($int("a"), $str("b")), call(f2, $int("a"), $str("b")));
        assertEquals(Exp.concat($int("a"), $str("b"), $col("c")), call(fN, $int("a"), $str("b"), $col("c")));
    }

    @Test
    public void of_InheritedCall() {

        QLFunctionDescriptor format = QLFunctionDescriptor.of("format", new InheritedConcreteUdf());

        assertEquals("format(STRING)", format.shape());
        assertEquals($str("a").castAsStr(), call(format, $str("a")));
    }

    @Test
    public void of_InheritedGenericCall() {

        // the type variable of the superclass is not resolved, so the parameter takes arguments of any type
        assertEquals("f(OBJECT)", QLFunctionDescriptor.of("f", new InheritedGenericUdf()).shape());
    }

    @Test
    public void bodyException() {

        Environment.setQLContext(QLContext.builder()
                .function("boom", Udf1.of(e -> {
                    throw new IllegalArgumentException("boom() does not like " + e.toQL());
                }))
                .function("state", Udf1.of(e -> {
                    throw new IllegalStateException("not ready");
                }))
                .function("npe", Udf1.of(e -> {
                    throw new NullPointerException();
                }))
                .function("regex", new MatchesFunction())
                .build());

        assertEquals("Function 'boom' at 1:4: boom() does not like " + $int("a").toQL(),
                assertThrows(QLParserException.class, () -> parseExp("1 + boom(int(a))")).getMessage());

        assertEquals("Function 'state' at 1:4: not ready",
                assertThrows(QLParserException.class, () -> parseExp("1 + state(int(a))")).getMessage());

        // no message: the exception class stands in for it
        assertEquals("Function 'npe' at 2:0: NullPointerException",
                assertThrows(QLParserException.class, () -> parseExp("1 +\nnpe(int(a))")).getMessage());

        // a library exception from the body
        assertTrue(assertThrows(QLParserException.class, () -> parseExp("regex(str(a), '[')")).getMessage()
                .startsWith("Function 'regex' at 1:0: Unclosed character class"));
    }

    public static class NotUdf implements Udf1<Boolean, Boolean> {
        @Override
        public Exp<Boolean> call(Exp<Boolean> c) {
            return c.castAsBool().not();
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
}
