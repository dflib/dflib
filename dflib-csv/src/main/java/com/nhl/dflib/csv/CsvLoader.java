package com.nhl.dflib.csv;

import com.nhl.dflib.DataFrame;
import com.nhl.dflib.Index;
import com.nhl.dflib.map.ValueMapper;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class CsvLoader {

    private int skipRows;
    private Index columns;
    private CSVFormat format;

    // storing converters as list to ensure predictable resolution order when the user supplies overlapping converters
    private List<Pair> converters;

    public CsvLoader() {
        this.format = CSVFormat.DEFAULT;
        this.converters = new ArrayList<>();
    }

    /**
     * Skips the specified number of rows. E.g. if the header is defined manually, you might call this method with "1"
     * as an argument.
     *
     * @param n number of rows to skip
     * @return this loader instance
     */
    public CsvLoader skipRows(int n) {
        this.skipRows = n;
        return this;
    }

    /**
     * Provides an alternative header to the returned DataFrame.
     *
     * @param columns user-defined DataFrame columns
     * @return this loader instance
     */
    public CsvLoader columns(String... columns) {
        this.columns = Index.withLabels(columns);
        return this;
    }

    public CsvLoader columnTypes(ValueMapper<String, ?>... typeConverters) {
        for (int i = 0; i < typeConverters.length; i++) {
            int captureI = i;
            converters.add(new Pair(ind -> captureI, typeConverters[i]));
        }
        return this;
    }


    public CsvLoader columnType(int column, ValueMapper<String, ?> typeConverter) {
        converters.add(new Pair(i -> column, typeConverter));
        return this;
    }

    public CsvLoader columnType(String column, ValueMapper<String, ?> typeConverter) {
        converters.add(new Pair(i -> i.position(column), typeConverter));
        return this;
    }

    /**
     * Optionally sets the style or format of the imported CSV. CSVFormat comes from "commons-csv" library and
     * contains a number of predefined formats, such as CSVFormat.MYSQL, etc. It also allows to customize the format
     * further, by defining custom delimiters, line separators, etc.
     *
     * @param format a format object defined in commons-csv library
     * @return this loader instance
     */
    public CsvLoader format(CSVFormat format) {
        this.format = format;
        return this;
    }

    public DataFrame load(File file) {
        try (Reader r = new FileReader(file)) {
            return load(r);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + file, e);
        }
    }

    public DataFrame load(String filePath) {
        try (Reader r = new FileReader(filePath)) {
            return load(r);
        } catch (IOException e) {
            throw new RuntimeException("Error reading file: " + filePath, e);
        }
    }

    public DataFrame load(Reader reader) {
        try  {
            Iterator<CSVRecord> it = format.parse(reader).iterator();

            rewind(it);
            Index columns = createColumns(it);

            if (!it.hasNext()) {
                return DataFrame.fromListOfRows(columns, Collections.emptyList());
            }

            ValueMapper<String, ?>[] converters = createConverters(columns);

            return new CsvLoaderWorker(columns, converters).load(it);
        } catch (IOException e) {
            throw new RuntimeException("Error reading CSV", e);
        }
    }

    private void rewind(Iterator<CSVRecord> it) {
        for (int i = 0; i < skipRows && it.hasNext(); i++) {
            it.next();
        }
    }

    private Index createColumns(Iterator<CSVRecord> it) {
        if (it.hasNext()) {
            return columns != null ? columns : loadColumns(it.next());
        } else {
            return columns != null ? columns : Index.withLabels();
        }
    }

    private Index loadColumns(CSVRecord header) {

        int width = header.size();
        String[] columnNames = new String[width];
        for (int i = 0; i < width; i++) {
            columnNames[i] = header.get(i);
        }

        return Index.withLabels(columnNames);
    }

    private ValueMapper<String, ?>[] createConverters(Index columns) {

        ValueMapper<String, ?>[] converters = new ValueMapper[columns.size()];

        // there may be overlapping pairs... the last one wins
        for (Pair p : this.converters) {
            converters[p.positionResolver.apply(columns)] = p.converter;
        }

        return converters;
    }

    private class Pair {
        Function<Index, Integer> positionResolver;
        ValueMapper<String, ?> converter;

        Pair(Function<Index, Integer> positionResolver, ValueMapper<String, ?> converter) {
            this.positionResolver = positionResolver;
            this.converter = converter;
        }
    }
}
