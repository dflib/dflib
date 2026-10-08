package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static org.dflib.ql.IdentityFunctions.identity;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ParseErrorReportingTest {

    @ParameterizedTest
    @MethodSource
    public void syntaxError(String text, String expectedMessagePrefix) {
        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp(text));
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

    // the grammar is untyped, so a type mismatch is not a syntax error, but an error reported by the operator
    @ParameterizedTest
    @MethodSource
    public void semanticError_OperandType(String text, String expectedMessage) {
        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp(text));
        assertEquals(expectedMessage, e.getMessage());
    }

    static Stream<Arguments> semanticError_OperandType() {
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
                        "Operator 'in' at 1:8 expects a list of string values for a date expression, got: 1")
        );
    }

    @Test
    public void semanticError_UnknownFunction() {
        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp("foo(1, 2)"));
        assertEquals("1:0 Unknown function: foo", e.getMessage());

        QLParserException nested = assertThrows(QLParserException.class, () -> Exp.parseExp("1 + foo(1, 2)"));
        assertEquals("1:4 Unknown function: foo", nested.getMessage());
    }

    @Test
    public void semanticError_NoMatchingOverload() {
        String substrOverloads = "Available: substr(OBJECT, const NUMERIC), substr(OBJECT, const NUMERIC, const NUMERIC)";

        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp("substr('example')"));
        assertEquals("1:0 No overload of substr matches substr(const STRING). " + substrOverloads, e.getMessage());

        QLParserException nested = assertThrows(QLParserException.class, () -> Exp.parseExp("len(substr('a'))"));
        assertEquals("1:4 No overload of substr matches substr(const STRING). " + substrOverloads, nested.getMessage());

        QLParserException nonConst = assertThrows(QLParserException.class, () -> Exp.parseExp("substr(a, int(b))"));
        assertEquals("1:0 No overload of substr matches substr(OBJECT, NUMERIC<Integer>). " + substrOverloads,
                nonConst.getMessage());
    }

    @Test
    public void semanticError_Producer() {
        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp("year(str(a))"));
        assertEquals("1:0 No overload of year matches year(STRING)."
                + " Available: year(DATE), year(DATETIME), year(OFFSETDATETIME)", e.getMessage());

        QLParserException scale = assertThrows(QLParserException.class, () -> Exp.parseExp("scale(int(a), 1.5)"));
        assertEquals("1:0 Not an integer constant: 1.5", scale.getMessage());
    }

    @Test
    public void semanticError_WrongReturnType() {
        QLFunctions original = Environment.commonEnv().qlFunctions();
        try {
            Environment.setQLFunctions(identity(QLFunctions.builder(), "f").build());

            QLParserException num = assertThrows(QLParserException.class, () -> Exp.parseExp("f(str(a)) + 1"));
            assertEquals("Operator '+' at 1:10 expects a numeric operand, got a string one: a", num.getMessage());

            QLParserException date = assertThrows(QLParserException.class, () -> Exp.parseExp("date(a) > f(str(b))"));
            assertEquals("Operator '>' at 1:8 expects a date operand, got a string one: b", date.getMessage());
        } finally {
            Environment.setQLFunctions(original);
        }
    }

    @Test
    public void parameters_Order() {
        assertEquals(Exp.$int("a").gt(Exp.$intVal(1)).and(Exp.$str("b").eq(Exp.$strVal("x"))),
                Exp.parseExp("int(a) > ? and str(b) = ?", 1, "x"));
    }
}
