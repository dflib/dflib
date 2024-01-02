package org.dflib;

import org.dflib.unit.SeriesAsserts;
import org.junit.jupiter.api.Test;

@Deprecated
public class DataFrame_MapColumnsTest {

    @Test
    public void mapColumn() {
        Series<Integer> mapped = DataFrame
                .foldByRow("a", "b")
                .of(
                        1, "x",
                        2, "y")
                .mapColumn(r -> ((int) r.get(0)) * 10);

        new SeriesAsserts(mapped).expectData(10, 20);
    }


}
