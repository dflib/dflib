package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.Udf1;
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

    public static class AdditionFn implements Udf1<Number, Number> {

        @SuppressWarnings({"unchecked", "rawtypes"})
        @Override
        public NumExp<Number> call(Exp<Number> exp) {
            return ((NumExp) exp).add(1);
        }
    }
}