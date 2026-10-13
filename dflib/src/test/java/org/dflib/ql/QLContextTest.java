package org.dflib.ql;

import org.dflib.DataFrame;
import org.dflib.Environment;
import org.dflib.Exp;
import org.dflib.Udf1;
import org.dflib.Udf2;
import org.dflib.unit.DataFrameAsserts;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.dflib.Exp.$col;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.concat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class QLContextTest extends BaseQLFunctionsTest {

    static final Udf1<Object, String> SHOUT = Udf1.of(e -> e.castAsStr().upper());
    static final Udf2<Object, Object, String> PAIR = Udf2.of((a, b) -> concat(a, "-", b));

    @Test
    public void builder_IncludesBuiltins() {
        QLContext context = QLContext.builder().function("shout", SHOUT).build();

        assertEquals($int("a").abs(), context.parseExp("abs(int(a))"));
        assertEquals($col("a").castAsStr().upper(), context.parseExp("shout(a)"));
    }

    @Test
    public void builder_NoDefaultFunctions() {
        QLContext context = QLContext.builder().noDefaultFunctions().function("shout", SHOUT).build();

        assertEquals($col("a").castAsStr().upper(), context.parseExp("shout(a)"));

        QLParserException e = assertThrows(QLParserException.class, () -> context.parseExp("abs(int(a))"));
        assertEquals("Function 'abs' at 1:0: Unknown function: abs", e.getMessage());
    }

    @Test
    public void builder_SameShapeAsBuiltin_Overrides() {
        QLContext context = QLContext.builder().function("trim", SHOUT).build();
        assertEquals($col("a").castAsStr().upper(), context.parseExp("trim(a)"));
    }

    @Test
    public void builder_DuplicateInLayer() {
        QLContext.Builder builder = QLContext.builder().function("shout", SHOUT);

        IllegalArgumentException e = assertThrows(IllegalArgumentException.class, () -> builder.function("shout", SHOUT));
        assertEquals("Function shout(OBJECT) already defined", e.getMessage());
    }

    @Test
    public void builder_NotAffectingCommonContext() {
        QLContext context = QLContext.builder().function("shout", SHOUT).build();
        assertEquals($col("a").castAsStr().upper(), context.parseExp("shout(a)"));

        QLParserException e = assertThrows(QLParserException.class, () -> Exp.parseExp("shout(a)"));
        assertEquals("Function 'shout' at 1:0: Unknown function: shout", e.getMessage());
    }

    @Test
    public void extend_KeepsParentFunctions() {
        QLContext parent = QLContext.builder().function("shout", SHOUT).build();
        QLContext context = parent.extend().function("pair", PAIR).build();

        assertEquals($int("a").abs(), context.parseExp("abs(int(a))"));
        assertEquals($col("a").castAsStr().upper(), context.parseExp("shout(a)"));
        assertEquals(concat($col("a"), "-", $col("b")), context.parseExp("pair(a, b)"));

        // the parent is not changed
        assertThrows(QLParserException.class, () -> parent.parseExp("pair(a, b)"));
    }

    @Test
    public void extend_SameShape_Overrides() {
        QLContext parent = QLContext.builder().function("f", SHOUT).build();
        QLContext context = parent.extend().function("f", Udf1.of(e -> e.castAsStr().lower())).build();

        assertEquals($col("a").castAsStr().upper(), parent.parseExp("f(a)"));
        assertEquals($col("a").castAsStr().lower(), context.parseExp("f(a)"));
    }

    @Test
    public void extend_Tie_OuterWins() {

        // "vConcat(str(a), ',')" matches the built-in "vConcat(Exp<?>, const String)" and the custom
        // "vConcat(StrExp, const Object)" with one wildcard each, and the built-in is in the outer layer
        QLContext context = Environment.commonEnv().qlContext().extend()
                .function("vConcat", new CustomFunctionTest.StrVConcatFunction())
                .build();

        assertEquals($str("a").vConcat(null, ",", "", ""), context.parseExp("vConcat(str(a), ',')"));
    }

    @Test
    public void extend_NoDefaultFunctions() {
        QLContext.Builder builder = QLContext.builder().build().extend();
        assertThrows(IllegalStateException.class, builder::noDefaultFunctions);
    }

    @Test
    public void extend_SnapshotOfCommonContext() {
        Environment.setQLContext(QLContext.builder().function("shout", SHOUT).build());
        QLContext context = Environment.commonEnv().qlContext().extend().function("pair", PAIR).build();

        Environment.setQLContext(null);

        assertEquals($col("a").castAsStr().upper(), context.parseExp("shout(a)"));
        assertThrows(QLParserException.class, () -> Exp.parseExp("shout(a)"));
    }

    @Test
    public void environment_SetQLContext() {
        QLContext context = QLContext.builder().function("shout", SHOUT).build();
        Environment.setQLContext(context);

        assertSame(context, Environment.commonEnv().qlContext());
        assertEquals($col("a").castAsStr().upper(), Exp.parseExp("shout(a)"));
        assertEquals(List.of($col("a").castAsStr().upper()), List.of(Exp.parseExps("shout(a)")));
    }

    @Test
    public void parseExp_Params() {
        QLContext context = QLContext.builder().function("pair", PAIR).build();

        assertEquals(concat($col("a"), "-", Exp.$val("x")), context.parseExp("pair(a, ?)", "x"));
        assertEquals(concat($col("a"), "-", $col("b")).castAsStr().upper(),
                context.parseExp("?f(pair(a, b))", SHOUT));
    }

    @Test
    public void parseExps() {
        QLContext context = QLContext.builder().function("pair", PAIR).build();

        Exp<?>[] exps = context.parseExps("pair(a, b) as p, abs(int(c))");
        assertEquals(List.of(concat($col("a"), "-", $col("b")).as("p"), $int("c").abs()), List.of(exps));
    }

    @Test
    public void parseCondition() {
        QLContext context = QLContext.builder().function("shout", SHOUT).build();

        assertEquals($col("a").castAsStr().upper().eq("X"), context.parseCondition("shout(a) = 'X'"));
        assertEquals($col("a").castAsStr().upper().castAsBool(), context.parseCondition("shout(a)"));
    }

    @Test
    public void parseError() {
        QLContext context = QLContext.builder().build();

        QLParserException e = assertThrows(QLParserException.class, () -> context.parseExp("abs("));
        assertTrue(e.getMessage().startsWith("line 1:4 mismatched input '<EOF>'"), e.getMessage());
    }

    @Test
    public void dataFrame() {
        QLContext context = QLContext.builder()
                .function("shout", SHOUT)
                .function("pair", PAIR)
                .build();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "x", 2,
                "y", 1,
                "z", 3);

        DataFrame selected = df.cols("p", "s").select(context.parseExps("pair(a, b), shout(a)"));
        new DataFrameAsserts(selected, "p", "s")
                .expectHeight(3)
                .expectRow(0, "x-2", "X")
                .expectRow(1, "y-1", "Y")
                .expectRow(2, "z-3", "Z");

        DataFrame filtered = df.rows(context.parseCondition("shout(a) != ?", "Y")).select();
        new DataFrameAsserts(filtered, "a", "b")
                .expectHeight(2)
                .expectRow(0, "x", 2)
                .expectRow(1, "z", 3);

        DataFrame sorted = df.sort(context.parseSorters("pair(b, a) desc"));
        new DataFrameAsserts(sorted, "a", "b")
                .expectHeight(3)
                .expectRow(0, "z", 3)
                .expectRow(1, "x", 2)
                .expectRow(2, "y", 1);

        DataFrame sortedOne = df.sort(context.parseSorter("shout(a) asc"));
        new DataFrameAsserts(sortedOne, "a", "b")
                .expectHeight(3)
                .expectRow(0, "x", 2)
                .expectRow(1, "y", 1)
                .expectRow(2, "z", 3);
    }
}
