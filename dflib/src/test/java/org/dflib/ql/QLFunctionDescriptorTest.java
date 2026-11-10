package org.dflib.ql;

import org.junit.jupiter.api.Test;

import static org.dflib.ql.DescriptorBuilder.descriptor;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.OBJECT;
import static org.dflib.ql.TypeClassifier.STRING;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class QLFunctionDescriptorTest {

    @Test
    public void shape() {
        assertEquals("f()", descriptor("f").as(args -> null).shape());
        assertEquals("f(...)", descriptor("f").varArgs().as(args -> null).shape());
        assertEquals("f(OBJECT, const NUMERIC)",
                descriptor("f").arg(OBJECT).constArg(NUMERIC).as(args -> null).shape());
        assertEquals("f(STRING, ...)", descriptor("f").arg(STRING).varArgs().as(args -> null).shape());
    }

    @Test
    public void testEquals() {
        QLFunctionDescriptor d = descriptor("f").arg(NUMERIC).as(args -> args.get(0));

        assertEquals(d, descriptor("f").arg(NUMERIC).as(args -> args.get(0).castAsStr()));
        assertEquals(d.hashCode(), descriptor("f").arg(NUMERIC).as(args -> args.get(0).castAsStr()).hashCode());

        assertNotEquals(d, descriptor("g").arg(NUMERIC).as(args -> args.get(0)));
        assertNotEquals(d, descriptor("f").constArg(NUMERIC).as(args -> args.get(0)));
        assertNotEquals(d, descriptor("f").arg(NUMERIC).varArgs().as(args -> args.get(0)));
        assertNotEquals(d, descriptor("f").arg(STRING).as(args -> args.get(0)));
    }

    @Test
    public void producer_throws() {
        assertThrows(NullPointerException.class, () -> descriptor("f").arg(NUMERIC).as(null));
    }
}
