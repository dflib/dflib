package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.Udf2;
import org.dflib.Udf3;
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