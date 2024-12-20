package org.dflib.exp.parser;

import org.dflib.Exp;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ExpParserTest {

    @Test
    void testParser() throws ParseException {
        List<String> expressions = List.of(
                "true",
                "false",
                "true or false",
                "true or false and not true",
                "42 + 12 * 3.0 > 23 % 12",
                "int(a) + (12 * int(b) / 2) > (long(c) - 12) * 23",
                "(bool(a) or bool(b)) and (str(c) != \"abc\")",
                "min(int(a), int(a) > 10)",
                "avg(long(a))",
                "max(str(b), len(str(b)) < 20)",
                "matches(str(b), \"abc\")"
        );

        for(String exp : expressions) {
            ExpParser parser = new ExpParser(exp);
            Exp<?> expression = parser.root();
            assertNotNull(expression);
        }

    }

}