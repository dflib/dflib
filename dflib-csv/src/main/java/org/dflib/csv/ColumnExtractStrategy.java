package org.dflib.csv;

import org.dflib.Index;

@FunctionalInterface
interface ColumnExtractStrategy {

    CsvColumnMap columnMap(Index csvHeader);

    static ColumnExtractStrategy all() {
        return ColumnExtractStrategy::allMap;
    }

    static ColumnExtractStrategy ofCols(int... columns) {
        return ch -> ColumnExtractStrategy.positions(ch, columns);
    }

    static ColumnExtractStrategy ofCols(String... columns) {
        return ch -> ColumnExtractStrategy.labels(ch, columns);
    }

    static ColumnExtractStrategy ofColsExcept(int... columns) {
        return ch -> ColumnExtractStrategy.positionsExcept(ch, columns);
    }

    static ColumnExtractStrategy ofColsExcept(String... columns) {
        return ch -> ColumnExtractStrategy.labelsExcept(ch, columns);
    }

    private static CsvColumnMap allMap(Index csvHeader) {
        int w = csvHeader.size();
        int[] positions = new int[w];
        for (int i = 0; i < w; i++) {
            positions[i] = i;
        }

        return new CsvColumnMap(csvHeader, csvHeader, positions);
    }

    private static CsvColumnMap positions(Index csvHeader, int[] columns) {
        int w = columns.length;

        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = csvHeader.getLabel(columns[i]);
        }

        Index dfHeader = Index.of(labels);
        return new CsvColumnMap(csvHeader, dfHeader, columns);
    }

    private static CsvColumnMap labels(Index csvHeader, String[] columns) {
        int w = columns.length;

        int[] positions = new int[w];

        for (int i = 0; i < w; i++) {
            // this will throw if the label is invalid, which is exactly what we want
            positions[i] = csvHeader.position(columns[i]);
        }

        Index dfHeader = Index.of(columns);
        return new CsvColumnMap(csvHeader, dfHeader, positions);
    }

    private static CsvColumnMap positionsExcept(Index csvHeader, int[] columns) {
        return positions(csvHeader, csvHeader.positionsExcept(columns));

    }

    private static CsvColumnMap labelsExcept(Index csvHeader, String[] columns) {
        return positions(csvHeader, csvHeader.positionsExcept(columns));
    }
}
