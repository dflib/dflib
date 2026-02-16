package org.dflib.echarts.render.option.geo;

/**
 * @since 2.0.0
 */
public record GeoModel(
        Boolean show,
        String map,
        GeoCoordsModel center,
        Integer zoom,
        Boolean roam,
        Double aspectScale
) {
    public boolean isShowPresent() {
        return show != null;
    }

    public boolean isRoamPresent() {
        return roam != null;
    }
}
