package com.nhl.dflib.exp;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.DecimalExp;
import com.nhl.dflib.Series;
import com.nhl.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static com.nhl.dflib.Exp.*;
import static org.junit.jupiter.api.Assertions.assertSame;

public class DecimalExpTest {

    @Test
    public void testScale() {
        DataFrame df = DataFrame.newFrame("a").foldByRow(
                new BigDecimal("2.0100287"),
                new BigDecimal("4.5"));

        Series<BigDecimal> s = $decimal("a").scale(2).eval(df);
        new SeriesAsserts(s).expectData(new BigDecimal("2.01"), new BigDecimal("4.50"));
    }

    @Test
    public void testCastAsDecimal() {
        DecimalExp e = $decimal("a");
        assertSame(e, e.castAsDecimal());
    }
}
