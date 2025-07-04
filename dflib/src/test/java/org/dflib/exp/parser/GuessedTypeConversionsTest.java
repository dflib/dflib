package org.dflib.exp.parser;

import org.dflib.Exp;
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

public class GuessedTypeConversionsTest {

    @ParameterizedTest
    @MethodSource
    void guessedConversions(String text, Exp<?> expected) {
        Exp<?> exp = parseExp(text);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> guessedConversions() {
        return Stream.of(
                arguments("int(a) + 2", $int("a").add(2)),
                arguments("int(a) + 2l", $int("a").add(2L)),

                arguments("a = '2001-01-02'", $col("a").eq("2001-01-02")),
                // TODO: we should be able to flip the arguments
//                arguments("'2001-01-02' = a", $strVal("2001-01-02").eq($col("a"))),

                arguments("str(a) = '2001-01-02'", $str("a").eq("2001-01-02")),
                arguments("'2001-01-02' = str(a)", $strVal("2001-01-02").eq($str("a"))),

                arguments("date(a) > '2001-01-02'", $date("a").gt(LocalDate.of(2001, 1, 2))),
                // TODO: we should be able to flip the arguments
//                arguments("date('2001-01-02') < date(a)", $dateVal(LocalDate.of(2001, 1, 2)).lt($date("a"))),

                arguments("dateTime(a) > '2001-01-02T00:01:30'", $dateTime("a").gt(LocalDateTime.of(2001, 1, 2, 0, 1, 30)))
        );
    }

    @ParameterizedTest
    @MethodSource
    void unrecognizedConversions(String text) {
        assertThrows(ExpParserException.class, () -> parseExp(text));
    }

    // TODO: the eventual goal is to become smart about these.. Maybe recognize generic NumExp based on the constant.
    //  In other cases, use a concept of DataFrame "schema" (late resolution)
    static Stream<Arguments> unrecognizedConversions() {
        return Stream.of(
                arguments("a + 2l"),
                arguments("2 + a"),
                arguments("'2001-01-02' < date(a)")
        );
    }
}
