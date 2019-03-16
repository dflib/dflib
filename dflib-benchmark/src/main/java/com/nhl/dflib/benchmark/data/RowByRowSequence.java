package com.nhl.dflib.benchmark.data;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RowByRowSequence extends Spliterators.AbstractSpliterator<Object> {

    private int width;
    private int height;
    private Supplier<Object>[] columnValueMakers;

    private int index;

    protected RowByRowSequence(Supplier<Object>[] columnValueMakers, int height) {
        super(height, 0);
        this.columnValueMakers = columnValueMakers;
        this.height = height;
        this.width = columnValueMakers.length;
    }

    public static DataFrame dfWithMixedData(int rows) {

        String string =
                "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.";

        return df(rows,
                ValueMakers.intSequence(),
                ValueMakers.stringSequence(),
                ValueMakers.randomIntSequence(rows / 2),
                ValueMakers.constStringSequence(string));
    }

    public static DataFrame df(int rows, Supplier<Object>... columnValueMakers) {

        String[] columnNames = new String[columnValueMakers.length];
        for (int i = 0; i < columnValueMakers.length; i++) {
            columnNames[i] = "c" + i;
        }

        Index index = Index.withNames(columnNames);
        return DataFrame.fromStream(index, dataStream(rows, columnValueMakers));
    }

    private static Stream<Object> dataStream(int rows, Supplier<Object>... columnValueMakers) {
        return StreamSupport.stream(new RowByRowSequence(columnValueMakers, rows), false);
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
