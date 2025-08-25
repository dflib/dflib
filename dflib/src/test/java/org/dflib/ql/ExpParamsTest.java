package org.dflib.ql;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.time.*;
import java.util.List;
import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExpParamsTest {

    @ParameterizedTest
    @MethodSource
    void parameterizedExpression(String text, Object[] args, Exp<?> expected) {
        Exp<?> exp = parseExp(text, args);
        assertEquals(expected, exp);
    }

    static Stream<Arguments> parameterizedExpression() {
        return Stream.of(
                arguments(
                        "?",
                        new Object[]{true},
                        $boolVal(true)
                ),

                arguments(
                        "int(1) > ?",
                        new Object[]{1L},
                        $int(1).gt($longVal(1L))
                ),
                arguments(
                        "str(b) != ?",
                        new Object[]{"abc"},
                        $str("b").ne($strVal("abc"))
                ),
                arguments(
                        "bool(c) = ?",
                        new Object[]{true},
                        $bool("c").eq($boolVal(true))
                ),

                arguments(
                        "date(c) = ?",
                        new Object[]{LocalDate.of(1905, 4, 3)},
                        $date("c").eq($dateVal(LocalDate.of(1905, 4, 3)))
                ),
                arguments(
                        "time(c) = ?",
                        new Object[]{LocalTime.of(23, 4, 3)},
                        $time("c").eq($timeVal(LocalTime.of(23, 4, 3)))
                ),
                arguments(
                        "dateTime(c) = ?",
                        new Object[]{LocalDateTime.of(
                                LocalDate.of(1905, 4, 3),
                                LocalTime.of(23, 4, 3)
                        )},
                        $dateTime("c").eq($dateTimeVal(LocalDateTime.of(
                                LocalDate.of(1905, 4, 3),
                                LocalTime.of(23, 4, 3))
                        ))
                ),
                arguments(
                        "offsetDateTime(c) = ?",
                        new Object[]{OffsetDateTime.of(
                                LocalDate.of(1905, 4, 3),
                                LocalTime.of(23, 4, 3),
                                ZoneOffset.ofHours(1)
                        )},
                        $offsetDateTime("c").eq($offsetDateTimeVal(OffsetDateTime.of(
                                LocalDate.of(1905, 4, 3),
                                LocalTime.of(23, 4, 3),
                                ZoneOffset.ofHours(1))
                        ))
                ),

                arguments(
                        "int(1) in ?",
                        new Object[]{List.of(1, 2, 3)},
                        $int(1).in(1, 2, 3)
                ),
                arguments(
                        "int(1) in (1, ?, 3)",
                        new Object[]{2},
                        $int(1).in(1, 2, 3)
                ),
                arguments(
                        "int(1) in (?, ?, ?)",
                        new Object[]{1, 2, 3},
                        $int(1).in(1, 2, 3)
                ),
                arguments(
                        "str(1) in ?",
                        new Object[]{List.of("a", "b", "c")},
                        $str(1).in("a", "b", "c")
                ),
                arguments(
                        "str(1) in (?, 'b', 'c')",
                        new Object[]{"a"},
                        $str(1).in("a", "b", "c")
                ),
                arguments(
                        "a in ?",
                        new Object[]{List.of("a", 1, .4)},
                        $col("a").in("a", 1, .4)
                ),
                arguments(
                        "a in ('a', 1, ?)",
                        new Object[]{0.4},
                        $col("a").in("a", 1, .4)
                ),

                arguments(
                        "(int(1) > ? and str(b) != ?) or (bool(c) != ?)",
                        new Object[]{1L, "abc", true},
                        $int(1).gt($longVal(1L))
                                .and($str("b").ne($strVal("abc")))
                                .or($bool("c").ne($boolVal(true)))
                )
        );
    }
}
