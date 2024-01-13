package org.dflib.map;

import org.dflib.ColumnDataFrame;
import org.dflib.DataFrame;
import org.dflib.Index;
import org.dflib.RowMapper;
import org.dflib.Series;
import org.dflib.ValueToRowMapper;
import org.dflib.row.MultiArrayRowBuilder;

public class Mapper {

    /**
     * @deprecated no longer in use
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    public static DataFrame map(DataFrame source, Index resultColumns, RowMapper mapper) {
        return new ColumnDataFrame(null, resultColumns, mapData(source, resultColumns, mapper));
    }

    /**
     * @since 0.7
     */
    public static <T> DataFrame map(Series<T> source, Index resultColumns, ValueToRowMapper<T> mapper) {
        return new ColumnDataFrame(null, resultColumns, mapData(source, resultColumns, mapper));
    }

    /**
     * @deprecated no longer in use
     */
    @Deprecated(since = "1.0.0-M19", forRemoval = true)
    static Series<?>[] mapData(DataFrame source, Index resultColumns, RowMapper mapper) {

        MultiArrayRowBuilder rowBuilder = new MultiArrayRowBuilder(resultColumns, source.height());

        source.forEach(from -> {
            rowBuilder.next();
            mapper.map(from, rowBuilder);
        });

        return rowBuilder.getData();
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
