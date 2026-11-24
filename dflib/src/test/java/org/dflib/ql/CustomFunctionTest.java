package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.ql.fn.Substr2Function;
import org.dflib.ql.fn.UpperFunction;
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

        Environment.setQLContext(QLContext.builder()
                .function("twice", new TwiceFunction())
                .build());

        assertEquals(new TwiceFunction().call($int("a").abs()), parseExp("twice(abs(int(a)))"));
        assertEquals($int("a").abs(), parseExp("abs(int(a))"));
        assertEquals($col("a").castAsBool(), parseExp("castAsBool(a)"));
        assertEquals($int("a").sum(), parseExp("sum(int(a))"));
    }

    @Test
    public void customFunction_Lambda() {

        Environment.setQLContext(QLContext.builder()
                .function("clean", Udf1.of(e -> e.castAsStr().trim().lower()))
                .function("pair", Udf2.of((a, b) -> Exp.concat(a, "-", b)))
                .build());

        assertEquals($col("a").castAsStr().trim().lower(), parseExp("clean(a)"));
        assertEquals(Exp.concat($int("a"), "-", $str("b")), parseExp("pair(int(a), str(b))"));
    }

    @Test
    public void builtInClass_Alias() {

        Environment.setQLContext(QLContext.builder()
                .function("left", new Substr2Function())
                .build());

        assertEquals($col("a").substr(2), parseExp("left(a, 2)"));
    }

    @Test
    public void builtInClass_Delegate() {

        Environment.setQLContext(QLContext.builder()
                .function("shout", Udf1.of(e -> Exp.concat(new UpperFunction().call(e), "!")))
                .build());

        assertEquals(Exp.concat($col("a").upper(), "!"), parseExp("shout(a)"));
    }

    @Test
    public void noDefaultFunctions() {
        QLContext functions = QLContext.builder()
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
        QLContext.Builder builder = QLContext.builder().function("trim", new TrimFunction());

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, builder::build);
        assertEquals("Function trim(OBJECT) already defined", e.getMessage());

        assertEquals(new TrimFunction().call($col("a")), QLContext.builder()
                .noDefaultFunctions()
                .function("trim", new TrimFunction())
                .build()
                .call("trim", List.of($col("a"))));
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
}
