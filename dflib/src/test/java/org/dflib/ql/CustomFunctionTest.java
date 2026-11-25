package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.ql.fn.Substr2Function;
import org.dflib.ql.fn.UpperFunction;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class CustomFunctionTest {

    @BeforeEach
    public void setContext() {
        Environment.setQLContext(QLContext.builder()
                .function("twice", new TwiceFunction())
                .function("clean", Udf1.of(e -> e.castAsStr().trim().lower()))
                .function("pair", Udf2.of((a, b) -> concat(a, "-", b)))
                .function("left", new Substr2Function())
                .function("shout", Udf1.of(e -> concat(new UpperFunction().call(e), "!")))
                .function("id", Udf1.of(e -> e))
                .function("id2", Udf2.of((a, b) -> a))
                .function("toInt", Udf1.of(e -> e.castAsInt()))
                .function("firstOf", Udf1.of(e -> e.first()))
                .function("boom", Udf1.of(e -> {
                    throw new IllegalArgumentException("boom() does not like " + e.toQL());
                }))
                .function("state", Udf1.of(e -> {
                    throw new IllegalStateException("not ready");
                }))
                .function("npe", Udf1.of(e -> {
                    throw new NullPointerException();
                }))
                .build());
    }

    @AfterEach
    public void resetContext() {
        Environment.setQLContext(null);
    }

    @ParameterizedTest
    @MethodSource
    public void function(String text, Exp<?> expected) {
        assertEquals(expected, parseExp(text));
    }

    static Stream<Arguments> function() {
        return Stream.of(
                arguments("twice(abs(int(a)))", new TwiceFunction().call($int("a").abs())),
                arguments("clean(a)", $col("a").castAsStr().trim().lower()),
                arguments("pair(int(a), str(b))", concat($int("a"), "-", $str("b"))),
                arguments("left(a, 2)", $col("a").substr(2)),
                arguments("shout(a)", concat($col("a").upper(), "!")),
                arguments("abs(int(a))", $int("a").abs()),

                // an untyped result is dispatched on the expression returned
                arguments("id(str(a))", $str("a")),
                arguments("id(firstOf(a))", $col("a").first()),
                arguments("id(int(a)) + 1", $int("a").add($intVal(1))),
                arguments("1 - id(int(a))", $intVal(1).sub($int("a"))),
                arguments("1 + (id(int(a))) * 2", $intVal(1).add($int("a").mul($intVal(2)))),
                arguments("- id(int(a))", $int("a").negate()),
                arguments("not id(bool(a))", not($bool("a"))),
                arguments("id(bool(a)) and bool(b)", $bool("a").and($bool("b"))),
                arguments("id(int(a)) + 1 > 5", $int("a").add($intVal(1)).gt($intVal(5))),
                arguments("id2(int(a), 1) <= 5", $int("a").le($intVal(5))),
                arguments("int(b) <= id(int(a))", $int("b").le($int("a"))),
                arguments("id(date(a)) > '2024-01-01'", $date("a").gt("2024-01-01")),
                arguments("id(a) = id(b)", $col("a").eq($col("b"))),
                arguments("id2(int(a), 1) not between 1 and 5", $int("a").notBetween($intVal(1), $intVal(5))),
                arguments("id(date(a)) between '2024-01-01' and '2024-12-31'",
                        $date("a").between("2024-01-01", "2024-12-31")),
                arguments("id(str(a)) in ('x', 'y')", $str("a").in("x", "y")),
                arguments("id(date(a)) in ('2024-01-01')", $date("a").in(LocalDate.parse("2024-01-01"))),
                arguments("year(id(date(a)))", $date("a").year()),
                arguments("toInt(id(a)) + 1", $col("a").castAsInt().add($intVal(1))),
                arguments("id(id2(date(a), 1))", $date("a"))
        );
    }

    @Test
    public void function_Param() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        assertEquals($dateTime("a").gt($dateTimeVal(dt)), parseExp("id(dateTime(a)) > ?", dt));
    }

    @ParameterizedTest
    @MethodSource
    public void function_throws(String text, String expectedMessage) {
        assertEquals(expectedMessage, assertThrows(QLParserException.class, () -> parseExp(text)).getMessage());
    }

    static Stream<Arguments> function_throws() {
        return Stream.of(
                arguments("1 + boom(int(a))", "Function 'boom' at 1:4: boom() does not like a"),
                arguments("1 + state(int(a))", "Function 'state' at 1:4: not ready"),
                // no message: the exception class stands in for it
                arguments("1 +\nnpe(int(a))", "Function 'npe' at 2:0: NullPointerException"),
                arguments("id(str(a)) + 1", "Operator '+' at 1:11 expects a numeric operand, got a string one: a"),
                arguments("date(a) > id(str(b))", "Operator '>' at 1:8 expects a date operand, got a string one: b")
        );
    }

    @Test
    public void builtInCollision_throws() {
        QLContext.Builder builder = QLContext.builder().function("trim", Udf1.of(e -> e));
        assertEquals("Function trim(OBJECT) already defined",
                assertThrows(IllegalArgumentException.class, builder::build).getMessage());
    }

    private static class TwiceFunction implements Udf1<Integer, Integer> {
        @Override
        public Exp<Integer> call(Exp<Integer> exp) {
            return exp.castAsInt().mul($intVal(2)).castAsInt();
        }
    }
}
