package org.dflib.echarts;

import org.dflib.Index;

import java.util.Objects;

class OptionSeriesBuilder {
    final SeriesOpts<?> opts;
    final Index dataColumns;
    String name;

    OptionSeriesBuilder(SeriesOpts<?> opts, Index dataColumns) {
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
}
