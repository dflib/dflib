package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;

public class DataGenerator {

    public static DataFrame dfWithMixedData(int rows) {

        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        return df(rows,
                ValueMaker.intSeq(),
                ValueMaker.stringSeq(),
                ValueMaker.randomIntSeq(rows / 2),
                ValueMaker.constStringSeq(string));
    }



    public static DataFrame df(int rows, ValueMaker<?>... columnValueMakers) {

        int w = columnValueMakers.length;
        String[] columnNames = new String[w];
        for (int i = 0; i < w; i++) {
            columnNames[i] = "c" + i;
        }

        Index index = Index.forLabels(columnNames);

        Series<?>[] data = new Series[w];
        for (int i = 0; i < w; i++) {
            ValueMaker<?> vm = columnValueMakers[i];
            data[i] = vm.series(rows);
        }

        return DataFrame.newFrame(index).columns(data);
    }
}
