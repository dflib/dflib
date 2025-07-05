package org.dflib.ql;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

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
                arguments("shift(str(1), -1)", $str(1).shift(-1))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "SHIFT(int(1), 2)",
            "shift(int(1))",
            "shift(int(1), )",
            "shift(, 2)",
            "shift(int(1), 2, 'replace')",
            "shift(plusDays(date(1), 1), 1)",
    })
    public void shift_throws(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }
}
