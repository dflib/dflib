package org.dflib;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class Sorter_ParseSorterTest {

    @ParameterizedTest
    @MethodSource
    public void parseSorter(String spec, Sorter expected) {
        Sorter parsed = Sorter.parseSorter(spec);
        assertEquals(expected, parsed);
    }

    static Stream<Arguments> parseSorter() {
        return Stream.of(
                arguments("test", $col("test").asc()),
                arguments("test asc", $col("test").asc()),
                arguments("test desc", $col("test").desc()),
                arguments("asc", $col("asc").asc()),
                arguments("desc", $col("desc").asc()),
                arguments("asc asc", $col("asc").asc()),
                arguments("asc desc", $col("asc").desc()),
                arguments("int(1) > 0", $int(1).gt(0).asc()),
                arguments("int(1) > 0 asc", $int(1).gt(0).asc()),
                arguments("int(1) > 0 desc", $int(1).gt(0).desc())
        );
    }

    @ParameterizedTest
    @MethodSource
    public void parseSorterList(String spec, Sorter[] expected) {
        assertArrayEquals(expected, Sorter.parseSorterArray(spec));
    }

    static Stream<Arguments> parseSorterList() {
        return Stream.of(
                arguments("test", new Sorter[]{$col("test").asc()}),
                arguments("a, b, c", new Sorter[]{$col("a").asc(), $col("b").asc(), $col("c").asc()}),
                arguments("a, b desc, c asc", new Sorter[]{$col("a").asc(), $col("b").desc(), $col("c").asc()}),
                arguments("test asc", new Sorter[]{$col("test").asc()}),
                arguments("test desc", new Sorter[]{$col("test").desc()}),
                arguments("asc, desc desc, asc asc", new Sorter[]{$col("asc").asc(),  $col("desc").desc(), $col("asc").asc()}),
                arguments("a, b desc, int(1) > 0 asc", new Sorter[]{$col("a").asc(), $col("b").desc(), $int(1).gt(0).asc()})
        );
    }
}
