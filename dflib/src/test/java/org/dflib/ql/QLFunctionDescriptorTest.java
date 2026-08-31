package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf1;
import org.dflib.UdfN;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.*;

class QLFunctionDescriptorTest {


    @Test
    void test() {
        QLFunctionDescriptor descriptor = QLFunctionDescriptor.ofUdf1(new AdditionFn()).name("add").build();
        assertNotNull(descriptor);

        Exp<?> fnCall = descriptor.expProducer().apply(List.of($int("a")));
        assertNotNull(fnCall);
    }

    @Test
    void udfN() {
        QLFunctionDescriptor descriptor = QLFunctionDescriptor.ofUdfN(new VarArgsFn()).name("vadd").build();
        assertNotNull(descriptor);
        assertTrue(descriptor.isVarArgs());
        assertEquals(0, descriptor.argTypes().length);
        assertEquals(QLFunctionDescriptor.TypeClassifier.NUMERIC, descriptor.returnType());

        Exp<?> fnCall = descriptor.expProducer().apply(List.of($int("a"), $int("b")));
        assertNotNull(fnCall);
    }

    public static class VarArgsFn implements UdfN<Number> {
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

    public static class AdditionFn implements Udf1<Number, Number> {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public NumExp<Number> call(Exp<Number> exp) {
            return ((NumExp) exp).add(1);
        }
    }
}