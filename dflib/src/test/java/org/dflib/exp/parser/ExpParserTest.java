package org.dflib.exp.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dflib.Exp;
import org.dflib.exp.bool.BoolScalarExp;
import org.dflib.exp.parser.antlr4.ExpExtractor;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpParser;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExpParserTest {

    @ParameterizedTest
    @MethodSource("expressions")
    void testJJ(String string, Exp<?> expected) throws ParseException {
        var parser = new org.dflib.exp.parser.ExpParser(string);
        Exp<?> exp = parser.root();

        assertEquals(expected, exp);
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void testAntlr(String string, Exp<?> expected) {
        ExpLexer lexer = new ExpLexer(CharStreams.fromString(string));
        ExpParser parser = new ExpParser(new CommonTokenStream(lexer));
        ExpExtractor extractor = new ExpExtractor();

        ExpParser.RootContext context = parser.root();
        Exp<?> exp = extractor.visit(context);

        assertEquals(expected, exp);
    }

    static Stream<Arguments> expressions() {
        return Stream.of(
                Arguments.of(
                        "-12",
                        Exp.$val(-12)
                ),
                Arguments.of(
                        "-.1",
                        Exp.$val(-0.1)
                ),
                Arguments.of(
                        "true",
                        Exp.$val(true)
                ),
                Arguments.of(
                        "false",
                        Exp.$val(false)
                ),
                Arguments.of(
                        "true or false",
                        Exp.or(BoolScalarExp.TRUE, BoolScalarExp.FALSE)
                ),
                Arguments.of(
                        "true or false and not true",
                        Exp.or(BoolScalarExp.TRUE, Exp.and(BoolScalarExp.FALSE, Exp.not(BoolScalarExp.TRUE)))
                ),
                Arguments.of(
                        "'a'",
                        Exp.$val("a")
                ),
                Arguments.of(
                        "\"str\"",
                        Exp.$val("str")
                ),
                Arguments.of(
                        "42 + 12 * 3.0 > 23 % 12",
                        Exp.$intVal(42).add(Exp.$intVal(12).mul(Exp.$val(3.0)))
                                .gt(Exp.$intVal(23).mod(Exp.$val(12)))
                ),
                Arguments.of(
                        "int(a) + (12 * int(b) / 2) > (long(c) - 12) * 23",
                        Exp.$int("a").add(Exp.$intVal(12).mul(Exp.$int("b").div(Exp.$val(2))))
                                .gt(Exp.$long("c").sub(Exp.$val(12)).mul(Exp.$val(23)))
                ),
                Arguments.of(
                        "(bool(a) or bool(b)) and (str(c) != \"abc\")",
                        Exp.$bool("a").or(Exp.$bool("b")).and(Exp.$str("c").ne(Exp.$val("abc")))
                ),
                Arguments.of(
                        "min(int(a), int(a) > 10)",
                        Exp.$int("a").min(Exp.$int("a").gt(Exp.$val(10)))
                ),
                Arguments.of(
                        "avg(long(a))",
                        Exp.$long("a").avg()
                ),
                Arguments.of(
                        "max(str(b), len(str(b)) < 20)",
                        Exp.$str("b").max(Exp.$str("b").mapVal(String::length).castAsInt().lt(Exp.$val(20)))
                ),
                Arguments.of(
                        "matches(str(b), \"abc\")",
                        Exp.$str("b").matches("abc")
                ),
                Arguments.of(
                        "split(str(1), \",\", 2)",
                        Exp.$str(1).split(",", 2)
                )
        );
    }

}
