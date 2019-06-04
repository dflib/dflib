package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.IntSeries;
import com.nhl.dflib.Series;
import com.nhl.dflib.series.builder.IntAccumulator;
import com.nhl.dflib.series.builder.ObjectAccumulator;

public class DataGenerator {

    public static DataFrame columnarDFWithMixedData(int rows) {

        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        return columnarDF(rows,
                ValueMaker.intSeq(),
                ValueMaker.stringSeq(),
                ValueMaker.randomIntSeq(rows / 2),
                ValueMaker.constStringSeq(string));
    }

    public static IntSeries intSeries(int size, ValueMaker<Integer> maker) {

        IntAccumulator ints = new IntAccumulator(size);

        for (int i = 0; i < size; i++) {
            ints.add(maker.get());
        }

        return ints.toIntSeries();
    }

    public static DataFrame columnarDF(int rows, ValueMaker<?>... columnValueMakers) {

        int w = columnValueMakers.length;
        String[] columnNames = new String[w];
        for (int i = 0; i < w; i++) {
            columnNames[i] = "c" + i;
        }

        Index index = Index.forLabels(columnNames);

        Series<?>[] data = new Series[w];
        for (int i = 0; i < w; i++) {
            ObjectAccumulator ml = new ObjectAccumulator<>(rows);
            ValueMaker<?> vm = columnValueMakers[i];

            for (int j = 0; j < rows; j++) {
                ml.add(vm.get());
            }

            data[i] = ml.toSeries().materialize();
        }

        return DataFrame.forColumns(index, data);
    }
}
