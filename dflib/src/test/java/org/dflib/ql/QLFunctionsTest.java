package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf2;
import org.dflib.Udf3;
import org.dflib.UdfN;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class QLFunctionsTest {

    @Test
    void function_Matching2ArgTypes() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        List<QLFunctionDescriptor.TypeClassifier> argTypes = List.of(
                QLFunctionDescriptor.TypeClassifier.NUMERIC,
                QLFunctionDescriptor.TypeClassifier.NUMERIC
        );

        QLFunctionDescriptor result = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC, argTypes);

        assertNotNull(result);
        assertEquals("sum", result.name());
        assertEquals(QLFunctionDescriptor.TypeClassifier.NUMERIC, result.returnType());
        assertEquals(2, result.argTypes().length);
    }

    @Test
    void function_Matching3ArgTypes() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new Int2SumFunction())
                .function("sum", new Int3SumFunction())
                .build();

        List<QLFunctionDescriptor.TypeClassifier> argTypes = List.of(
                QLFunctionDescriptor.TypeClassifier.NUMERIC,
                QLFunctionDescriptor.TypeClassifier.NUMERIC,
                QLFunctionDescriptor.TypeClassifier.NUMERIC
        );

        QLFunctionDescriptor result = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC, argTypes);

        assertNotNull(result);
        assertEquals("sum", result.name());
        assertEquals(QLFunctionDescriptor.TypeClassifier.NUMERIC, result.returnType());
        assertEquals(3, result.argTypes().length);
    }

    @Test
    void function_NoMatchingName() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new Int2SumFunction())
                .build();
        List<QLFunctionDescriptor.TypeClassifier> argTypes = List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> functions.function("multiply", QLFunctionDescriptor.TypeClassifier.NUMERIC, argTypes)
        );
        assertEquals("Function NUMERIC multiply([NUMERIC, NUMERIC]) not found", exception.getMessage());
    }

    @Test
    void function_WrongArgTypes() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new Int2SumFunction())
                .build();
        List<QLFunctionDescriptor.TypeClassifier> wrongArgTypes = List.of(
                QLFunctionDescriptor.TypeClassifier.NUMERIC,
                QLFunctionDescriptor.TypeClassifier.STRING
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC, wrongArgTypes)
        );
        assertEquals("Function NUMERIC sum([NUMERIC, STRING]) not found", exception.getMessage());
    }

    @Test
    void function_WrongReturnType() {
        QLFunctions functions = QLFunctions.builder()
                .function("concat", (Udf2<String, String, String>) Exp::concat)
                .build();
        List<QLFunctionDescriptor.TypeClassifier> argTypes = List.of(
                QLFunctionDescriptor.TypeClassifier.STRING,
                QLFunctionDescriptor.TypeClassifier.STRING
        );

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> functions.function("concat", QLFunctionDescriptor.TypeClassifier.NUMERIC, argTypes)
        );
        assertEquals("Function NUMERIC concat([STRING, STRING]) not found", exception.getMessage());
    }

    @Test
    void function_VarArgs_MatchesAnyArity() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new IntNSumFunction())
                .build();

        // match with 2 args
        QLFunctionDescriptor result2 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC,
                List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC));
        assertNotNull(result2);
        assertTrue(result2.isVarArgs());

        // match with 3 args
        QLFunctionDescriptor result3 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC,
                List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC));
        assertNotNull(result3);
        assertTrue(result3.isVarArgs());

        // match with 1 arg
        QLFunctionDescriptor result1 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC,
                List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC));
        assertNotNull(result1);
        assertTrue(result1.isVarArgs());

        // match with 0 args
        QLFunctionDescriptor result0 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC, List.of());
        assertNotNull(result0);
        assertTrue(result0.isVarArgs());
    }

    @Test
    void function_VarArgs_FixedArityPreferred() {
        QLFunctions functions = QLFunctions.builder()
                .function("sum", new Int2SumFunction())
                .function("sum", new IntNSumFunction())
                .build();

        // 2-arg call should prefer the fixed-arity Udf2
        QLFunctionDescriptor result2 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC,
                List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC));
        assertNotNull(result2);
        assertFalse(result2.isVarArgs());
        assertEquals(2, result2.argTypes().length);

        // 3-arg call should fall back to varargs
        QLFunctionDescriptor result3 = functions.function("sum", QLFunctionDescriptor.TypeClassifier.NUMERIC,
                List.of(QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC, QLFunctionDescriptor.TypeClassifier.NUMERIC));
        assertNotNull(result3);
        assertTrue(result3.isVarArgs());
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