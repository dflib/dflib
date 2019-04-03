package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.ValueMapper;
import org.apache.commons.csv.CSVRecord;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

class CsvLoaderWorker {

    private Index columns;
    private ValueMapper<String, ?>[] converters;

    public CsvLoaderWorker(Index columns, ValueMapper<String, ?>[] converters) {
        this.columns = columns;
        this.converters = converters;
    }

    DataFrame load(Iterator<CSVRecord> it) {

        List<Object[]> rows = new ArrayList<>();

        while (it.hasNext()) {
            rows.add(loadRow(columns, it.next()));
        }

        return DataFrame.forRows(columns, rows);
    }

    private Object[] loadRow(Index columns, CSVRecord record) {
        int width = columns.size();

        Object[] row = new Object[width];

        for (int i = 0; i < width; i++) {
            String v = record.get(i);
            row[i] = (converters[i] != null) ? converters[i].map(v) : v;
        }

        return row;
    }

}
