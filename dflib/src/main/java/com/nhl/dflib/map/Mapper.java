package com.nhl.dflib.map;

import com.nhl.dflib.ColumnDataFrame;
import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.RowMapper;
import com.nhl.dflib.Series;
import com.nhl.dflib.ValueToRowMapper;

public class Mapper {

    public static DataFrame map(DataFrame source, Index resultColumns, RowMapper mapper) {
        return new ColumnDataFrame(resultColumns, mapData(source, resultColumns, mapper));
    }

    /**
     * @since 0.7
     */
    public static <T> DataFrame map(Series<T> source, Index resultColumns, ValueToRowMapper<T> mapper) {
        return new ColumnDataFrame(resultColumns, mapData(source, resultColumns, mapper));
    }

    private static Series<?>[] mapData(DataFrame source, Index resultColumns, RowMapper mapper) {

        MultiArrayRowBuilder rowBuilder = new MultiArrayRowBuilder(resultColumns, source.height());

        source.forEach(from -> {
            mapper.map(from, rowBuilder);
            rowBuilder.reset();
        });

        return rowBuilder.getData();
    }

    private static <T> Series<?>[] mapData(Series<T> source, Index resultColumns, ValueToRowMapper<T> mapper) {
        MultiArrayRowBuilder rowBuilder = new MultiArrayRowBuilder(resultColumns, source.size());

        source.forEach(from -> {
            mapper.map(from, rowBuilder);
            rowBuilder.reset();
        });

        return rowBuilder.getData();
    }

}
