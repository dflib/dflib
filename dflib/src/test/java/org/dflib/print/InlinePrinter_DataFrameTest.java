package org.dflib.print;

import org.dflib.DataFrame;
import org.dflib.Series;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class InlinePrinter_DataFrameTest {

    @Test
    public void emptyColumn() {

        DataFrame df = DataFrame
                .byColumn("col1", "")
                .of(
                        Series.of("one", "two"),
                        Series.of("", ""));

        InlinePrinter p = new InlinePrinter(5, 10);

        assertEquals("{col1:one,:},{col1:two,:}", p.print(df));
    }
}
