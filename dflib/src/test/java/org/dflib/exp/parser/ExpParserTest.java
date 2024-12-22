package org.dflib.exp.parser;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.dflib.Exp;
import org.dflib.exp.parser.antlr4.ExpExtractor;
import org.dflib.exp.parser.antlr4.ExpLexer;
import org.dflib.exp.parser.antlr4.ExpParser;
import org.dflib.exp.parser.jj.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;

class ExpParserTest {

    @ParameterizedTest
    @MethodSource("expressions")
    void testJJ(String string) throws ParseException {
        var parser = new org.dflib.exp.parser.jj.ExpParser(string);
        Exp<?> exp = parser.root();

        assertNotNull(exp);
    }

    @ParameterizedTest
    @MethodSource("expressions")
    void testAntlr(String string) {
        ExpLexer lexer = new ExpLexer(CharStreams.fromString(string));
        ExpParser parser = new ExpParser(new CommonTokenStream(lexer));
        ExpExtractor extractor = new ExpExtractor();

        ExpParser.RootContext context = parser.root();
        Exp<?> exp = extractor.visit(context);

        assertNotNull(exp);
    }

    static Stream<String> expressions() {
        return Stream.of(
                "true",
                "false",
                "true or false",
                "true or false and not true",
                "'a'",
                "\"str\"",
                "42 + 12 * 3.0 > 23 % 12",
                "int(a) + (12 * int(b) / 2) > (long(c) - 12) * 23",
                "(bool(a) or bool(b)) and (str(c) != \"abc\")",
                "min(int(a), int(a) > 10)",
                "avg(long(a))",
                "max(str(b), len(str(b)) < 20)",
                "matches(str(b), \"abc\")",
                "split(str(1), \",\", 2)"
        );
    }

}
