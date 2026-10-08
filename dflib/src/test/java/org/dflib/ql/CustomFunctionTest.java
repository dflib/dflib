package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.StrExp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class CustomFunctionTest extends BaseQLFunctionsTest {

    @Test
    public void customFunction() {

        Environment.setQLFunctions(QLFunctions.builder()
                .function("twice", new TwiceFunction())
                .build());

        assertEquals(new TwiceFunction().call($int("a").abs()), parseExp("twice(abs(int(a)))"));
        assertEquals($int("a").abs(), parseExp("abs(int(a))"));
        assertEquals($col("a").castAsBool(), parseExp("castAsBool(a)"));
        assertEquals($int("a").sum(), parseExp("sum(int(a))"));
    }

    @Test
    public void customFunction_Lambda() {

        Environment.setQLFunctions(QLFunctions.builder()
                .function("clean", Udf1.of(e -> e.castAsStr().trim().lower()))
                .function("pair", Udf2.of((a, b) -> Exp.concat(a, "-", b)))
                .build());

        assertEquals($col("a").castAsStr().trim().lower(), parseExp("clean(a)"));
        assertEquals(Exp.concat($int("a"), "-", $str("b")), parseExp("pair(int(a), str(b))"));
    }

    @Test
    public void noDefaultFunctions() {
        QLFunctions functions = QLFunctions.builder()
                .noDefaultFunctions()
                .function("twice", new TwiceFunction())
                .build();

        assertEquals(new TwiceFunction().call($int("a")), functions.call("twice", List.of($int("a"))));

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
                () -> functions.call("abs", List.of($int("a"))));
        assertEquals("Unknown function: abs", e.getMessage());
    }

    @Test
    public void customFunction_BuiltInCollision() {

        // reported at build time, as that is when the built-ins are added
        QLFunctions.Builder builder = QLFunctions.builder().function("trim", new TrimFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Function trim(OBJECT) already defined", e.getMessage());

        assertEquals(new TrimFunction().call($col("a")), QLFunctions.builder()
                .noDefaultFunctions()
                .function("trim", new TrimFunction())
                .build()
                .call("trim", List.of($col("a"))));
    }

    @Test
    public void customOverload_MoreSpecific() {

        // the built-in "trim" is declared over "Exp<?>", so a "StrExp" overload is a better match for a string
        Environment.setQLFunctions(QLFunctions.builder()
                .function("trim", new StrTrimFunction())
                .build());

        assertEquals(new StrTrimFunction().call($str("a")), parseExp("trim(str(a))"));
        assertEquals($col("a").trim(), parseExp("trim(a)"));
    }

    @Test
    public void customOverload_Tied() {

        // "vConcat(str(a), ',')" matches the built-in "vConcat(Exp<?>, const String)" and the custom
        // "vConcat(StrExp, const Object)" with one wildcard each, and the built-in is registered first
        Environment.setQLFunctions(QLFunctions.builder()
                .function("vConcat", new StrVConcatFunction())
                .build());

        assertEquals($str("a").vConcat(null, ",", "", ""), parseExp("vConcat(str(a), ',')"));
    }

    private static class TwiceFunction implements Udf1<Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> exp) {
            return exp.castAsInt().mul($intVal(2)).castAsInt();
        }
    }

    private static class TrimFunction implements Udf1<Object, String> {
        @Override
        public Exp<String> call(Exp<Object> exp) {
            return exp.castAsStr().trim();
        }
    }

    private static class StrTrimFunction implements Udf1<String, String> {
        @Override
        public Exp<String> call(Exp<String> exp) {
            return exp.castAsStr().trim().upper();
        }
    }

    public static class StrVConcatFunction implements QLFunction {
        public Exp<?> call(StrExp e, Object delimiter) {
            return e.vConcat(null, String.valueOf(delimiter), "<", ">");
        }
    }
}
