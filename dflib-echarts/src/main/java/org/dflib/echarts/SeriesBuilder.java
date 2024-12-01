package org.dflib.echarts;

import org.dflib.Index;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

class SeriesBuilder<T extends SeriesOpts<T>> {

    final T seriesOpts;
    final Index dataColumns;
    String name;

    // dimensions are references to row numbers in the dataset
    Integer xDimension;
    List<Integer> yDimensions;
    // TODO: tooltip dimension

    Integer pieLabelsDimension;

    String datasetSeriesLayoutBy;

    SeriesBuilder(T seriesOpts, Index dataColumns) {
        this.seriesOpts = Objects.requireNonNull(seriesOpts);
        this.dataColumns = Objects.requireNonNull(dataColumns);
        this.name = defaultName(seriesOpts, dataColumns);
    }

    private String defaultName(SeriesOpts<?> opts, Index dataColumns) {

        if (opts.name != null) {
            return opts.name;
        }

        int len = dataColumns != null ? dataColumns.size() : 0;
        if (len == 1) {
            return dataColumns.get(0);
        } else {
            // either "0" or ">1"
            return opts.getType().name();
        }
    }

    SeriesBuilder name(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    SeriesBuilder xDimension(int dim) {
        this.xDimension = dim;
        return this;
    }

    SeriesBuilder yDimension(int dim) {
        if (this.yDimensions == null) {
            this.yDimensions = new ArrayList<>();
        }

        this.yDimensions.add(dim);
        return this;
    }

    SeriesBuilder pieLabelsDimension(int dim) {
        this.pieLabelsDimension = dim;
        return this;
    }


    SeriesBuilder datasetSeriesLayoutBy(String layout) {
        this.datasetSeriesLayoutBy = layout;
        return this;
    }
}
