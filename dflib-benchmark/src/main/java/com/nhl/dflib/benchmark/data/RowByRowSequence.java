package com.nhl.dflib.benchmark.data;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.columnar.ColumnarDataFrame;

import java.util.Spliterators;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class RowByRowSequence extends Spliterators.AbstractSpliterator<Object> {

    private int width;
    private int height;
    private ValueMaker<?>[] columnValueMakers;

    private int index;

    protected RowByRowSequence(ValueMaker<?>[] columnValueMakers, int height) {
        super(height, 0);
        this.columnValueMakers = columnValueMakers;
        this.height = height;
        this.width = columnValueMakers.length;
    }

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

        String[] columnNames = new String[columnValueMakers.length];
        for (int i = 0; i < columnValueMakers.length; i++) {
            columnNames[i] = "c" + i;
        }

        Index index = Index.withNames(columnNames);
        return DataFrame.fromStreamFoldByRow(index, dataStream(rows, columnValueMakers));
    }

    public static DataFrame columnarDf(int rows, ValueMaker<?>... columnValueMakers) {

        String[] columnNames = new String[columnValueMakers.length];
        for (int i = 0; i < columnValueMakers.length; i++) {
            columnNames[i] = "c" + i;
        }

        Index index = Index.withNames(columnNames);

        // TODO: use per column streams for columnar DF
        return ColumnarDataFrame.fromRowStream(index, dataStream(rows, columnValueMakers));
    }

    private static Stream<Object> dataStream(int rows, ValueMaker<?>... columnValueMakers) {
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
