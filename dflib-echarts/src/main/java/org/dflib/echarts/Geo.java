package org.dflib.echarts;

import org.dflib.echarts.render.option.geo.GeoCoord;
import org.dflib.echarts.render.option.geo.GeoCoordsModel;
import org.dflib.echarts.render.option.geo.GeoModel;

/**
 * @since 2.0.0
 */
public class Geo {

    private final String map;
    private Boolean show;
    private Integer zoom;

    // TODO: "roam" has more options than just true and false
    private Boolean roam;

    private GeoCoord centerLat;
    private GeoCoord centerLon;
    private Double aspectScale;
    private Label label;

    public static Geo of(String map) {
        return new Geo(map);
    }

    private Geo(String map) {
        this.map = map;
    }

    public Geo show(boolean show) {
        this.show = show;
        return this;
    }

    public Geo zoom(int zoom) {
        this.zoom = zoom;
        return this;
    }

    public Geo roam(boolean roam) {
        this.roam = roam;
        return this;
    }

    public Geo centerLat(double lat) {
        this.centerLat = GeoCoord.of(lat);
        return this;
    }

    public Geo centerLatPct(double latPct) {
        this.centerLat = GeoCoord.ofPct(latPct);
        return this;
    }

    public Geo centerLon(double lon) {
        this.centerLon = GeoCoord.of(lon);
        return this;
    }

    public Geo centerLonPct(double lonPct) {
        this.centerLon = GeoCoord.ofPct(lonPct);
        return this;
    }

    public Geo aspectScale(double aspectScale) {
        this.aspectScale = aspectScale;
        return this;
    }

    public Geo label(Label label) {
        this.label = label;
        return this;
    }

    protected GeoModel resolve() {

        return new GeoModel(
                show,
                map,
                centerLat != null && centerLon != null ? new GeoCoordsModel(centerLat.asString(), centerLon.asString()) : null,
                zoom,
                roam,
                aspectScale,
                label != null ? label.resolve(null) : null
        );
    }
}
