package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Supplier;

public class CsvReader {

    private CSVFormat format;
    private Supplier<Reader> readerSupplier;

    protected CsvReader(CSVFormat format, Supplier<Reader> readerSupplier) {
        this.format = Objects.requireNonNull(format);
        this.readerSupplier = readerSupplier;
    }

    public DataFrame load() {
        Reader in = readerSupplier.get();
        try (CSVParser parser = format.parse(in);) {
            return doLoad(parser);
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private DataFrame doLoad(CSVParser parser) {
        Iterator<CSVRecord> it = parser.iterator();

        if (!it.hasNext()) {
            return DataFrame.fromRows(Index.withNames());
        }

        Index columns = loadIndex(it.next());

        List<Object[]> rows = new ArrayList<>();

        while (it.hasNext()) {
            rows.add(loadRow(columns, it.next()));
        }

        return DataFrame.fromRowsList(columns, rows);
    }

    private Index loadIndex(CSVRecord header) {

        int width = header.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = header.get(i);
        }

        return Index.withNames(columnNames);
    }

    private Object[] loadRow(Index columns, CSVRecord record) {
        int width = columns.size();

        Object[] row = new Object[width];

        for (int i = 0; i < width; i++) {
            row[i] = record.get(i);
        }

        return row;
    }

}
