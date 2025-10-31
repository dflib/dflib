package org.dflib.echarts;

import org.dflib.DataFrame;
import org.dflib.echarts.render.OptionModel;
import org.dflib.echarts.render.option.GridModel;

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
        SeriesModelBuilders sb = SeriesModelBuilders.of(opt, dataFrame, dsb);

        return new OptionModel(
                dsb != null ? dsb.resolve() : null,
                opt.legend != null ? opt.legend.resolve() : null,
                grids(),
                sb.resolve(),
                opt.title != null ? opt.title.resolve() : null,
                opt.toolbox != null ? opt.toolbox.resolve() : null,
                opt.tooltip != null ? opt.tooltip.resolve() : null,
                opt.xAxes != null ? opt.xAxes.stream().map(XAxisBuilder::getAxis).map(XAxis::resolve).collect(Collectors.toList()) : null,
                opt.yAxes != null ? opt.yAxes.stream().map(YAxis::resolve).collect(Collectors.toList()) : null,
                opt.singleAxes != null ? opt.singleAxes.stream().map(SingleAxisBuilder::getAxis).map(SingleAxis::resolve).collect(Collectors.toList()) : null,
                opt.calendars != null ? opt.calendars.stream().map(CalendarCoordsBuilder::getCalendar).map(CalendarCoords::resolve).collect(Collectors.toList()) : null,
                opt.visualMaps != null ? opt.visualMaps.stream().map(VisualMap::resolve).collect(Collectors.toList()) : null
        );
    }

    private List<GridModel> grids() {
        return opt.grids != null
                ? opt.grids.stream().map(Grid::resolve).collect(Collectors.toList())
                : null;
    }
}
