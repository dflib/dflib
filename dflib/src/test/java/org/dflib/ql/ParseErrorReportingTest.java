package org.dflib.ql;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.Exp.parseExp;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ParseErrorReportingTest {

    @ParameterizedTest
    @MethodSource
    public void syntaxError(String text, String expectedMessagePrefix) {
        QLParserException e = assertThrows(QLParserException.class, () -> parseExp(text));
        assertTrue(e.getMessage().startsWith(expectedMessagePrefix), e.getMessage());
    }

    // the "expecting" token lists that follow are left out, as they change with every keyword added to the grammar
    static Stream<Arguments> syntaxError() {
        return Stream.of(
                arguments("str(1) in ()", "line 1:11 mismatched input ')' expecting"),
                arguments("split(, ',')", "line 1:6 extraneous input ',' expecting"),
                arguments("int(1) +", "line 1:8 mismatched input '<EOF>' expecting"),

                // ordering comparisons, "between" and "in" do not chain
                arguments("1 < 2 < 3", "line 1:6 mismatched input '<' expecting"),
                arguments("int(a) > 1 in (true)", "line 1:11 mismatched input 'in' expecting")
        );
    }

    @ParameterizedTest
    @MethodSource
    public void semanticError(String text, String expectedMessage) {
        QLParserException e = assertThrows(QLParserException.class, () -> parseExp(text));
        assertEquals(expectedMessage, e.getMessage());
    }

    // the grammar is untyped, so a type mismatch is reported by the operator or function, not the parser
    static Stream<Arguments> semanticError() {
        return Stream.of(
                arguments("'hello' > 'world'", "Operator '>' at 1:8 is not supported for a string expression: 'hello'"),
                arguments("'hello' > 1", "Operator '>' at 1:8 is not supported for a string expression: 'hello'"),
                arguments("bool(a) in (true)", "Operator 'in' at 1:8 is not supported for a boolean expression: a"),
                arguments("int(1) = null", "Operator '=' at 1:7 expects a numeric operand, got a generic one: null"),
                arguments("str(a) + 1", "Operator '+' at 1:7 expects a numeric operand, got a string one: a"),
                arguments("int(a) and int(b)", "Operator 'and' at 1:7 expects a boolean operand, got a numeric one: a"),
                arguments("not int(a)", "Operator 'not' at 1:0 expects a boolean operand, got a numeric one: a"),
                arguments("bool(a) = 1", "Operator '=' at 1:8 expects a boolean operand, got a numeric one: 1"),
                arguments("date(a) > 5", "Operator '>' at 1:8 expects a date operand, got a numeric one: 5"),
                arguments("a between 1 and 5", "Operator 'between' at 1:2 is not supported for a generic expression: a"),
                arguments("str(1) in (1, 2)",
                        "Operator 'in' at 1:7 expects a list of string values for a string expression, got: 1"),
                arguments("int(1) not in ('a')",
                        "Operator 'in' at 1:11 expects a list of numeric values for a numeric expression, got: a"),
                arguments("date(1) in (1)",
                        "Operator 'in' at 1:8 expects a list of date values or ISO-8601 strings for a date expression, got: 1"),

                arguments("foo(1, 2)", "Function 'foo' at 1:0: Unknown function: foo"),
                arguments("1 + foo(1, 2)", "Function 'foo' at 1:4: Unknown function: foo"),
                arguments("substr('example')", "Function 'substr' at 1:0: No overload of substr matches substr(STRING)."
                        + " Available: substr(OBJECT, NUMERIC), substr(OBJECT, NUMERIC, NUMERIC)"),
                arguments("substr(a, int(b))", "Function 'substr' at 1:0: Not a constant: b"),
                // an untyped argument is cast before the function sees it
                arguments("plusDays(date(d), b)", "Function 'plusDays' at 1:0: Not a constant: castAsNumber(b)"),
                arguments("scale(int(a), 1.5)", "Function 'scale' at 1:0: Not an integer constant: 1.5"),
                arguments("substr(str(a), 3000000000)", "Function 'substr' at 1:0: Integer constant out of range"
                        + " [-2147483648..2147483647]: 3000000000"),
                arguments("year(str(a))", "Function 'year' at 1:0: year(): argument 1 of type STRING is not one of"
                        + " DATE, DATETIME, OFFSETDATETIME"),
                arguments("avg(str(a))", "Function 'avg' at 1:0: avg(): argument 1 of type STRING is not one of"
                        + " NUMERIC, DATE, TIME, DATETIME"),
                arguments("year(a)", "Function 'year' at 1:0: year(): argument 1 is untyped, cast it explicitly to"
                        + " one of DATE, DATETIME, OFFSETDATETIME"),
                arguments("min(a)", "Function 'min' at 1:0: min(): argument 1 is untyped, cast it explicitly to"
                        + " one of NUMERIC, STRING, DATE, TIME, DATETIME"),
                arguments("vConcat(str(a))", "Function 'vConcat' at 1:0: vConcat() takes"
                        + " (exp, [filter,] delimiter[, prefix, suffix]) arguments"),
                arguments("vConcat(str(a), int(b), ',', '[', ']')",
                        "Function 'vConcat' at 1:0: vConcat() filter is not a condition: b"),
                arguments("shift(int(1), 2, 'replace')", "Function 'shift' at 1:0: shift() filler of type STRING"
                        + " is not compatible with a NUMERIC expression: `int(1)`"),
                arguments("shift(str(a), 1, 1)", "Function 'shift' at 1:0: shift() filler of type NUMERIC"
                        + " is not compatible with a STRING expression: a"),
                arguments("shift(int(a), 1, 1.5)", "Function 'shift' at 1:0: shift() filler 1.5 can not be converted"
                        + " to the Integer type of the expression: a"),
                arguments("shift(int(a), 1, 3000000000)", "Function 'shift' at 1:0: shift() filler 3000000000"
                        + " can not be converted to the Integer type of the expression: a")
        );
    }
}
