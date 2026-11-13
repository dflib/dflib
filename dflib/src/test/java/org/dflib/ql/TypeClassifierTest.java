package org.dflib.ql;

import org.dflib.Condition;
import org.dflib.DateExp;
import org.dflib.DateTimeExp;
import org.dflib.DecimalExp;
import org.dflib.Exp;
import org.dflib.NumExp;
import org.dflib.OffsetDateTimeExp;
import org.dflib.StrExp;
import org.dflib.TimeExp;
import org.dflib.exp.flow.IfExp;
import org.dflib.exp.flow.IfNullExp;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;
import java.util.List;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$dateTime;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$offsetDateTime;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$time;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TypeClassifierTest {

    private static Method method(String name) {
        for (Method m : Fixtures.class.getDeclaredMethods()) {
            if (m.getName().equals(name)) {
                return m;
            }
        }
        throw new IllegalArgumentException("No such fixture method: " + name);
    }

    private static TypeClassifier returnOf(String method) {
        return TypeClassifier.classify(method(method).getGenericReturnType());
    }

    private static TypeClassifier paramOf(String method, int i) {
        return TypeClassifier.classify(method(method).getGenericParameterTypes()[i]);
    }

    @Test
    public void numExp_Wildcard() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("numWildcard", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("numWildcard"));
    }

    @Test
    public void numExp_Raw() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("rawNum", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("rawNum"));
    }

    @Test
    public void numExp_TypeVariable() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("numVar", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("numVar"));
    }

    @Test
    public void numExp_Parameterized() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("numParameterized", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("numParameterized"));
    }

    @Test
    public void decimalExp() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("decimal", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("decimal"));
    }

    @Test
    public void typedExpInterfaces() {
        assertEquals(TypeClassifier.STRING, paramOf("str", 0));
        assertEquals(TypeClassifier.STRING, returnOf("str"));

        assertEquals(TypeClassifier.BOOLEAN, paramOf("cond", 0));
        assertEquals(TypeClassifier.BOOLEAN, returnOf("cond"));

        assertEquals(TypeClassifier.DATE, paramOf("date", 0));
        assertEquals(TypeClassifier.DATE, returnOf("date"));

        assertEquals(TypeClassifier.TIME, paramOf("time", 0));
        assertEquals(TypeClassifier.TIME, returnOf("time"));

        assertEquals(TypeClassifier.DATETIME, paramOf("dateTime", 0));
        assertEquals(TypeClassifier.DATETIME, returnOf("dateTime"));

        assertEquals(TypeClassifier.OFFSETDATETIME, paramOf("offsetDateTime", 0));
        assertEquals(TypeClassifier.OFFSETDATETIME, returnOf("offsetDateTime"));
    }

    @Test
    public void bareTypeVariable() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("bareNumVar", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("bareNumVar"));

        assertEquals(TypeClassifier.OBJECT, paramOf("bareVar", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("bareVar"));
    }

    @Test
    public void expOfTypeVariable() {
        assertEquals(TypeClassifier.OBJECT, paramOf("expVar", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("expVar"));
    }

    @Test
    public void primitives() {
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 0)); // int
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 1)); // double
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 2)); // long
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 3)); // float
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 4)); // short
        assertEquals(TypeClassifier.NUMERIC, paramOf("primitives", 5)); // byte
        assertEquals(TypeClassifier.BOOLEAN, paramOf("primitives", 6)); // boolean
        assertEquals(TypeClassifier.OBJECT, paramOf("primitives", 7)); // char

        assertEquals(TypeClassifier.NUMERIC, returnOf("primitives"));
    }

    @Test
    public void valueTypes() {
        assertEquals(TypeClassifier.STRING, paramOf("values", 0));
        assertEquals(TypeClassifier.DATE, paramOf("values", 1));
        assertEquals(TypeClassifier.TIME, paramOf("values", 2));
        assertEquals(TypeClassifier.DATETIME, paramOf("values", 3));
        assertEquals(TypeClassifier.OFFSETDATETIME, paramOf("values", 4));
        assertEquals(TypeClassifier.NUMERIC, paramOf("values", 5)); // Integer
        assertEquals(TypeClassifier.BOOLEAN, paramOf("values", 6)); // Boolean
        assertEquals(TypeClassifier.OBJECT, paramOf("values", 7)); // Object
    }

    @Test
    public void rawExp() {
        assertEquals(TypeClassifier.OBJECT, paramOf("wildcardExp", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("wildcardExp"));

        assertEquals(TypeClassifier.NUMERIC, paramOf("intExp", 0));
        assertEquals(TypeClassifier.STRING, paramOf("strValueExp", 0));
        assertEquals(TypeClassifier.OBJECT, paramOf("objectExp", 0));
    }

    @Test
    public void expOfArray() {
        assertEquals(TypeClassifier.OBJECT, paramOf("arrayExp", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("arrayExp"));
    }

    @Test
    public void expOfNestedGeneric() {
        assertEquals(TypeClassifier.OBJECT, paramOf("listExp", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("listExp"));
    }

    @Test
    public void varArgs() {
        Method m = method("varargs");
        assertTrue(m.isVarArgs());
        assertEquals(TypeClassifier.OBJECT, paramOf("varargs", 0));
        assertEquals(TypeClassifier.NUMERIC, returnOf("varargs"));
    }

    @Test
    public void rawExp_Object() {
        // what a lambda's erased "call" method looks like
        assertEquals(TypeClassifier.OBJECT, paramOf("rawExp", 0));
        assertEquals(TypeClassifier.OBJECT, returnOf("rawExp"));
    }

    @Test
    public void classifyExp_TypedInterfaces() {
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify($int("a")));
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify($intVal(1)));
        assertEquals(TypeClassifier.STRING, TypeClassifier.classify($str("a")));
        assertEquals(TypeClassifier.BOOLEAN, TypeClassifier.classify($bool("a")));
        assertEquals(TypeClassifier.DATE, TypeClassifier.classify($date("a")));
        assertEquals(TypeClassifier.TIME, TypeClassifier.classify($time("a")));
        assertEquals(TypeClassifier.DATETIME, TypeClassifier.classify($dateTime("a")));
        assertEquals(TypeClassifier.OFFSETDATETIME, TypeClassifier.classify($offsetDateTime("a")));
    }

    @Test
    public void classifyExp_Object() {

        // whatever the value type of the expression
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($date("a").first()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($int("a").last()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(new IfNullExp<>($int("a"), $int("b"))));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(new IfExp<>($bool("c"), $int("a"), $int("b"))));

        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($col("a")));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($str("a").split(',')));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($col("a").list()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(Exp.$val(new Object())));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(Exp.$val(null)));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify((Exp<?>) null));
    }

    @Test
    public void isTyped() {
        for (TypeClassifier t : TypeClassifier.values()) {
            assertEquals(t != TypeClassifier.OBJECT, t.isTyped(), t.name());
        }
    }

    @Test
    public void cast() {
        assertEquals($col("a").castAsNumber(), TypeClassifier.NUMERIC.cast($col("a")));
        assertEquals($col("a").castAsStr(), TypeClassifier.STRING.cast($col("a")));
        assertEquals($col("a").castAsBool(), TypeClassifier.BOOLEAN.cast($col("a")));
        assertEquals($col("a").castAsDate(), TypeClassifier.DATE.cast($col("a")));
        assertEquals($col("a").castAsTime(), TypeClassifier.TIME.cast($col("a")));
        assertEquals($col("a").castAsDateTime(), TypeClassifier.DATETIME.cast($col("a")));
        assertEquals($col("a").castAsOffsetDateTime(), TypeClassifier.OFFSETDATETIME.cast($col("a")));

        // the cast result is an expression of the classifier's type
        for (TypeClassifier t : TypeClassifier.values()) {
            if (t.canCast()) {
                assertEquals(t, TypeClassifier.classify(t.cast($col("a"))), t.name());
            }
        }

        // no cast for OBJECT
        assertFalse(TypeClassifier.OBJECT.canCast());
        assertThrows(IllegalStateException.class, () -> TypeClassifier.OBJECT.cast($col("a")));
    }

    @SuppressWarnings({"rawtypes", "unused"})
    static class Fixtures {

        Exp rawExp(Exp e) {
            return e;
        }

        NumExp<?> numWildcard(NumExp<?> e) {
            return e;
        }

        NumExp rawNum(NumExp e) {
            return e;
        }

        <N extends Number> NumExp<N> numVar(NumExp<N> e) {
            return e;
        }

        NumExp<Integer> numParameterized(NumExp<Integer> e) {
            return e;
        }

        DecimalExp decimal(DecimalExp e) {
            return e;
        }

        StrExp str(StrExp e) {
            return e;
        }

        Condition cond(Condition e) {
            return e;
        }

        DateExp date(DateExp e) {
            return e;
        }

        TimeExp time(TimeExp e) {
            return e;
        }

        DateTimeExp dateTime(DateTimeExp e) {
            return e;
        }

        OffsetDateTimeExp offsetDateTime(OffsetDateTimeExp e) {
            return e;
        }

        <N extends Number> N bareNumVar(N filler) {
            return filler;
        }

        <T> T bareVar(T v) {
            return v;
        }

        <T> Exp<T> expVar(Exp<T> e) {
            return e;
        }

        int primitives(int i, double d, long l, float f, short s, byte b, boolean bool, char c) {
            return i;
        }

        void values(String s, LocalDate d, LocalTime t, LocalDateTime dt, OffsetDateTime odt, Integer i,
                    Boolean b, Object o) {
        }

        Exp<?> wildcardExp(Exp<?> e) {
            return e;
        }

        Exp<Integer> intExp(Exp<Integer> e) {
            return e;
        }

        Exp<String> strValueExp(Exp<String> e) {
            return e;
        }

        Exp<Object> objectExp(Exp<Object> e) {
            return e;
        }

        Exp<String[]> arrayExp(Exp<String[]> e) {
            return e;
        }

        Exp<List<String>> listExp(Exp<List<String>> e) {
            return e;
        }

        NumExp<?> varargs(Exp<?>... exps) {
            return (NumExp<?>) exps[0];
        }
    }
}
