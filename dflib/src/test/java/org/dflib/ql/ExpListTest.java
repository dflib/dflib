package org.dflib.ql;

import org.dflib.Exp;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ExpListTest {

    @ParameterizedTest
    @MethodSource
    public void list(String exp, Exp<?>[] result) {
        assertArrayEquals(result, parseExpArray(exp));
    }

    static Stream<Arguments> list() {
        return Stream.of(
                arguments("a", new Exp<?>[]{$col("a")}),
                arguments("a, b", new Exp<?>[]{$col("a"), $col("b")}),
                arguments("a, b, int(c) * 5 as c", new Exp<?>[]{$col("a"), $col("b"), $int("c").mul(5).as("c")})
        );
    }

}
