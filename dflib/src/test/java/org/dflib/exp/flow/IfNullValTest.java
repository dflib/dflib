package org.dflib.exp.flow;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.exp.BaseExpTest;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.dflib.Exp.$int;
import static org.dflib.Exp.ifNullVal;

public class IfNullValTest extends BaseExpTest {

    @Test
    public void val() {
        Exp<?> noNulls = ifNullVal($int("a"), 77);

        DataFrame df = DataFrame.foldByRow("a").of(
                1,
                null,
                8,
                null);

        new SeriesAsserts(noNulls.eval(df)).expectData(1, 77, 8, 77);
    }
}
