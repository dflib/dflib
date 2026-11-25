package org.dflib.ql;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.dflib.Exp.$str;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ShiftTest {

    @ParameterizedTest
    @MethodSource
    public void shift(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> shift() {
        return Stream.of(
                arguments("shift(int(1), 2)", $int(1).shift(2)),
                arguments("shift(int(1), -2)", $int(1).shift(-2)),
                arguments("shift(int(1), 2, 0)", $int(1).shift(2, 0)),
                arguments("shift(1 + 2, 1)", $intVal(1).add(2).shift(1)),
                arguments("shift(str(1), 1)", $str(1).shift(1)),
                arguments("shift(str(1), 1, 'default')", $str(1).shift(1, "default")),
                arguments("shift(str(1), -1)", $str(1).shift(-1)),
                arguments("shift(date(a), 1)", $date("a").shift(1)),
                arguments("shift(plusDays(date(1), 1), 1)", $date(1).plusDays(1).shift(1)),
                arguments("shift(a, 1, 'x')", $col("a").shift(1, "x")),
                arguments("shift(a, 2)", $col("a").shift(2)),
                arguments("shift(true, 1)", $boolVal(true).shift(1)),
                arguments("shift(int(1) > 0, 1, false)", $int(1).gt(0).shift(1, false)),
                arguments("shift(int(a), - 1)", $int("a").shift(-1)),
                arguments("shift(int(a), -(1), -(-0))", $int("a").shift(-1, 0)),

                // a numeric filler is converted to the type of the expression
                arguments("shift(long(a), 1, 99L)", $long("a").shift(1, 99L)),
                arguments("shift(long(a), 1, 99)", $long("a").shift(1, 99L)),
                arguments("shift(int(a), 1, 99L)", $int("a").shift(1, 99)),
                arguments("shift(double(a), 1, 0.1)", $double("a").shift(1, 0.1)),
                arguments("shift(decimal(a), 1, 1.5)", $decimal("a").shift(1, new BigDecimal("1.5"))),
                arguments("shift(bigint(a), 1, 10)", $bigint("a").shift(1, BigInteger.TEN))
        );
    }

    @Test
    public void shift_Eval() {
        DataFrame df = DataFrame.foldByRow("a").of(1L, 2L, 3L);
        new SeriesAsserts(df.cols().select(parseExp("shift(long(a), 1, 0)")).getColumn(0)).expectData(0L, 1L, 2L);
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SHIFT(int(1), 2)",
            "shift(int(1))",
            "shift(int(1), )",
            "shift(, 2)",
            "shift(true, 1, 0)",
    })
    public void shift_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }
}
