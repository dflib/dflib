package org.dflib.series;

import org.dflib.unit.BoolSeriesAsserts;
import org.junit.jupiter.api.Test;

public class TrueSeries_HeadTest {

    @Test
    public void test() {
        new BoolSeriesAsserts(new TrueSeries(3).head(2)).expectData(true, true);
    }

    @Test
    public void zero() {
        new BoolSeriesAsserts(new TrueSeries(3).head(0)).expectData();
    }

    @Test
    public void outOfBounds() {
        new BoolSeriesAsserts(new TrueSeries(3).head(4)).expectData(true, true, true);
    }

    @Test
    public void negative() {
        new BoolSeriesAsserts(new TrueSeries(3).head(-2)).expectData(true);
    }
}
