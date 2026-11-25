package org.dflib.ql;

import org.dflib.Exp;
import org.dflib.exp.flow.IfExp;
import org.dflib.exp.flow.IfNullExp;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.OffsetDateTime;

import static org.dflib.Exp.$bool;
import static org.dflib.Exp.$col;
import static org.dflib.Exp.$date;
import static org.dflib.Exp.$dateTime;
import static org.dflib.Exp.$int;
import static org.dflib.Exp.$intVal;
import static org.dflib.Exp.$offsetDateTime;
import static org.dflib.Exp.$str;
import static org.dflib.Exp.$time;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class TypeClassifierTest {

    @Test
    public void classify_Exp() {
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify($int("a")));
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify($intVal(1)));
        assertEquals(TypeClassifier.STRING, TypeClassifier.classify($str("a")));
        assertEquals(TypeClassifier.BOOLEAN, TypeClassifier.classify($bool("a")));
        assertEquals(TypeClassifier.DATE, TypeClassifier.classify($date("a")));
        assertEquals(TypeClassifier.TIME, TypeClassifier.classify($time("a")));
        assertEquals(TypeClassifier.DATETIME, TypeClassifier.classify($dateTime("a")));
        assertEquals(TypeClassifier.OFFSETDATETIME, TypeClassifier.classify($offsetDateTime("a")));
    }

    @Test
    public void classify_UntypedExp() {

        // whatever the value type of the expression
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($date("a").first()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($int("a").last()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(new IfNullExp<>($int("a"), $int("b"))));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(new IfExp<>($bool("c"), $int("a"), $int("b"))));

        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($col("a")));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($str("a").split(',')));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify($col("a").list()));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(Exp.$val(new Object())));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(Exp.$val(null)));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify((Exp<?>) null));
    }

    @Test
    public void classify_Class() {
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify(Integer.class));
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify(BigDecimal.class));
        assertEquals(TypeClassifier.NUMERIC, TypeClassifier.classify(Number.class));
        assertEquals(TypeClassifier.STRING, TypeClassifier.classify(String.class));
        assertEquals(TypeClassifier.BOOLEAN, TypeClassifier.classify(Boolean.class));
        assertEquals(TypeClassifier.DATE, TypeClassifier.classify(LocalDate.class));
        assertEquals(TypeClassifier.TIME, TypeClassifier.classify(LocalTime.class));
        assertEquals(TypeClassifier.DATETIME, TypeClassifier.classify(LocalDateTime.class));
        assertEquals(TypeClassifier.OFFSETDATETIME, TypeClassifier.classify(OffsetDateTime.class));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(Object.class));
        assertEquals(TypeClassifier.OBJECT, TypeClassifier.classify(String[].class));
    }

    @Test
    public void cast() {
        assertEquals($col("a").castAsNumber(), TypeClassifier.NUMERIC.cast($col("a")));
        assertEquals($col("a").castAsStr(), TypeClassifier.STRING.cast($col("a")));
        assertEquals($col("a").castAsBool(), TypeClassifier.BOOLEAN.cast($col("a")));
        assertEquals($col("a").castAsDate(), TypeClassifier.DATE.cast($col("a")));
        assertEquals($col("a").castAsTime(), TypeClassifier.TIME.cast($col("a")));
        assertEquals($col("a").castAsDateTime(), TypeClassifier.DATETIME.cast($col("a")));
        assertEquals($col("a").castAsOffsetDateTime(), TypeClassifier.OFFSETDATETIME.cast($col("a")));
        assertEquals($col("a"), TypeClassifier.OBJECT.cast($col("a")));
    }
}
