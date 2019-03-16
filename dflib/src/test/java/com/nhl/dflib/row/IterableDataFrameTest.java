package com.nhl.dflib.row;

import com.nhl.dflib.DFAsserts;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.junit.Test;

import java.util.List;

import static java.util.Arrays.asList;

public class IterableDataFrameTest {

    @Test
    public void testFromObjects() {

        class Bean {
            int a;
            int b;

            Bean(int a, int b) {
                this.a = a;
                this.b = b;
            }
        }

        List<Bean> beans = asList(new Bean(5, 4), new Bean(3, 1));

        Index i = Index.withNames("a", "b");
        DataFrame df = IterableRowDataFrame.fromObjects(i, beans, b -> DataFrame.row(b.a, b.b));

        new DFAsserts(df, i)
                .expectHeight(2)
                .expectRow(0, 5, 4)
                .expectRow(1, 3, 1);
    }

}
