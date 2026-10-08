package org.dflib.ql;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.math.BigInteger;
import java.time.LocalDate;
import java.util.stream.Stream;

import static org.dflib.Exp.$bigintVal;
import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$boolVal;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$dateVal;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$doubleVal;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$longVal;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class OperatorPrecedenceTest {

    @Test
    public void negate_Literal() {
        assertEquals($intVal(-1), parseExp("- 1"));
        assertEquals($intVal(1), parseExp("-(-1)"));
        assertEquals($doubleVal(-1.5), parseExp("- 1.5d"));

        // the negation of the min value does not fit the type, so it is widened
        assertEquals($longVal(2147483648L), parseExp("-(-2147483648)"));
        assertEquals($bigintVal(BigInteger.valueOf(Long.MIN_VALUE).negate()), parseExp("-(-9223372036854775808)"));

        // a column is still negated as an expression
        assertEquals($int("a").negate(), parseExp("- int(a)"));
    }

    @ParameterizedTest
    @MethodSource
    public void precedence(String text, Exp<?> expected) {
        assertEquals(expected, parseExp(text));
    }

    static Stream<Arguments> precedence() {
        return Stream.of(
                arguments("1 + 2 * 3", $intVal(1).add($intVal(2).mul($intVal(3)))),
                arguments("- int(a) * 2", $int("a").negate().mul($intVal(2))),
                arguments("1 + 2 > 2 and true", $intVal(1).add($intVal(2)).gt($intVal(2)).and($boolVal(true))),
                arguments("not int(a) > 1", Exp.not($int("a").gt($intVal(1)))),
                arguments("not bool(a) and bool(b)", Exp.not($bool("a")).and($bool("b"))),
                arguments("bool(a) or bool(b) and bool(c)", $bool("a").or($bool("b").and($bool("c")))),
                arguments("(bool(a) or bool(b)) and bool(c)", $bool("a").or($bool("b")).and($bool("c"))),
                arguments("int(a) > 1 = true", $int("a").gt($intVal(1)).eq($boolVal(true))),
                arguments("true = 5 < 4", $boolVal(true).eq($intVal(5).lt($intVal(4)))),

                // boolean equality binds tighter than "not", "and" and "or"
                arguments("bool(a) and bool(b) = bool(c)", $bool("a").and($bool("b").eq($bool("c")))),
                arguments("not bool(a) = true", Exp.not($bool("a").eq($boolVal(true)))),

                // bounds of a temporal range can mix expressions and ISO-8601 literals
                arguments("date(a) between '2020-01-01' and date(b)",
                        $date("a").between($dateVal(LocalDate.parse("2020-01-01")), $date("b"))),
                arguments("date(a) not between date(b) and '2020-12-31'",
                        $date("a").notBetween($date("b"), $dateVal(LocalDate.parse("2020-12-31")))),

                // the bounds of "between" bind tighter than "and"
                arguments("int(a) between 1 and 5 and bool(b)",
                        $int("a").between($intVal(1), $intVal(5)).and($bool("b"))),
                arguments("min(int(a)) between 1 and 5 and bool(b)",
                        $int("a").min().between($intVal(1), $intVal(5)).and($bool("b"))),
                arguments("int(a) not between 1 + 1 and 5 * 2 or bool(b)",
                        $int("a").notBetween($intVal(1).add($intVal(1)), $intVal(5).mul($intVal(2))).or($bool("b"))),
                arguments("str(a) in ('x') and str(b) not in ('y')",
                        $str("a").in("x").and($str("b").notIn("y"))),
                arguments("str(a) = 'x' or 1 + 1 = 2",
                        $str("a").eq($strVal("x")).or($intVal(1).add($intVal(1)).eq($intVal(2))))
        );
    }
}
