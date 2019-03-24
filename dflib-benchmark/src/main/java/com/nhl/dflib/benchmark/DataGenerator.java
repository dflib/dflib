package com.nhl.dflib.benchmark;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.column.ColumnDataFrame;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class DataGenerator extends Spliterators.AbstractSpliterator<Object> {

    private int width;
    private int height;
    private ValueMaker<?>[] columnValueMakers;

    private int index;

    protected DataGenerator(ValueMaker<?>[] columnValueMakers, int height) {
        super(height, 0);
        this.columnValueMakers = columnValueMakers;
        this.height = height;
        this.width = columnValueMakers.length;
    }

    public static DataFrame columnarDFWithMixedData(int rows) {

        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        return columnarDF(rows,
                ValueMaker.intSeq(),
                ValueMaker.stringSeq(),
                ValueMaker.randomIntSeq(rows / 2),
                ValueMaker.constStringSeq(string));
    }

    public static DataFrame columnarDF(int rows, ValueMaker<?>... columnValueMakers) {

        String[] columnNames = new String[columnValueMakers.length];
        for (int i = 0; i < columnValueMakers.length; i++) {
            columnNames[i] = "c" + i;
        }

        // TODO: fold by column - much faster for columnar DF
        Index index = Index.withNames(columnNames);
        return ColumnDataFrame.fromStreamFoldByRow(index, dataStream(rows, columnValueMakers));
    }

    private static Stream<Object> dataStream(int rows, ValueMaker<?>... columnValueMakers) {
        return StreamSupport.stream(new DataGenerator(columnValueMakers, rows), false);
    }

    @Override
    public boolean tryAdvance(Consumer<? super Object> action) {
        return index / width < height ? advance(action) : false;
    }

    private boolean advance(Consumer<? super Object> action) {
        action.accept(columnValueMakers[index % width].get());
        index++;
        return true;
    }
}
