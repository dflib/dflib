package org.dflib.csv;

import org.dflib.Index;

@FunctionalInterface
interface CsvColumnMapFactory {

    static CsvColumnMapFactory all() {
        return CsvColumnMapFactory::createForAll;
    }

    static CsvColumnMapFactory ofCols(int... columns) {
        return ch -> CsvColumnMapFactory.createForPositions(ch, columns);
    }

    static CsvColumnMapFactory ofCols(String... columns) {
        return ch -> CsvColumnMapFactory.createForLabels(ch, columns);
    }

    static CsvColumnMapFactory ofColsExcept(int... columns) {
        return ch -> CsvColumnMapFactory.createForPositionsExcept(ch, columns);
    }

    static CsvColumnMapFactory ofColsExcept(String... columns) {
        return ch -> CsvColumnMapFactory.createForLabelsExcept(ch, columns);
    }

    CsvColumnMap create(Index csvHeader);

    private static CsvColumnMap createForAll(Index csvHeader) {
        int w = csvHeader.size();
        int[] positions = new int[w];
        for (int i = 0; i < w; i++) {
            positions[i] = i;
        }

        return new CsvColumnMap(csvHeader, csvHeader, positions);
    }

    private static CsvColumnMap createForPositions(Index csvHeader, int[] columns) {
        int w = columns.length;

        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = csvHeader.getLabel(columns[i]);
        }

        Index dfHeader = Index.of(labels);
        return new CsvColumnMap(csvHeader, dfHeader, columns);
    }

    private static CsvColumnMap createForLabels(Index csvHeader, String[] columns) {
        int w = columns.length;

        int[] positions = new int[w];

        for (int i = 0; i < w; i++) {
            // this will throw if the label is invalid, which is exactly what we want
            positions[i] = csvHeader.position(columns[i]);
        }

        Index dfHeader = Index.of(columns);
        return new CsvColumnMap(csvHeader, dfHeader, positions);
    }

    private static CsvColumnMap createForPositionsExcept(Index csvHeader, int[] columns) {
        return createForPositions(csvHeader, csvHeader.positionsExcept(columns));

    }

    private static CsvColumnMap createForLabelsExcept(Index csvHeader, String[] columns) {
        return createForPositions(csvHeader, csvHeader.positionsExcept(columns));
    }
}
