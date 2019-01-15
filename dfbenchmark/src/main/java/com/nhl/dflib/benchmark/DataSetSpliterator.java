package com.nhl.dflib.benchmark;

import java.util.Random;
import java.util.Spliterators;
import java.util.function.Consumer;

class DataSetSpliterator extends Spliterators.AbstractSpliterator<Object> {

    private long size;
    private long index;
    private Random random = new Random();

    DataSetSpliterator(long size) {
        super(size, 0);
        this.size = size;
    }

    @Override
    public boolean tryAdvance(Consumer<? super Object> action) {
        int rowIndex = (int) (index / 4);

        if (rowIndex > size - 1) {
            return false;
        }

        int column = (int) (index % 4);
        switch (column) {
            case 0:
                action.accept(rowIndex);
                break;
            case 1:
                action.accept("data_" + rowIndex);
                break;
            case 2:
                action.accept(random.nextInt((int)(size / 2)));
                break;
            case 3:
                action.accept("Lorem ipsum dolor sit amet, consectetur adipiscing elit. Duis vulputate sollicitudin ligula sit amet ornare.");
                break;
        }

        index++;
        return true;
    }
}
