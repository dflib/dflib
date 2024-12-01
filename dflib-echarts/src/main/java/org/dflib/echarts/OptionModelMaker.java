package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.option.GridModel;
import org.dflib.echarts.render.option.SeriesModel;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

class OptionModelMaker {

    private final Option opt;
    private final DataFrame dataFrame;

    OptionModelMaker(Option opt, DataFrame dataFrame) {
        this.opt = Objects.requireNonNull(opt);
        this.dataFrame = Objects.requireNonNull(dataFrame);
    }

    OptionModel resolve() {

        boolean cartesianDefaults = useCartesianDefaults(opt.seriesOpts);

        List<XAxisBuilder> xs = opt.xAxes != null
                ? opt.xAxes
                : (cartesianDefaults ? List.of(new XAxisBuilder(null, XAxis.ofDefault())) : null);
        List<YAxis> ys = opt.yAxes != null
                ? opt.yAxes
                : (cartesianDefaults ? List.of(YAxis.ofDefault()) : null);

        DatasetBuilder dsb = DatasetBuilder.of(dataFrame, opt, xs);
        List<SeriesModel> series = SeriesModelBuilders.of(opt, dsb).build();

        return new OptionModel(
                dsb.datasetModel(),
                opt.legend != null ? opt.legend.resolve() : null,
                grids(),
                series,
                opt.title != null ? opt.title.resolve() : null,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                xs != null ? xs.stream().map(XAxisBuilder::getAxis).map(XAxis::resolve).collect(Collectors.toList()) : null,
                ys != null ? ys.stream().map(YAxis::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<GridModel> grids() {
        return opt.grids != null
                ? opt.grids.stream().map(Grid::resolve).collect(Collectors.toList())
                : null;
    }

    private boolean useCartesianDefaults(List<SeriesOpts<?>> series) {
        return series.isEmpty()
                || series.stream().anyMatch(s -> s.getCoordinateSystemType().isCartesian());
    }
}
