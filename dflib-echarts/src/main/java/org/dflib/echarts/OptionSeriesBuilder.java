package org.dflib.echarts;

import org.dflib.Index;

import java.util.List;
import java.util.Objects;

class OptionSeriesBuilder<T extends SeriesOpts<T>> {

    final T opts;
    final Index dataColumns;
    String name;

    // dimensions are references to row numbers in the dataset
    Integer xDimension;
    List<Integer> yDimensions;
    // TODO: tooltip dimension

    Integer pieLabelsDimension;

    String datasetSeriesLayoutBy;

    OptionSeriesBuilder(T opts, Index dataColumns) {
        this.opts = Objects.requireNonNull(opts);
        this.dataColumns = Objects.requireNonNull(dataColumns);
        this.name = defaultName(opts, dataColumns);
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

    OptionSeriesBuilder name(String name) {
        this.name = Objects.requireNonNull(name);
        return this;
    }

    OptionSeriesBuilder xDimension(int dim) {
        this.xDimension = dim;
        return this;
    }

    OptionSeriesBuilder yDimensions(List<Integer> dims) {
        this.yDimensions = Objects.requireNonNull(dims);
        return this;
    }

    OptionSeriesBuilder pieLabelsDimension(int dim) {
        this.pieLabelsDimension = dim;
        return this;
    }


    OptionSeriesBuilder datasetSeriesLayoutBy(String layout) {
        this.datasetSeriesLayoutBy = layout;
        return this;
    }
}
