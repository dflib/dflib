package org.dflib.ql;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class IfExpTest {

    @ParameterizedTest
    @MethodSource
    public void test(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> test() {
        return Stream.of(
                arguments("if(true, 1, 0)", ifExp($boolVal(true), $val(1), $val(0))),
                arguments("if(false, 1, 0)", ifExp($boolVal(false), $val(1), $val(0))),
                arguments("if(false, 1, 0)", ifExp($boolVal(false), $val(1), $val(0))),
                arguments("if(bool(1), 1, 0)", ifExp($bool(1), $val(1), $val(0))),
                arguments("if(not bool(1), 1, 0)", ifExp($bool(1).not(), $val(1), $val(0))),
                arguments("if(bool(1) and bool(2), 1, 0)",
                        ifExp($bool(1).and($bool(2)), $val(1), $val(0))),
                arguments("if(castAsBool(1), 1, 0)",
                        ifExp($intVal(1).castAsBool(), $val(1), $val(0))),
                arguments("if(1 > 0, 'yes', 'no')",
                        ifExp($intVal(1).gt($val(0)), $val("yes"), $val("no")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IF(true, 1, 0)",
            "if(1, 1, 0)",
            "if(true, 1)",
            "if(true, 1, 0, 2)",
    })
    public void testThrows(String text) {
        assertThrows(QLParserException.class, () -> parseExp(text));
    }
}
