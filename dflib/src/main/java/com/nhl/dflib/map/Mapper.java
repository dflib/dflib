package com.nhl.dflib.map;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.Series;
import com.nhl.dflib.ColumnDataFrame;

public class Mapper {

    public static DataFrame map(DataFrame source, Index mappedColumns, RowMapper mapper) {
        return new ColumnDataFrame(mappedColumns, mapData(source, mappedColumns, mapper));
    }

    private static Series<?>[] mapData(DataFrame source, Index mappedColumns, RowMapper mapper) {

        MultiArrayRowBuilder rowBuilder = new MultiArrayRowBuilder(mappedColumns, source.height());

        source.forEach(from -> {
            mapper.map(from, rowBuilder);
            rowBuilder.reset();
        });

        return rowBuilder.getData();
    }
}
