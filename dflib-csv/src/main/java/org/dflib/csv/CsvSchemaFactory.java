package org.dflib.csv;

import org.dflib.Index;

@FunctionalInterface
interface CsvSchemaFactory {

    CsvSchema schema(Index csvHeader);

    static CsvSchemaFactory all() {
        return CsvSchemaFactory::allMap;
    }

    static CsvSchemaFactory ofCols(int... columns) {
        return ch -> CsvSchemaFactory.positions(ch, columns);
    }

    static CsvSchemaFactory ofCols(String... columns) {
        return ch -> CsvSchemaFactory.labels(ch, columns);
    }

    static CsvSchemaFactory ofColsExcept(int... columns) {
        return ch -> CsvSchemaFactory.positionsExcept(ch, columns);
    }

    static CsvSchemaFactory ofColsExcept(String... columns) {
        return ch -> CsvSchemaFactory.labelsExcept(ch, columns);
    }

    private static CsvSchema allMap(Index csvHeader) {
        int w = csvHeader.size();
        int[] positions = new int[w];
        for (int i = 0; i < w; i++) {
            positions[i] = i;
        }

        return new CsvSchema(csvHeader, csvHeader, positions);
    }

    private static CsvSchema positions(Index csvHeader, int[] columns) {
        int w = columns.length;

        String[] labels = new String[w];

        for (int i = 0; i < w; i++) {
            labels[i] = csvHeader.get(columns[i]);
        }

        Index dfHeader = Index.of(labels);
        return new CsvSchema(csvHeader, dfHeader, columns);
    }

    private static CsvSchema labels(Index csvHeader, String[] columns) {
        int w = columns.length;

        int[] positions = new int[w];

        for (int i = 0; i < w; i++) {
            // this will throw if the label is invalid, which is exactly what we want
            positions[i] = csvHeader.position(columns[i]);
        }

        Index dfHeader = Index.of(columns);
        return new CsvSchema(csvHeader, dfHeader, positions);
    }

    private static CsvSchema positionsExcept(Index csvHeader, int[] columns) {
        return positions(csvHeader, csvHeader.positionsExcept(columns));
    }

    private static CsvSchema labelsExcept(Index csvHeader, String[] columns) {
        return positions(csvHeader, csvHeader.positionsExcept(columns));
    }
}
