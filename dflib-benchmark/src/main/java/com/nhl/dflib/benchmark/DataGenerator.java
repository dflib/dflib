package com.nhl.dflib.benchmark;

import com.nhl.dflib.*;
import com.nhl.dflib.accumulator.DoubleAccumulator;
import com.nhl.dflib.accumulator.IntAccumulator;

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

        return ints.toSeries();
    }

    public static DoubleSeries doubleSeries(int size, ValueMaker<Double> maker) {

        DoubleAccumulator ds = new DoubleAccumulator(size);

        for (int i = 0; i < size; i++) {
            ds.add(maker.get());
        }

        return ds.toSeries();
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
            ValueMaker<?> vm = columnValueMakers[i];
            data[i] = vm.series(rows);
        }

        return DataFrame.newFrame(index).columns(data);
    }
}
