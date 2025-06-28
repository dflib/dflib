package org.dflib.exp.parser;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.ifNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class IfNullTest {

    @ParameterizedTest
    @MethodSource
    public void test(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> test() {
        return Stream.of(
                arguments("ifNull(int(1), 0)", ifNullVal($int(1), 0)),
                arguments("ifNull(str(1), 'unknown')", ifNull($str(1), $strVal("unknown")))
        );
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "IFNULL($int(1), 0)",
            "ifNull($int(1))",
            "ifNull($int(1), )",
            "ifNull(, $int(1))"
    })
    public void ifNull_throws(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

}
