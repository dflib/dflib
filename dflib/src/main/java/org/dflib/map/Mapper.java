package org.dflib.map;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.Series;
import org.dflib.ValueToRowMapper;
import org.dflib.row.MultiArrayRowBuilder;

public class Mapper {


    public static <T> DataFrame map(Series<T> source, Index resultColumns, ValueToRowMapper<T> mapper) {
        return new ColumnDataFrame(null, resultColumns, mapData(source, resultColumns, mapper));
    }

    static <T> Series<?>[] mapData(Series<T> source, Index resultColumns, ValueToRowMapper<T> mapper) {
        MultiArrayRowBuilder rowBuilder = new MultiArrayRowBuilder(resultColumns, source.size());

        source.forEach(from -> {
            rowBuilder.next();
            mapper.map(from, rowBuilder);
        });

        return rowBuilder.getData();
    }
}
