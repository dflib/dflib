package org.dflib.exp;

import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.exp.num.IntScalarExp;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$int;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ExpBaseTest {

    protected void assertEqualsContract(Object x, Object y, Object z) {
        assertEquals(x, x, "Reflexive: object must equal itself");
        
        assertEquals(x, y, "Symmetric: x should equal y");
        assertEquals(y, x, "Symmetric: y should equal x");
        
        assertEquals(y, z, "Transitive: y should equal z");
        assertEquals(x, z, "Transitive: x should equal z");
        
        assertNotEquals(x, null, "Not equal to null");
        
        assertEquals(x.hashCode(), x.hashCode(), "HashCode must be consistent");
        assertEquals(x.hashCode(), y.hashCode(), "Equal objects must have equal hashCodes");
        assertEquals(y.hashCode(), z.hashCode(), "Equal objects must have equal hashCodes");
        
        assertHashCodeProperties(x);
        assertHashCodeProperties(y);
        assertHashCodeProperties(z);
    }

    protected void assertHashCodeProperties(Object obj) {
        int hash1 = obj.hashCode();
        int hash2 = obj.hashCode();
        int hash3 = obj.hashCode();
        assertEquals(hash1, hash2, "HashCode must be consistent across multiple calls");
        assertEquals(hash2, hash3, "HashCode must be consistent across multiple calls");
        
        assertNotEquals(0, hash1, "HashCode should not be 0");
        assertNotEquals(1, hash1, "HashCode should not be 1");
        assertNotEquals(obj.getClass().getName().hashCode(), hash1, 
            "HashCode should not simply be the class name hash");
        
        assertNotEquals(System.identityHashCode(obj), hash1,
            "HashCode should be overridden and not use default Object.hashCode()");
    }

    @Test
    public void equalsHashCode_deepNestedExpressions() {
        Exp<?> e1 = Exp.ifExp(
                Exp.ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                Exp.ifExp($bool("b"), $int(3), $int(4)),
                $int(5)
        );
        Exp<?> e2 = Exp.ifExp(
                Exp.ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                Exp.ifExp($bool("b"), $int(3), $int(4)),
                $int(5)
        );
        Exp<?> e3 = Exp.ifExp(
                Exp.ifExp($bool("a"), $int(1), $int(2)).castAsBool(),
                Exp.ifExp($bool("b"), $int(3), $int(4)),
                $int(5)
        );

        assertEqualsContract(e1, e2, e3);
    }

    @Test
    public void equalsHashCode_mixedNumericTypes() {
        NumExp<?> e1 = $int("a").add(5L);
        NumExp<?> e2 = $int("a").add(5L);
        NumExp<?> e3 = $int("a").add(5L);
        NumExp<?> different = $int("a").add(5);

        assertEqualsContract(e1, e2, e3);
        assertNotEquals(e1, different);
    }
}
