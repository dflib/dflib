package org.dflib.exp;

import org.dflib.DataFrame;
import org.dflib.Exp;
import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RowNumExpTest {

    @Test
    public void eval() {

        Exp<Integer> exp = Exp.rowNum();

        DataFrame df = DataFrame.foldByRow("a", "b").of(
                "A", 2,
                "B", 4);

        new SeriesAsserts(exp.eval(df)).expectData(1, 2);
    }

    @Test
    public void getColumnName() {
        assertEquals("rowNum()", Exp.rowNum().getColumnName());
    }

    @Test
    public void as() {
        assertEquals("NUMBER", Exp.rowNum().as("NUMBER").getColumnName());
    }
}
