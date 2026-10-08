package org.dflib.ql;

import org.dflib.Environment;
import org.dflib.Exp;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$dateTime;
import static org.dflib.Exp.$dateTimeVal;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$strVal;
import static org.dflib.Exp.parseExp;
import static org.dflib.ql.DescriptorBuilder.descriptor;
import static org.dflib.ql.IdentityFunctions.identity;
import static org.dflib.ql.TypeClassifier.NUMERIC;
import static org.dflib.ql.TypeClassifier.OBJECT;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class PolymorphicDispatchTest extends BaseQLFunctionsTest {

    @BeforeEach
    public void setUpFunctions() {
        QLFunctions.Builder builder = QLFunctions.builder();
        identity(builder, "pmin");
        identity(builder, "plusLike", new QLFunctionArg(NUMERIC, true));

        Environment.setQLFunctions(builder
                .function(descriptor("foo").arg(OBJECT).as(args -> args.get(0).castAsInt()))
                .function(descriptor("anyLike").arg(OBJECT).as(args -> args.get(0).first()))
                .build());
    }

    @Test
    public void polymorphic_Bare() {
        assertEquals($str("a"), parseExp("pmin(str(a))"));
        assertEquals($int("a"), parseExp("pmin(int(a))"));
        assertEquals($col("a").first(), parseExp("pmin(anyLike(a))"));
        assertEquals($col("a").first(), parseExp("anyLike(a)"));
    }

    @Test
    public void polymorphic_Operator() {
        assertEquals($int("a").add($intVal(1)), parseExp("pmin(int(a)) + 1"));
        assertEquals($int("a").mul($intVal(2)), parseExp("pmin(int(a)) * 2"));
        assertEquals($bool("a").and($bool("b")), parseExp("pmin(bool(a)) and bool(b)"));
    }

    @Test
    public void polymorphic_PrefixOperator() {
        assertEquals(Exp.not($bool("a")), parseExp("not pmin(bool(a))"));
        assertEquals(Exp.not($bool("a")), parseExp("not (pmin(bool(a)))"));
        assertEquals($int("a").negate(), parseExp("- pmin(int(a))"));
        assertEquals($intVal(1).sub($int("a")), parseExp("1 - pmin(int(a))"));
    }

    @Test
    public void polymorphic_Parenthesized() {
        assertEquals($int("a").add($intVal(1)), parseExp("(pmin(int(a))) + 1"));
        assertEquals($intVal(1).add($int("a").mul($intVal(2))), parseExp("1 + (pmin(int(a))) * 2"));
        assertEquals($int("a").gt($intVal(5)), parseExp("(pmin(int(a))) > 5"));
    }

    @Test
    public void polymorphic_Comparison() {
        assertEquals($int("a").gt($intVal(5)), parseExp("pmin(int(a)) > 5"));
        assertEquals($int("a").le($intVal(5)), parseExp("plusLike(int(a), 1) <= 5"));
        assertEquals($int("a").ne($intVal(5)), parseExp("plusLike(int(a), 1) != 5"));
        assertEquals($str("a").eq($strVal("x")), parseExp("pmin(str(a)) = 'x'"));
        assertEquals($date("a").gt("2024-01-01"), parseExp("pmin(date(a)) > '2024-01-01'"));
        assertEquals($col("x").eq($col("y")), parseExp("pmin(x) = pmin(y)"));
    }

    @Test
    public void polymorphic_ComparisonParam() {
        LocalDateTime dt = LocalDateTime.of(2024, 1, 2, 3, 4, 5);
        assertEquals($dateTime("a").gt($dateTimeVal(dt)), parseExp("plusLike(dateTime(a), 1) > ?", dt));
    }

    @Test
    public void polymorphic_Between() {
        assertEquals($int("a").between($intVal(1), $intVal(5)), parseExp("plusLike(int(a), 1) between 1 and 5"));
        assertEquals($int("a").notBetween($intVal(1), $intVal(5)),
                parseExp("plusLike(int(a), 1) not between 1 and 5"));
        assertEquals($date("a").between("2024-01-01", "2024-12-31"),
                parseExp("pmin(date(a)) between '2024-01-01' and '2024-12-31'"));
    }

    @Test
    public void polymorphic_In() {
        assertEquals($str("a").in("x", "y"), parseExp("pmin(str(a)) in ('x', 'y')"));
        assertEquals($int("a").notIn(1, 2), parseExp("plusLike(int(a), 1) not in (1, 2)"));
        assertEquals($date("a").in(LocalDate.parse("2024-01-01")), parseExp("pmin(date(a)) in ('2024-01-01')"));
    }

    @Test
    public void polymorphic_OperatorThenComparison() {
        assertEquals($int("a").add($intVal(1)).gt($intVal(5)), parseExp("pmin(int(a)) + 1 > 5"));
    }

    @Test
    public void polymorphic_ComparisonRight() {
        assertEquals($intVal(5).gt($int("a")), parseExp("5 > pmin(int(a))"));
        assertEquals($int("b").le($int("a")), parseExp("int(b) <= pmin(int(a))"));
    }

    @Test
    public void polymorphic_TypedCallArg() {
        assertEquals($date("a").year(), parseExp("year(pmin(date(a)))"));
    }

    @Test
    public void polymorphic_Argument() {
        assertEquals($col("x").castAsInt().add($intVal(1)), parseExp("foo(pmin(x)) + 1"));
        assertEquals($int("a"), parseExp("pmin(plusLike(int(a), 1))"));
        assertEquals($date("a"), parseExp("pmin(plusLike(date(a), 1))"));
    }
}
