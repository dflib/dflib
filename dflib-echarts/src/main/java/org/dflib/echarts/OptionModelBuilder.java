package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class OptionModelBuilder {

    private final Option opt;
    private final DataFrame dataFrame;

    OptionModelBuilder(Option opt, DataFrame dataFrame) {
        this.opt = Objects.requireNonNull(opt);
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    OptionModel resolve() {

        DatasetBuilder dsb = DatasetBuilder.of(opt, dataFrame);
        List<SeriesModel> series = SeriesModelBuilders.of(opt, dsb).build();

        return new OptionModel(
                dsb.datasetModel(),
                opt.legend != null ? opt.legend.resolve() : null,
                grids(),
                series,
                opt.title != null ? opt.title.resolve() : null,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                opt.xAxes != null ? opt.xAxes.stream().map(XAxisBuilder::getAxis).map(XAxis::resolve).collect(Collectors.toList()) : null,
                opt.yAxes != null ? opt.yAxes.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<GridModel> grids() {
        return opt.grids != null
                ? opt.grids.stream().map(Grid::resolve).collect(Collectors.toList())
                : null;
    }
}
